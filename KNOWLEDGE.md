## 1. bootstrap.yml 和 application.yml 的区别？
在Spring Boot项目中，`bootstrap.yml` 和 `application.yml` 都是用来进行配置的 YAML 文件，但它们在加载时机和作用范围上存在显著区别：
1. bootstrap.yml：
   - 加载优先级：bootstrap.yml 的加载发生在 application.yml 之前，是 Spring Boot 应用启动的第一个阶段。
   - 用途：主要用于设置应用程序引导时需要的属性，比如设置加密解密的秘钥、数据源配置（用于初始化Spring Cloud Config Client等分布式配置中心场景）、日志级别等系统级别的配置信息。
   - 不可覆盖性：bootstrap.yml 中的属性一旦被加载后，在应用运行过程中通常不会被重新加载或覆盖。
2. application.yml：
   - 加载顺序：在 bootstrap.yml 加载完成后，Spring Boot 才会加载 application.yml。
   - 用途：主要用来定义应用级别的配置，这些配置可以涵盖应用程序的各个模块和组件，例如数据库连接信息、服务器端口、自定义Bean配置、环境变量等应用特有且可能随环境变化而变化的配置项。
   - 可动态替换性：如果 application.yml 中的某些属性与bootstrap.yml中的属性标签相同，虽然application.yml不会覆盖bootstrap.yml中的相应属性，但是application.yml文件中的内容可以根据Spring Boot Actuator的刷新机制在应用运行时动态更改。

总结来说，`bootstrap.yml` 更侧重于程序启动时所需的固定基础配置，而 `application.yml` 则用于更广泛的、与具体业务相关的应用配置。

## 2. application-环境.yml
在Spring Boot项目中，除了基础的`bootstrap.yml`和`application.yml`配置文件外，通常还会根据不同的环境（如开发环境dev、测试环境test、生产环境prod等）创建特定环境的配置文件，以实现不同环境下应用配置的差异化管理。
- 这是Spring Boot项目中用于开发环境的具体配置文件。当应用运行在开发模式下时，Spring Boot会自动识别当前环境，并优先加载这个环境对应的配置文件来覆盖application.yml中的通用配置。
- 例如，你可以在这个文件中定义数据库连接字符串、日志级别、服务器端口等属性，以便在开发环境中进行调试或适配开发工具。

## 3. spring-cloud-starter-bootstrap依赖
在Spring Cloud的较新版本中（特别是从2020年发布的Spring Cloud版本开始，如Hoxton.SR5及之后的版本），`spring-cloud-starter-bootstrap`依赖的引入方式有所变化。这是因为Spring Cloud团队调整了配置数据加载的方式，特别是与Spring Boot 2.4及更高版本的整合更加紧密。在这之前，Spring Cloud应用通常依赖`bootstrap.yml`或`bootstrap.properties`文件来提前加载配置数据，如从配置服务器。但在Spring Boot 2.4及之后，这一机制通过引入新的配置处理方式得到了改进。

### 引入`spring-cloud-starter-bootstrap`依赖

如果你的项目需要使用到旧的`bootstrap`上下文方式，比如你依赖于`bootstrap.yml`来加载配置中心的配置，在Spring Boot 2.4及更高版本中，你需要手动添加`spring-cloud-starter-bootstrap`依赖来启用`bootstrap`上下文。

在`pom.xml`中引入依赖如下：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-bootstrap</artifactId>
    <version>版本号</version> <!-- 请替换为具体的版本号，如3.0.0 -->
</dependency>
```

请确保替换`<version>`标签中的内容为实际的版本号，这个版本号应该与你项目中使用的Spring Cloud版本相兼容。

### 注意事项

- **版本兼容性**：确保`spring-cloud-starter-bootstrap`的版本与你的Spring Cloud和Spring Boot版本兼容。可以查阅[Spring Cloud官方文档](https://spring.io/projects/spring-cloud)获取版本推荐。
- **配置迁移**：随着Spring Boot和Spring Cloud版本的更新，考虑逐步迁移到新的配置处理方式，利用Spring Boot原生支持的配置文件加载顺序和配置文件导入功能，减少对`bootstrap`上下文的依赖。

### 更新配置加载方式

从Spring Boot 2.4开始，Spring Boot提供了新的配置文件处理方式，包括`spring.config.import`属性，允许你指定配置文件的加载顺序和来源，这为替代`bootstrap`上下文提供了更灵活的方式。如果可能，推荐更新你的应用以适应这些新特性，以便更好地利用Spring Boot和Spring Cloud提供的能力。

## 4. spring.factories文件 和 org.springframework.boot.autoconfigure.AutoConfiguration.imports
### 4.1 spring.factories文件 
项目根目录以外的Bean，也就是导入的spring-boot-starter-***的maven依赖 是根据 /META-INF/spring.factories下的文件去进行加载的。
```
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
  com.cktec.common.web.config.WebMvcConfig,\
  com.cktec.common.web.config.FeignConfig
```
### 4.2 org.springframework.boot.autoconfigure.AutoConfiguration.imports
org.springframework.boot.autoconfigure.AutoConfiguration.imports 文件功能与 spring.factories一样，都是用来加载自动装配的类。注意：从spring boot2.7开始，慢慢不支持META-INF/spring.factories文件了，需要导入的自动配置类可以放在/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports文件中。
```
com.pig4cloud.pig.common.core.config.TaskExecutorConfiguration
```
## 5. springboot 新建一个starter
### 5.1 创建 Starter
- 新建一个模块lan-spring-boot-starter-web
- 添加必要的依赖
- 实现自动配置：创建xxx.config.YourAutoConfiguration，在 src/main/resources/META-INF 目录下，创建一个文件 spring.factories，并添加你的自动配置类
### 5.2 使用 Starter
```
<dependency>
    <groupId>your.groupId</groupId>
    <artifactId>your-artifactId</artifactId>
    <version>your.version</version>
</dependency>
```
## 