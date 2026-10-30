# DWAGGER

dwagger 是 Spring Boot 的 OpenAPI 3 文档增强方案：springdoc 负责生成规范，dwagger 负责界面与扩展注解。启动后访问 `/doc.html` 即可调试接口。

核心能力：分组、搜索、标签页、排序、在线调试、请求参数缓存、AfterScript、Swagger Models、文档管理、Authorize / Basic / OAuth2、生产环境屏蔽、中英文切换。

> 本文档同时面向人类与 AI 代理。文中的路径、坐标、端口和验证命令均实测可用。

## 1. 仓库结构

| 路径 | 说明 |
| --- | --- |
| `dwagger-ui/` | Vue 3 前端源码，构建产物在 `dist/`（不入库，由 Maven 构建生成） |
| `dwagger-backend/dwagger-core/` | 核心工具、扩展注解、过滤器 |
| `dwagger-backend/dwagger-openapi3-ui/` | 只含前端静态资源的 jar（webjar），构建时打包 `dwagger-ui/dist`，`/doc.html` 由它提供 |
| `dwagger-backend/dwagger-openapi3-spring-boot-starter/` | Spring Boot 2.x（javax）Starter |
| `dwagger-backend/dwagger-openapi3-jakarta-spring-boot-starter/` | Spring Boot 3.x / 4.x（jakarta）Starter |

## 2. 兼容矩阵

| Spring Boot | Starter | springdoc | Servlet | Java |
| --- | --- | --- | --- | --- |
| 2.x | `dwagger-openapi3-spring-boot-starter` | 1.8.0（内置） | javax | 8+ |
| 3.5.x | `dwagger-openapi3-jakarta-spring-boot-starter` + springdoc 2.9.1 | 2.9.1（手动指定） | jakarta | 17+ |
| 4.1.x | `dwagger-openapi3-jakarta-spring-boot-starter` | 3.1.1（内置） | jakarta | 17+ |

- 两个 Starter 只能二选一。
- Spring Boot 3.x 与 4.x 共用 jakarta Starter，区别只在 springdoc 版本：4.x 用内置 3.x；3.x 必须排除内置的 3.x 并手动引入 2.9.x。
- 已实测：Boot 4.1.1 + JDK 17 + springdoc 3.1.1；Boot 3.5.16 + springdoc 2.9.1。
- Boot 3.5.x / 4.1.x 默认的 Logback 1.5.x 存在 CVE-2026-19880、CVE-2026-104721，建议把 `logback.version` 设为 `1.6.5`、`slf4j.version` 设为 `2.0.20`。Boot 2.7 不能升到 Logback 1.6 / SLF4J 2。

## 3. 硬性约束（接入前必读）

以下任意一条不满足，`/doc.html` 都会空白或提示 `Dwagger文档请求异常`。

1. **`dwagger.enable=true`**：这是总开关，默认关闭。
2. **`springdoc.swagger-ui.enabled` 必须保持 `true`（默认值）**。dwagger UI 通过 `GET /v3/api-docs/swagger-config` 读取分组列表，该端点由 springdoc 的 `SwaggerConfig` 提供，一旦关闭 swagger-ui 就会 404。不要为了隐藏自带页面而关闭它；如果想隐藏 `/swagger-ui/index.html`，请用安全框架拦截。
3. **不要用 `configureMessageConverters(List)` 覆盖默认转换器后把 Jackson 放在最前面**。springdoc 3.x 的 `/v3/api-docs` 返回 `byte[]`，Jackson 会把它序列化成 base64 字符串（页面表现为拿到一串 `"eyJvcGVuYXBp..."`）。自定义转换器时必须保证 `ByteArrayHttpMessageConverter` 排在 `MappingJackson2HttpMessageConverter` 之前。
4. **项目设置了 `server.servlet.context-path` 时**，直接用 `http://host:port/{context-path}/doc.html` 访问，UI 会自动按当前路径拼接 docs 地址。
5. `/doc.html` 是固定路径，不提供配置项。

## 4. 快速开始

### 4.1 Spring Boot 4.1.x

```xml
<dependency>
    <groupId>cn.haigle.dwagger</groupId>
    <artifactId>dwagger-openapi3-jakarta-spring-boot-starter</artifactId>
    <version>0.0.5</version>
</dependency>
```

### 4.2 Spring Boot 3.5.x（排除内置 springdoc 3.x，改用 2.9.1）

