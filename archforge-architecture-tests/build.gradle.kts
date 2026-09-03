description = "ArchUnit + Spring Modulith architecture guard rails (test-only module)"

dependencies {
    // 被守护的模块：整仓类路径，让 ArchUnit 能 import 全部生产代码
    testImplementation(project(":archforge-common:archforge-common-base"))
    testImplementation(project(":archforge-common:archforge-common-jpa"))
    testImplementation(project(":archforge-common:archforge-common-error"))
    testImplementation(project(":archforge-infrastructure"))
    testImplementation(project(":archforge-domain:archforge-blog"))
    testImplementation(project(":archforge-domain:archforge-admin-user"))
    testImplementation(project(":archforge-domain:archforge-meta-table"))
    testImplementation(project(":archforge-example:archforge-example-task"))
    testImplementation(project(":archforge-server-admin"))
    testImplementation(project(":archforge-server-web"))

    testImplementation("com.tngtech.archunit:archunit")
    testImplementation("org.springframework.modulith:spring-modulith-test")
}

// 本模块只承载架构测试，不打 jar
tasks.jar {
    enabled = false
}
