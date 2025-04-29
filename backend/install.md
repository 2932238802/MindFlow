your-webapp/
├── pom.xml             <-- Maven 配置文件
└── src/
└── main/
├── java/
│   └── com/
│       └── example/
│           └── api/
│               └── StatusServlet.java  <-- 你的 Servlet 代码
└── webapp/
├── WEB-INF/
│   └── web.xml   <-- (如果不用@WebServlet注解，可以在这里配置Servlet)
└── index.html    <-- (你的前端文件，通常由Nginx处理，放这里或Nginx指定位置)



y-backend-app/
├── pom.xml                 <-- Maven 的项目配置文件 (或者 build.gradle for Gradle)
├── .gitignore              <-- Git 忽略文件配置
├── README.md               <-- 项目说明文件
├── src/                    <-- 源代码和资源根目录
│   ├── main/               <-- 主要的应用程序代码和资源
│   │   ├── java/           <-- Java 源代码根目录
│   │   │   └── com/        <-- 包结构 (根据你的域名反转或自定义)
│   │   │       └── example/
│   │   │           └── myapp/
│   │   │               ├── controller/   <-- 控制器层 (处理 HTTP 请求)
│   │   │               ├── service/      <-- 服务层 (业务逻辑)
│   │   │               ├── repository/   <-- 数据访问层 (与数据库交互, DAO)
│   │   │               ├── model/        <-- 数据模型 (实体类, DTO)
│   │   │               ├── config/       <-- 配置类
│   │   │               ├── util/         <-- 工具类
│   │   │               └── Application.java <-- Spring Boot 启动类 (或类似入口)
│   │   └── resources/      <-- 资源文件根目录
│   │       ├── application.properties  <-- Spring Boot 配置文件 (或 application.yml)
│   │       ├── static/       <-- 静态资源 (JS, CSS, 图片 - Spring Boot 约定)
│   │       ├── templates/    <-- 模板文件 (Thymeleaf, Freemarker - Spring Boot 约定)
│   │       ├── logback-spring.xml <-- 日志配置文件 (或 log4j2.xml)
│   │       └── META-INF/     <-- (可选) 元数据信息
│   └── test/               <-- 测试代码和资源
│       ├── java/           <-- Java 测试代码根目录
│       │   └── com/
│       │       └── example/
│       │           └── myapp/
│       │               ├── controller/   <-- 控制器测试
│       │               ├── service/      <-- 服务测试
│       │               └── ...
│       └── resources/      <-- 测试资源文件根目录
│           └── application-test.properties <-- 测试专用的配置文件
└── target/                 <-- Maven 构建输出目录 (编译后的 .class, 打包的 .jar/.war)
(或者 build/ for Gradle)    <-- (这个目录是自动生成的, 通常不提交到 Git)



执行命令：mvn clean package
这会生成一个 WAR 文件 (通常在 target/ 目录下，文件名类似 your-app-name.war)。


