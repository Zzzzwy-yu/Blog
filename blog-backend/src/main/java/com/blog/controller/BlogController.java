package com.blog.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.common.PageResult;
import com.blog.common.Result;
import com.blog.dto.ArticleQueryDTO;
import com.blog.dto.CommentDTO;
import com.blog.dto.CommentQueryDTO;
import com.blog.entity.Article;
import com.blog.entity.Category;
import com.blog.entity.Comment;
import com.blog.entity.Tag;
import com.blog.mapper.ArticleMapper;
import com.blog.mapper.CommentMapper;
import com.blog.service.ArticleService;
import com.blog.service.CategoryService;
import com.blog.service.CommentService;
import com.blog.service.TagService;
import com.blog.service.UserService;
import com.blog.vo.ProfileVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

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
    public Result<PageResult<Comment>> commentList(CommentQueryDTO dto) {
        return Result.success(commentService.pageFront(dto));
    }

    /** 留言列表 */
    @GetMapping("/message/list")
    public Result<PageResult<Comment>> messageList(CommentQueryDTO dto) {
        dto.setOnlyMsg(true);
        return Result.success(commentService.pageFront(dto));
    }

    /** 提交评论/留言 - 默认进入待审核状态 */
    @PostMapping("/comment/submit")
    public Result<Void> submitComment(@RequestBody CommentDTO dto,
                                      HttpServletRequest request) {
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
