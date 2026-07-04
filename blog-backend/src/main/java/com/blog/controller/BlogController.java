package com.blog.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.common.PageResult;
import com.blog.common.Result;
import com.blog.dto.ArticleQueryDTO;
import com.blog.dto.CommentDTO;
import com.blog.dto.CommentQueryDTO;
import com.blog.dto.LoginDTO;
import com.blog.dto.RegisterDTO;
import com.blog.dto.UserInfoDTO;
import com.blog.entity.Article;
import com.blog.entity.Category;
import com.blog.entity.Comment;
import com.blog.entity.Tag;
import com.blog.entity.User;
import com.blog.mapper.ArticleMapper;
import com.blog.mapper.CommentMapper;
import com.blog.service.ArticleLikeService;
import com.blog.service.ArticleService;
import com.blog.service.CategoryService;
import com.blog.service.CommentLikeService;
import com.blog.service.CommentService;
import com.blog.service.TagService;
import com.blog.service.UserService;
import com.blog.utils.JwtUtil;
import com.blog.vo.LoginVO;
import com.blog.vo.ProfileVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 前台接口控制器
 *
 * @author blog
 * @since 2024-06-07
 */
@RestController
@RequestMapping("/api/blog")
@CrossOrigin
public class BlogController {

    @Resource
    private ArticleService articleService;

    @Resource
    private CategoryService categoryService;

    @Resource
    private TagService tagService;

    @Resource
    private CommentService commentService;

    @Resource
    private UserService userService;

    @Resource
    private ArticleLikeService articleLikeService;

    @Resource
    private CommentLikeService commentLikeService;

    @Resource
    private JwtUtil jwtUtil;

    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private CommentMapper commentMapper;

    /** 首页文章分页列表 */
    @GetMapping("/article/list")
    public Result<PageResult<Article>> articleList(ArticleQueryDTO dto) {
        return Result.success(articleService.pageFront(dto));
    }

    /** 文章详情 - 浏览量 +1 */
    @GetMapping("/article/{id}")
    public Result<Article> articleDetail(@PathVariable Long id) {
        return Result.success(articleService.detail(id));
    }

    /** 全部分类 */
    @GetMapping("/category/list")
    public Result<List<Category>> categoryList() {
        return Result.success(categoryService.listAll());
    }

    /** 全部标签 */
    @GetMapping("/tag/list")
    public Result<List<Tag>> tagList() {
        return Result.success(tagService.listAll());
    }

