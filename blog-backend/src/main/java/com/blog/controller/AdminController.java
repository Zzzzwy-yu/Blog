package com.blog.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.common.PageResult;
import com.blog.common.Result;
import com.blog.dto.*;
import com.blog.entity.Article;
import com.blog.entity.Category;
import com.blog.entity.Comment;
import com.blog.entity.Tag;
import com.blog.mapper.ArticleMapper;
import com.blog.mapper.CategoryMapper;
import com.blog.mapper.CommentMapper;
import com.blog.mapper.TagMapper;
import com.blog.service.*;
import com.blog.vo.DashboardVO;
import com.blog.vo.LoginVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 后台管理接口控制器
 *
 * @author blog
 * @since 2024-06-07
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class AdminController {

    @Resource
    private UserService userService;

    @Resource
    private ArticleService articleService;

    @Resource
    private CategoryService categoryService;

    @Resource
    private TagService tagService;

    @Resource
    private CommentService commentService;

    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private TagMapper tagMapper;

    @Resource
    private CommentMapper commentMapper;

    /** 登录 */
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        return Result.success(userService.login(dto));
    }

    /** 登出 */
    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success();
    }

    /** 获取当前用户信息 */
    @GetMapping("/info")
    public Result<com.blog.entity.User> info(HttpServletRequest request) {
        String username = (String) request.getAttribute("currentUser");
        if (username == null) {
            username = "admin";
        }
        com.blog.entity.User user = userService.getByUsername(username);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }

    /** 修改密码 */
    @PostMapping("/password")
    public Result<Void> changePassword(@Valid @RequestBody PasswordDTO dto,
                                       HttpServletRequest request) {
        String username = (String) request.getAttribute("currentUser");
        if (username == null) {
            username = "admin";
        }
        userService.changePassword(username, dto);
        return Result.success();
    }

    /** 数据看板 */
    @GetMapping("/dashboard")
    public Result<DashboardVO> dashboard() {
        DashboardVO vo = new DashboardVO();
        vo.setArticleCount(articleMapper.selectCount(null));
        vo.setCategoryCount(categoryMapper.selectCount(null));
        vo.setTagCount(tagMapper.selectCount(null));
        vo.setCommentCount(commentMapper.selectCount(null));

        LambdaQueryWrapper<Comment> pendingWrapper = new LambdaQueryWrapper<>();
        pendingWrapper.eq(Comment::getStatus, 0);
        vo.setPendingCommentCount(commentMapper.selectCount(pendingWrapper));

        // 今日发布
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LambdaQueryWrapper<Article> todayWrapper = new LambdaQueryWrapper<>();
        todayWrapper.ge(Article::getCreateTime, todayStart);
        vo.setTodayArticleCount(articleMapper.selectCount(todayWrapper));

        // 总浏览量 - 使用自定义SQL聚合(这里简单用内存累加+分页)
        long totalView = 0;
        List<Article> list = articleMapper.selectList(null);
        for (Article a : list) {
            if (a.getViewCount() != null) {
                totalView += a.getViewCount();
            }
        }
        vo.setTotalViewCount(totalView);

        return Result.success(vo);
    }

    // ============ 文章管理 ============
    @GetMapping("/article/list")
    public Result<PageResult<Article>> articleList(ArticleQueryDTO dto) {
        return Result.success(articleService.pageAdmin(dto));
    }

    @GetMapping("/article/{id}")
    public Result<Article> articleDetail(@PathVariable Long id) {
        Article article = articleMapper.selectById(id);
        if (article != null) {
            article.setTagList(articleMapper.selectTagsByArticleId(id));
        }
        return Result.success(article);
    }

    @PostMapping("/article/save")
    public Result<Void> articleSave(@Valid @RequestBody ArticleDTO dto) {
        articleService.saveArticle(dto);
        return Result.success();
    }

    @PostMapping("/article/update")
    public Result<Void> articleUpdate(@Valid @RequestBody ArticleDTO dto) {
        articleService.updateArticle(dto);
        return Result.success();
    }

    @PostMapping("/article/delete/{id}")
    public Result<Void> articleDelete(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return Result.success();
    }

    @PostMapping("/article/status/{id}/{status}")
    public Result<Void> articleStatus(@PathVariable Long id, @PathVariable Integer status) {
        articleService.switchStatus(id, status);
        return Result.success();
    }

    // ============ 分类管理 ============
    @GetMapping("/category/list")
    public Result<java.util.List<Category>> categoryList() {
        return Result.success(categoryService.listAll());
    }

    @PostMapping("/category/save")
    public Result<Void> categorySave(@Valid @RequestBody CategoryDTO dto) {
        categoryService.save(dto);
        return Result.success();
    }

    @PostMapping("/category/update")
    public Result<Void> categoryUpdate(@Valid @RequestBody CategoryDTO dto) {
        categoryService.update(dto);
        return Result.success();
    }

    @PostMapping("/category/delete/{id}")
    public Result<Void> categoryDelete(@PathVariable Long id) {
        categoryService.delete(id);
        return Result.success();
    }

    // ============ 标签管理 ============
    @GetMapping("/tag/list")
    public Result<java.util.List<Tag>> tagList() {
        return Result.success(tagService.listAll());
    }

    @PostMapping("/tag/save")
    public Result<Void> tagSave(@Valid @RequestBody TagDTO dto) {
        tagService.save(dto);
        return Result.success();
    }

    @PostMapping("/tag/update")
    public Result<Void> tagUpdate(@Valid @RequestBody TagDTO dto) {
        tagService.update(dto);
        return Result.success();
    }

    @PostMapping("/tag/delete/{id}")
    public Result<Void> tagDelete(@PathVariable Long id) {
        tagService.delete(id);
        return Result.success();
    }

    // ============ 留言/评论管理 ============
    @GetMapping("/comment/list")
    public Result<PageResult<Comment>> commentList(CommentQueryDTO dto) {
        return Result.success(commentService.pageAdmin(dto));
    }

    @PostMapping("/comment/approve/{id}")
    public Result<Void> commentApprove(@PathVariable Long id) {
        commentService.approve(id);
        return Result.success();
    }

    @PostMapping("/comment/reject/{id}")
    public Result<Void> commentReject(@PathVariable Long id) {
        commentService.reject(id);
        return Result.success();
    }

    @PostMapping("/comment/delete/{id}")
    public Result<Void> commentDelete(@PathVariable Long id) {
        commentService.delete(id);
        return Result.success();
    }
}
