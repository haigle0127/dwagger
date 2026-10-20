package com.example.dwaggerdemo.controller;

import com.example.dwaggerdemo.model.User;
import com.haigle.dwagger.annotations.ApiOperationSupport;
import com.haigle.dwagger.annotations.ApiSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@ApiSupport(order = 1, author = "dwagger")
@Tag(name = "用户管理", description = "用户相关接口")
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final AtomicLong idGen = new AtomicLong(1);
    private final List<User> users = new ArrayList<>();

    @ApiOperationSupport(order = 1, author = "dwagger")
    @Operation(summary = "查询用户列表", description = "返回全部用户")
    @GetMapping("/list")
    public List<User> list() {
        return users;
    }

    @ApiOperationSupport(order = 2)
    @Operation(summary = "按ID查询用户")
    @GetMapping("/{id}")
    public User getById(@Parameter(description = "用户ID") @PathVariable("id") Long id) {
        return users.stream().filter(u -> u.getId().equals(id)).findFirst().orElse(null);
    }

    @ApiOperationSupport(order = 3)
    @Operation(summary = "新增用户")
    @PostMapping("/add")
    public User add(@RequestParam("name") String name, @RequestParam(value = "age", required = false) Integer age) {
        User user = new User();
        user.setId(idGen.getAndIncrement());
        user.setName(name);
        user.setAge(age);
        users.add(user);
        return user;
    }

    @ApiOperationSupport(order = 4)
    @Operation(summary = "保存用户实体")
    @PostMapping("/save")
    public User save(@RequestBody User user) {
        user.setId(idGen.getAndIncrement());
        users.add(user);
        return user;
    }
}
