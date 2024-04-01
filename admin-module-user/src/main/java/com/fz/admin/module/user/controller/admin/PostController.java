package com.fz.admin.module.user.controller.admin;


import com.fz.admin.framework.common.pojo.PageResult;
import com.fz.admin.framework.common.pojo.ServRespEntity;
import com.fz.admin.module.user.model.entity.SysPost;
import com.fz.admin.module.user.model.param.PostPageParam;
import com.fz.admin.module.user.service.SysPostService;
import com.fz.admin.module.user.model.param.PostCreateOrUpdateParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

import static com.fz.admin.framework.common.pojo.ServRespEntity.success;


@Tag(name = "岗位管理")
@AllArgsConstructor
@Validated
@RestController
@RequestMapping("/system/post")
public class PostController {


    private SysPostService postService;

    @PostMapping("/create")
    @Operation(summary = "创建岗位")
    // @PreAuthorize("@ss.hasPermission('system:post:create')")
    public ServRespEntity<Long> createPost(@Validated @RequestBody PostCreateOrUpdateParam param) {
        Long postId = postService.createPost(param);
        return success(postId);
    }

    @PutMapping("/update")
    @Operation(summary = "修改岗位")
    // @PreAuthorize("@ss.hasPermission('system:post:update')")
    public ServRespEntity<Boolean> updatePost(@Validated @RequestBody PostCreateOrUpdateParam param) {
        postService.updatePost(param);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除岗位")
    // @PreAuthorize("@ss.hasPermission('system:post:delete')")
    public ServRespEntity<Boolean> deletePost(@RequestParam("id") Long id) {
        postService.deletePostById(id);
        return success(true);
    }



    @DeleteMapping("/delete-batch")
    @Operation(summary = "批量删除岗位")
    @PreAuthorize("@ss.hasPermission('system:post:delete')")
    public ServRespEntity<Boolean> deletePosts(@NotEmpty(message = "id列表不能为空") @RequestBody List<Long> ids) {
        postService.deletePostByIds(ids);
        return success(true);
    }

    @GetMapping(value = "/get")
    @Operation(summary = "获得岗位信息")
    @Parameter(name = "id", description = "岗位编号", required = true, example = "1024")
    // @PreAuthorize("@ss.hasPermission('system:post:query')")
    public ServRespEntity<SysPost> getPost(@RequestParam("id") Long id) {
        SysPost post = postService.getPostById(id);
        return success(post);
    }

    @GetMapping(value = {"/list-all-simple", "simple-list"})
    @Operation(summary = "获取岗位全列表", description = "只包含被开启的岗位，主要用于前端的下拉选项")
    public ServRespEntity<List<SysPost>> getSimplePostList() {
        // 获得岗位列表，只要开启状态的
        List<SysPost> list = postService.getSimplePostList();
        // 排序后，返回给前端
        list.sort(Comparator.comparing(SysPost::getOrder));
        return success(list);
    }

    @GetMapping("/page")
    @Operation(summary = "获得岗位分页列表")
    // @PreAuthorize("@ss.hasPermission('system:post:query')")
    public ServRespEntity<PageResult<SysPost>> getPostPage(@Validated PostPageParam param) {
        PageResult<SysPost> pageResult = postService.getPostPage(param);
        return success(pageResult);
    }

    // @GetMapping("/export")
    // @Operation(summary = "岗位管理")
    // @PreAuthorize("@ss.hasPermission('system:post:export')")
    // @OperateLog(type = EXPORT)
    // public void export(HttpServletResponse response, @Validated PostPageReqVO reqVO) throws IOException {
    //     reqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
    //     List<PostDO> list = postService.getPostPage(reqVO).getList();
    //     // 输出
    //     ExcelUtils.write(response, "岗位数据.xls", "岗位列表", PostRespVO.class,
    //             BeanUtils.toBean(list, PostRespVO.class));
    // }

}
