dependencies {
    // 领域模块禁止依赖 infrastructure / server-*（由根项目 checkModuleDependencies 强制）
    // JPA / 事务 / Modulith 注解能力由 common-jpa 提供（api 传递：starter-data-jpa，
    // 以及经 common-base 传递的 spring-modulith-api），与 blog 等 domain 模块同一模式
    api(project(":archforge-common:archforge-common-jpa"))

    // web/swagger/sa-token/validation 原先经 infrastructure 传递引入，现显式声明（版本由根项目统一 BOM 提供）
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui")
    implementation("cn.dev33:sa-token-spring-boot3-starter")

    // Lombok
    compileOnly("org.projectlombok:lombok")
}
