# DWAGGER

dwagger 是为 Spring Boot 集成 OpenAPI 3 文档的增强解决方案。它提供开箱即用的文档界面和一组扩展注解，帮助你在 Spring Boot 项目中快速生成、调试和交付接口文档。

## 特性

- 开箱即用的文档 UI，启动后访问 `/doc.html`
- 接口分组、搜索、标签页与排序
- 在线调试、请求参数缓存、AfterScript
- Swagger Models 数据模型浏览
- 文档管理：全局参数、离线文档、个性化设置
- Authorize 鉴权：支持接口级与全局 `security`，填写后自动带入调试请求头
- Basic 认证、OAuth2 授权
- 生产环境屏蔽文档
- 中文 / English 界面切换

## 环境要求

| Spring Boot | Starter | springdoc | Servlet | Java |
| --- | --- | --- | --- | --- |
| 2.x | `dwagger-openapi3-spring-boot-starter` | 1.8.0（内置） | javax | 8+ |
| 3.5.x | `dwagger-openapi3-jakarta-spring-boot-starter` + springdoc 2.9.1 | 2.9.1（手动指定） | jakarta | 17+ |
| 4.1.x | `dwagger-openapi3-jakarta-spring-boot-starter` | 3.1.1（内置） | jakarta | 17+ |

两个 Starter 只能二选一，不能同时引入。Spring Boot 3.x 和 4.x 共用 jakarta Starter，区别在于 springdoc 版本：4.x 直接使用内置的 springdoc 3.x，3.x 需要排除内置版本并手动引入 springdoc 2.9.x。Boot 3 已用 3.5.16 实测通过。

> Boot 3.5.x / 4.1.x 默认的 Logback 1.5.x 存在 CVE-2026-19880、CVE-2026-104721，建议把 `logback.version` 设为 `1.6.5`、`slf4j.version` 设为 `2.0.20`。

## 快速开始

### 1. 添加依赖

Spring Boot 4.1.x：

```xml
<dependency>
    <groupId>cn.haigle.dwagger</groupId>
    <artifactId>dwagger-openapi3-jakarta-spring-boot-starter</artifactId>
    <version>0.0.2</version>
</dependency>
```

Spring Boot 3.5.x（排除内置的 springdoc 3.x，改用 springdoc 2.9.1）：

```xml
<dependency>
    <groupId>cn.haigle.dwagger</groupId>
    <artifactId>dwagger-openapi3-jakarta-spring-boot-starter</artifactId>
    <version>0.0.2</version>
    <exclusions>
        <exclusion>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.9.1</version>
</dependency>
```

Spring Boot 2.x：

```xml
<dependency>
    <groupId>cn.haigle.dwagger</groupId>
    <artifactId>dwagger-openapi3-spring-boot-starter</artifactId>
    <version>0.0.2</version>
</dependency>
```

Gradle：

```groovy
// Spring Boot 4.x
implementation 'cn.haigle.dwagger:dwagger-openapi3-jakarta-spring-boot-starter:0.0.2'

// Spring Boot 3.5.x
implementation('cn.haigle.dwagger:dwagger-openapi3-jakarta-spring-boot-starter:0.0.2') {
    exclude group: 'org.springdoc', module: 'springdoc-openapi-starter-webmvc-ui'
}
implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.9.1'
```

### 2. 开启 dwagger

在 `application.yml` 中开启：

```yaml
dwagger:
  enable: true
  cors: true
```

需要分组时，配合 springdoc 的 `group-configs`：

```yaml
springdoc:
  group-configs:
    - group: default
      packages-to-scan: com.example.demo
```

`dwagger.enable=true` 是总开关。`@EnableDwagger` 不是必需的，加上也不影响：

