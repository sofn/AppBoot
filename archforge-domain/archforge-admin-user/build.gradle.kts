plugins {
    id("java-test-fixtures")
}

dependencies {
    // testFixtures 源集不继承 implementation, 需显式引入版本平台
    "testFixturesImplementation"(platform("org.springframework.boot:spring-boot-dependencies:4.1.0"))
    "testFixturesImplementation"(platform(project(":archforge-dependencies")))

    api(project(":archforge-common:archforge-common-jpa"))

    // 领域模块内部的 BCrypt 适配器需要密码加密能力（原先经 infrastructure 传递引入，现显式声明）
    implementation("org.springframework.security:spring-security-crypto")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.hibernate.orm:hibernate-processor:7.4.1.Final")

    compileOnly(platform(project(":archforge-dependencies")))
    compileOnly("org.mapstruct:mapstruct")
    annotationProcessor(platform(project(":archforge-dependencies")))
    annotationProcessor("org.mapstruct:mapstruct-processor")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