    /** 评论列表 (按文章) */
    @GetMapping("/comment/list")
    public Result<PageResult<Comment>> commentList(CommentQueryDTO dto, HttpServletRequest request) {
        Long userId = null;
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            String username = jwtUtil.getUsernameFromToken(token);
            if (username != null) {
                User user = userService.getByUsername(username);
                if (user != null) {
                    userId = user.getId();
                }
            }
        }
        return Result.success(commentService.pageFrontWithTree(dto, userId));
    }

    /** 留言列表 */
    @GetMapping("/message/list")
    public Result<PageResult<Comment>> messageList(CommentQueryDTO dto, HttpServletRequest request) {
        dto.setOnlyMsg(true);
        Long userId = null;
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            String username = jwtUtil.getUsernameFromToken(token);
            if (username != null) {
                User user = userService.getByUsername(username);
                if (user != null) {
                    userId = user.getId();
                }
            }
        }
        return Result.success(commentService.pageFrontWithTree(dto, userId));
    }

    /** 提交评论/留言 - 默认进入待审核状态 */
    @PostMapping("/comment/submit")
    public Result<Void> submitComment(@RequestBody CommentDTO dto,
                                      HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return Result.error("请先登录");
        }
        token = token.substring(7);
        String username = jwtUtil.getUsernameFromToken(token);
        if (username == null) {
            return Result.error("请先登录");
        }
        User user = userService.getByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }
        dto.setNickname(user.getNickname());
        dto.setEmail(user.getEmail());
        String ip = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");
        commentService.submit(dto, ip, userAgent);
        return Result.success();
    }

    /** 个人简介页 - 使用轻量 count, 不再加载全量列表 */
    @GetMapping("/profile")
    public Result<ProfileVO> profile() {
        com.blog.entity.User admin = userService.getByUsername("admin");
        if (admin == null) {
            return Result.success(null);
        }
        ProfileVO vo = new ProfileVO();
        vo.setNickname(admin.getNickname());
        vo.setAvatar(admin.getAvatar());
        vo.setEmail(admin.getEmail());
        vo.setBio(admin.getBio());
        vo.setCreateTime(admin.getCreateTime());

        LambdaQueryWrapper<Article> articleWrapper = new LambdaQueryWrapper<>();
        articleWrapper.eq(Article::getStatus, 1);
        vo.setArticleCount((long) articleMapper.selectCount(articleWrapper));

        LambdaQueryWrapper<Comment> commentWrapper = new LambdaQueryWrapper<>();
        commentWrapper.eq(Comment::getStatus, 1);
        vo.setCommentCount((long) commentMapper.selectCount(commentWrapper));

        // 总浏览量 - 使用 MySQL IFNULL + SUM 的聚合查询
        long totalView = 0;
        List<Article> list = articleMapper.selectList(
                new LambdaQueryWrapper<Article>().eq(Article::getStatus, 1)
                        .select(Article::getViewCount));
        for (Article a : list) {
            if (a.getViewCount() != null) {
                totalView += a.getViewCount();
            }
        }
        vo.setTotalView(totalView);
        return Result.success(vo);
    }

    /** 用户注册 */
    @PostMapping("/user/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO dto) {
        userService.register(dto);
        return Result.success();
    }

    /** 用户登录 */
    @PostMapping("/user/login")
    public Result<LoginVO> userLogin(@Valid @RequestBody LoginDTO dto) {
        return Result.success(userService.userLogin(dto));
    }

    /** 获取当前登录用户信息 */
    @GetMapping("/user/info")
    public Result<User> getUserInfo(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            String username = jwtUtil.getUsernameFromToken(token);
            if (username != null) {
                User user = userService.getByUsername(username);
                if (user != null) {
                    user.setPassword(null);
                }
                return Result.success(user);
            }
        }
        return Result.success(null);
    }

    /** 更新用户信息 */
    @PostMapping("/user/update")
    public Result<Void> updateUserInfo(@Valid @RequestBody UserInfoDTO dto,
                                       HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            String username = jwtUtil.getUsernameFromToken(token);
            if (username != null) {
                User user = userService.getByUsername(username);
                if (user != null) {
                    userService.updateUserInfo(user.getId(), dto);
                }
            }
        }
        return Result.success();
    }

    /** 点赞/取消点赞 */
    @PostMapping("/article/like/{id}")
    public Result<Map<String, Object>> toggleLike(@PathVariable Long id,
                                                  HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            String username = jwtUtil.getUsernameFromToken(token);
            if (username != null) {
                User user = userService.getByUsername(username);
                if (user != null) {
                    articleLikeService.toggleLike(id, user.getId());
                    int likeCount = articleLikeService.getLikeCount(id);
                    boolean liked = articleLikeService.isLiked(id, user.getId());
                    Map<String, Object> result = new HashMap<>();
                    result.put("likeCount", likeCount);
                    result.put("liked", liked);
                    return Result.success(result);
                }
            }
        }
        return Result.error("请先登录");
    }

    /** 检查文章是否已点赞 */
    @GetMapping("/article/like/status/{id}")
    public Result<Map<String, Object>> getLikeStatus(@PathVariable Long id,
                                                     HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        boolean liked = false;
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            String username = jwtUtil.getUsernameFromToken(token);
            if (username != null) {
                User user = userService.getByUsername(username);
                if (user != null) {
                    liked = articleLikeService.isLiked(id, user.getId());
                }
            }
        }
        int likeCount = articleLikeService.getLikeCount(id);
        Map<String, Object> result = new HashMap<>();
        result.put("likeCount", likeCount);
        result.put("liked", liked);
        return Result.success(result);
    }

    /** 评论点赞/取消点赞 */
    @PostMapping("/comment/like/{id}")
    public Result<Map<String, Object>> toggleCommentLike(@PathVariable Long id,
                                                        HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            String username = jwtUtil.getUsernameFromToken(token);
            if (username != null) {
                User user = userService.getByUsername(username);
                if (user != null) {
                    return Result.success(commentLikeService.toggleLike(id, user.getId()));
                }
            }
        }
        return Result.error("请先登录");
    }

    /** 检查评论是否已点赞 */
    @GetMapping("/comment/like/status/{id}")
    public Result<Map<String, Object>> getCommentLikeStatus(@PathVariable Long id,
                                                           HttpServletRequest request) {
        Long userId = null;
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            String username = jwtUtil.getUsernameFromToken(token);
            if (username != null) {
                User user = userService.getByUsername(username);
                if (user != null) {
                    userId = user.getId();
                }
            }
        }
        return Result.success(commentLikeService.getLikeStatus(id, userId));
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