```xml
<dependency>
    <groupId>cn.haigle.dwagger</groupId>
    <artifactId>dwagger-openapi3-jakarta-spring-boot-starter</artifactId>
    <version>0.0.5</version>
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

### 4.3 Spring Boot 2.x

```xml
<dependency>
    <groupId>cn.haigle.dwagger</groupId>
    <artifactId>dwagger-openapi3-spring-boot-starter</artifactId>
    <version>0.0.5</version>
</dependency>
```

### 4.4 Gradle

```groovy
// Spring Boot 4.x
implementation 'cn.haigle.dwagger:dwagger-openapi3-jakarta-spring-boot-starter:0.0.5'

// Spring Boot 3.5.x
implementation('cn.haigle.dwagger:dwagger-openapi3-jakarta-spring-boot-starter:0.0.5') {
    exclude group: 'org.springdoc', module: 'springdoc-openapi-starter-webmvc-ui'
}
implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.9.1'
```

### 4.5 最小配置

```yaml
dwagger:
  enable: true
  cors: true

springdoc:
  # 保持 true，dwagger 依赖 /v3/api-docs/swagger-config 获取分组
  swagger-ui:
    enabled: true
  api-docs:
    enabled: true
    path: /v3/api-docs
  group-configs:
    - group: default
      packages-to-scan: com.example.demo
```

`@EnableDwagger` 不是必需的，加上也不影响：

```java
@EnableDwagger
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

## 5. 30 秒验证清单

启动应用后依次执行（把 `8080` 换成实际端口）：

```bash
# 1. UI 页面：应返回 200 且包含 webjars/js/doc-*.js
curl -s -o /dev/null -w "%{http_code}\n" http://localhost:8080/doc.html

# 2. 分组配置：应返回含 "urls" 数组的 JSON
curl -s http://localhost:8080/v3/api-docs/swagger-config

# 3. 接口文档：应以 {"openapi":"3.x.x" 开头，而不是引号包起来的 base64
curl -s http://localhost:8080/v3/api-docs | head -c 60

# 4. UI 静态资源：应返回 200（从 doc.html 里取出实际带 hash 的文件名）
JS=$(curl -s http://localhost:8080/doc.html | grep -o 'webjars/js/doc-[^"]*\.js' | head -1)
curl -s -o /dev/null -w "%{http_code}\n" "http://localhost:8080/$JS"
```

判定标准：

- 第 2 步返回 404 → 见第 6 节「页面空白」行。
- 第 3 步返回 `"eyJvcGVuYXBp...`（带引号的 base64）→ 见第 6 节「返回带引号的 base64」行。
- 第 1 步 200 但页面空白 → 打开浏览器控制台，若出现 `Dwagger文档请求异常`，见第 6 节「页面空白」行。

## 6. 常见故障排查

| 现象 | 根因 | 修复 |
| --- | --- | --- |
| 页面空白，控制台提示 `Dwagger文档请求异常` | `springdoc.swagger-ui.enabled=false`，`/v3/api-docs/swagger-config` 404 | 删除该配置或改为 `true` |
| `/v3/api-docs` 返回带引号的 base64 字符串 | 自定义 `configureMessageConverters(List)` 把 Jackson 排在 `ByteArrayHttpMessageConverter` 之前 | 在 Jackson 之前加入 `new ByteArrayHttpMessageConverter()`，或改用 `Jackson2ObjectMapperBuilderCustomizer` 定制 ObjectMapper |
| `/doc.html` 404 | 未引入 UI 资源 jar，或静态资源被安全框架/自定义 `ResourceHandler` 拦截 | 确认引入的是 starter 而不是只有 core；放行 `/doc.html`、`/webjars/**`、`/v3/api-docs/**` |
| `/v3/api-docs` 401/403 | Spring Security 拦截 | 对文档端点 `permitAll()` |
| 分组下拉为空或缺少某个分组 | `group-configs[].packages-to-scan` 没匹配到 Controller 包 | 改成实际包名；不分组时删除 `group-configs`，使用 `/v3/api-docs` |
| 页面能打开但接口列表为空 | Controller 不在扫描包内，或类上缺少 `@RestController` | 检查包路径与注解 |
| 调试请求 404 | 应用配置了 `context-path`，但调试地址没带前缀 | 检查 `dwagger.setting.enableHost` / `enableHostText`，或直接使用相对地址 |
| IDEA 对 `group-configs[].packages-to-scan` 报「需要索引访问权限」 | IDEA 把 `packages-to-scan` 解析为 `List<String>`，要求键路径带 `[0]` 索引才算解析成功（`SpringBootApplicationYaml` 检查，2026.x 的误报） | 纯 IDE 误报，不影响运行和绑定，可忽略；不要为它改配置，也不要换成 `GroupedOpenApi` Bean（3.x 不会出现在 `swagger-config` 里，分组下拉会空） |