```java
@EnableDwagger
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

### 3. 打开文档

启动应用后访问：

```text
http://localhost:8080/doc.html
```

接口描述由 springdoc-openapi 提供：未配置分组时地址为 `/v3/api-docs`，配置分组后为 `/v3/api-docs/{group}`。

## 使用注解

dwagger 的扩展注解用于排序和展示作者信息，接口描述仍然使用 OpenAPI 3 标准注解。

```java
@ApiSupport(order = 1, author = "haigle")
@Tag(name = "用户管理", description = "用户相关接口")
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final List<User> users = new ArrayList<>();

    @ApiOperationSupport(order = 1, author = "haigle")
    @Operation(summary = "查询用户列表", description = "返回全部用户")
    @GetMapping("/list")
    public List<User> list() {
        return users;
    }
}
```

实体类使用 `@Schema`：

```java
@Schema(description = "用户信息")
public class User {

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户名")
    private String name;
}
```

- `@ApiSupport(order, author)`：Controller 级排序和作者
- `@ApiOperationSupport(order, author)`：接口级排序和作者
- `order` 越小越靠前
- `@Tag`、`@Operation`、`@Parameter`、`@Schema` 由 springdoc-openapi 解析

## 常用配置

| 配置 | 默认值 | 说明 |
| --- | --- | --- |
| `dwagger.enable` | `false` | 是否开启 dwagger |
| `dwagger.cors` | `false` | 是否开启默认跨域 |
| `dwagger.production` | `false` | 生产环境屏蔽文档 |
| `dwagger.basic.enable` | `false` | 是否开启 Basic 认证 |
| `dwagger.basic.username` | - | Basic 用户名 |
| `dwagger.basic.password` | - | Basic 密码 |
| `dwagger.setting.language` | `ZH_CN` | 界面语言，可选 `ZH_CN`、`EN` |
| `dwagger.setting.enableSwaggerModels` | `true` | 显示 Swagger Models |
| `dwagger.setting.enableDocumentManage` | `true` | 显示文档管理 |
| `dwagger.setting.enableSearch` | `true` | 显示搜索框 |
| `dwagger.setting.enableGroup` | `true` | 显示分组下拉框 |
| `dwagger.setting.enableOpenApi` | `true` | 显示 OpenAPI 原始结构 |
| `dwagger.setting.enableFooter` | `true` | 显示底部 footer |
| `dwagger.setting.enableResponseCode` | `true` | 显示响应状态码 |
| `dwagger.setting.enableDebug` | `true` | 开启在线调试 |
| `dwagger.setting.enableHomeCustom` | `false` | 是否自定义首页 |
| `dwagger.setting.homeCustomLocation` | - | 首页 Markdown 路径 |

Basic 认证示例：

```yaml
dwagger:
  enable: true
  basic:
    enable: true
    username: admin
    password: change-me
```

生产环境屏蔽示例：

```yaml
dwagger:
  enable: true
  production: true
  setting:
    customCode: 404
```

## 多分组

```yaml
springdoc:
  group-configs:
    - group: user
      packages-to-scan: com.example.demo.user
    - group: order
      packages-to-scan: com.example.demo.order
```

UI 顶部会显示分组下拉框。只有一个分组时，可以设置 `dwagger.setting.enableGroup: false` 隐藏。

## 接口鉴权（Authorize）

在 `securitySchemes` 中声明认证方式后，左侧会显示 Authorize 页面。填写 token 并保存，调试接口时会自动作为请求头带入。

`security` 可以写在接口上，也可以写在 OpenAPI 根节点（springdoc 常见做法）。根级 `security` 会被所有接口继承；scheme 缺少 `in` 时按请求头处理。全局 Bearer 示例：

```java
@Bean
public OpenAPI openAPI() {
    return new OpenAPI()
            .components(new Components().addSecuritySchemes("Authorization",
                    new SecurityScheme()
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")))
            .addSecurityItem(new SecurityRequirement().addList("Authorization"));
}
```

`apiKey` 类型可用 `in(SecurityScheme.In.HEADER).name("Authorization")` 自定义请求头名称。

## 离线文档与自定义首页

`dwagger.documents` 可以给分组挂载 Markdown 文档：

```yaml
dwagger:
  enable: true
  documents:
    - group: default
      name: 用户手册
      locations: classpath:markdown/user.md
```

设置 `dwagger.setting.enableHomeCustom: true` 后，可以用 `homeCustomLocation` 指定首页 Markdown。

## 从源码构建

```bash
mvn -f dwagger-backend/pom.xml clean install
```

修改前端 UI 后，先重新构建 `dwagger-ui`，再把产物回填到后端 UI 模块：

```bash
cd dwagger-ui
pnpm install
pnpm build
```

将 `dwagger-ui/dist` 下的全部文件复制到 `dwagger-backend/dwagger-openapi3-ui/src/main/resources`，然后重新执行上面的 Maven 命令。


## 致谢

本项目基于 [knife4j](https://doc.xiaominfo.com/) 迭代开发，感谢原作者与社区。

## License

[MIT](LICENSE)
