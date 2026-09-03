package com.lesofn.archforge.demo.task.internal.web;

import com.lesofn.archforge.demo.task.api.dto.*;
import com.lesofn.archforge.demo.task.api.port.CurrentUserPort;
import com.lesofn.archforge.demo.task.internal.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import cn.dev33.satoken.annotation.SaCheckRole;
import org.springframework.web.bind.annotation.*;

/** Task management REST API */
@Slf4j
@Tag(name = "任务管理")
@SaCheckRole(value = "ADMIN", type = TaskController.STP_TYPE_ADMIN)
@RestController
@RequiredArgsConstructor
@RequestMapping("/task")
public class TaskController {

    /** 登录类型：管理端（原 StpAdminUtil.TYPE 常量，领域模块不再依赖 infrastructure） */
    public static final String STP_TYPE_ADMIN = "admin";

    private final TaskService taskService;
    private final CurrentUserPort currentUserPort;

    @Operation(summary = "获取任务列表")
    @PostMapping
    public TaskPageResponse<TaskDTO> list(@RequestBody @Valid TaskListRequest request) {
        return taskService.searchTasks(request);
    }

    @Operation(summary = "创建任务")
    @PostMapping("/create")
    public Long create(@RequestBody @Valid TaskCreateRequest request) {
        return taskService.createTask(
                request,
                request.getUid() != null ? request.getUid() : currentUserPort.getCurrentUid());
    }

    @Operation(summary = "更新任务")
    @PutMapping("/update")
    public Boolean update(@RequestBody @Valid TaskUpdateRequest request) {
        return taskService.updateTask(request);
    }

    @Operation(summary = "删除任务")
    @PostMapping("/delete")
    public Boolean delete(@RequestBody @Valid TaskDeleteRequest request) {
        return taskService.deleteTask(request.getId());
    }

    @Operation(summary = "开始任务")
    @PostMapping("/start")
    public Boolean start(@RequestBody @Valid TaskActionRequest request) {
        return taskService.startTask(request.getId());
    }

    @Operation(summary = "完成任务")
    @PostMapping("/complete")
    public Boolean complete(@RequestBody @Valid TaskActionRequest request) {
        return taskService.completeTask(request.getId());
    }

    @Operation(summary = "取消任务")
    @PostMapping("/cancel")
    public Boolean cancel(@RequestBody @Valid TaskActionRequest request) {
        return taskService.cancelTask(request.getId());
    }
}