自定义转换器的正确写法（Boot 4 + springdoc 3.x）：

```java
@Override
public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    MappingJackson2HttpMessageConverter jackson = new MappingJackson2HttpMessageConverter();
    // ... 自定义 ObjectMapper ...

    // byte[] 必须用 ByteArrayHttpMessageConverter 原样输出，且排在 Jackson 之前
    converters.add(new ByteArrayHttpMessageConverter());
    converters.add(jackson);
    converters.add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
}
```

## 7. 使用注解

dwagger 的扩展注解只负责排序和展示作者信息，接口描述仍使用 OpenAPI 3 标准注解。

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

## 8. 配置项

| 配置 | 默认值 | 说明 |
| --- | --- | --- |
| `dwagger.enable` | `false` | 是否开启 dwagger |
| `dwagger.cors` | `false` | 是否开启默认跨域 |
| `dwagger.production` | `false` | 生产环境屏蔽文档 |
| `dwagger.basic.enable` | `false` | 是否开启 Basic 认证 |
| `dwagger.basic.username` | - | Basic 用户名 |
| `dwagger.basic.password` | - | Basic 密码 |
| `dwagger.basic.include` | - | 需要 Basic 校验的 URL（正则） |
| `dwagger.setting.customCode` | `200` | `production=true` 时返回的 HTTP 状态码 |
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

## 9. 多分组

```yaml
springdoc:
  group-configs:
    - group: user
      packages-to-scan: com.example.demo.user
    - group: order
      packages-to-scan: com.example.demo.order
```

UI 顶部会显示分组下拉框。只有一个分组时，可以设置 `dwagger.setting.enableGroup: false` 隐藏。

## 10. 接口鉴权（Authorize）

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

## 11. 离线文档与自定义首页

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

## 12. 从源码构建

环境：JDK 17+、Maven 3.9+、Node.js 20.19+（或 22.12+）、pnpm。

一条命令即可完成前端构建、资源打包和全部模块安装：

```bash
mvn -f dwagger-backend/pom.xml clean install
```

`dwagger-openapi3-ui` 模块在 `generate-resources` 阶段会自动执行：

1. `pnpm install`（工作目录 `dwagger-ui/`）
2. `pnpm run build:SpringDocOpenApi`
3. 把 `dwagger-ui/dist` 下的全部文件（含 `.gz`）打进 jar 的 `META-INF/resources/`

验证接入：启动你自己的应用，打开 `http://localhost:8080/doc.html`（端口以实际配置为准）。

注意：

- 前端构建产物（`dwagger-ui/dist`、jar 内的 `META-INF/resources`）不入库，每次构建自动生成，文件名带 hash 不会残留旧文件。
- 只改后端、想跳过前端构建：`mvn -f dwagger-backend/pom.xml clean install -Ddwagger-skip-ui-build=true`，此时直接复用本地已有的 `dwagger-ui/dist`。
- 单独调试前端：`cd dwagger-ui && pnpm dev`；只构建前端：`pnpm build`。
- `pnpm run build:SpringDocOpenApi` 读取 `.env.production`，默认发布类型为 `SpringDocOpenApi`。

## 13. AI 代理接入检查清单

按顺序执行，任何一步失败就停在对应项修复：

1. 确认 Spring Boot 大版本，按第 2 节选择 Starter，且只引入一个。
2. 全局搜索 `springdoc.swagger-ui.enabled`，值必须为 `true` 或未配置。
3. 全局搜索 `configureMessageConverters`，若存在，确认 `ByteArrayHttpMessageConverter` 位于 Jackson 之前。
4. 确认 `dwagger.enable=true`。
5. 确认安全框架放行 `/doc.html`、`/webjars/**`、`/v3/api-docs/**`。
6. 启动应用，执行第 5 节的 4 条 `curl`，全部符合预期才算接入完成。
7. 若使用分组，核对 `packages-to-scan` 与 Controller 实际包名一致。

## 致谢

本项目基于 [knife4j](https://doc.xiaominfo.com/) 迭代开发，感谢原作者与社区。

## License

[MIT](LICENSE)
