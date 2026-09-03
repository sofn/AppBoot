package com.lesofn.archforge.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noMethods;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.freeze.FreezingArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * ArchForge 架构守护规则（方案 §4.2）。
 *
 * <p>
 * 除规则 2 外，所有规则都通过 {@link FreezingArchRule#freeze(ArchRule)} 冻结当前违规作为基线
 * （存储在 {@code archunit_store/}，随仓库提交）。基线只允许减少、不允许增加：
 * 新增违规会直接让测试失败；修复一类违规后，删除 {@code archunit_store/} 重跑
 * 即可收紧基线。
 *
 * <p>
 * 规则 2（domain 模块不得依赖 infrastructure）在阶段 1 已清零（39 → 0），
 * 并在根构建增加 Gradle 侧第一道防线（{@code checkModuleDependencies}），
 * 故解冻为强制校验，不再走冻结基线。
 *
 * <p>
 * 注意：规则本身是"目标态"，仍有冻结规则的模块存在历史违规，不代表已经达标。
 */
@DisplayName("ArchForge 架构守护规则")
class ArchitectureTest {

    private static final String ROOT = "com.lesofn.archforge";

    private static JavaClasses classes;

    @BeforeAll
    static void importClasses() {
        classes = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages(ROOT);
    }

    /** 规则 1：领域层零框架依赖 */
    @Test
    @DisplayName("领域层不得依赖 Spring / JPA / Hibernate")
    void domainShouldBeFrameworkFree() {
        ArchRule rule = noClasses()
                .that().resideInAnyPackage(
                        "..archforge.user.domain.model..",
                        "..archforge.user.domain.service..",
                        "..archforge.user.domain.event..",
                        "..archforge.user.domain.repository..",
                        "..archforge.user.domain.valueobject..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("org.springframework..", "jakarta.persistence..", "org.hibernate..")
                .because("领域层必须与框架解耦，否则无法纯单元测试");
        check(rule);
    }

    /** 规则 2：domain 模块不得依赖 infrastructure 模块（阶段 1 的主验收规则） */
    @Test
    @DisplayName("领域模块不得依赖 infrastructure")
    void domainShouldNotDependOnInfrastructure() {
        ArchRule rule = noClasses()
                .that().resideInAnyPackage(
                        "..archforge.user..", "..archforge.blog..", "..archforge.meta.table..")
                .should().dependOnClassesThat().resideInAPackage("..archforge.infrastructure..")
                .because("反向依赖会让领域层吃进全技术栈；需要外部能力时应在 domain/.../port/ 声明端口");
        // 阶段 1 验收：admin-user 39 条违规清零后解冻（不走 FreezingArchRule）。
        // 已知缺口：demo.task（example-task）不在上述包列表内，其反向依赖切断由
        // Gradle checkModuleDependencies 兜底，后续阶段再纳入本规则监控。
        rule.check(classes);
    }

    /** 规则 3：禁止 Controller 直连持久层 */
    @Test
    @DisplayName("Controller 不得注入 Repository / Dao，必须经应用服务")
    void controllersShouldNotDependOnRepositories() {
        ArchRule rule = noClasses()
                .that().haveSimpleNameEndingWith("Controller")
                .should().dependOnClassesThat().haveSimpleNameEndingWith("Repository")
                .orShould().dependOnClassesThat().haveSimpleNameEndingWith("Dao")
                .because("持久层访问必须收口到应用服务，避免领域查询逻辑泄漏到 Controller");
        check(rule);
    }

    /** 规则 4：聚合根不得暴露 public setter */
    @Test
    @DisplayName("聚合根不得有 public setter")
    void aggregatesShouldNotExposeSetters() {
        ArchRule rule = noMethods()
                .that().arePublic()
                .and().haveNameMatching("set[A-Z].*")
                .should().beDeclaredInClassesThat().resideInAPackage("..domain.model.aggregate..")
                .because("聚合根状态变更必须走业务方法以保证不变量");
        check(rule);
    }

    /** 规则 5：值对象必须不可变 */
    @Test
    @DisplayName("值对象必须是 record")
    void valueObjectsShouldBeImmutable() {
        ArchRule rule = classes()
                .that().resideInAPackage("..domain.valueobject..")
                .and().haveNameMatching("(?!.*package-info).*")
                .should().beRecords()
                .because("值对象应不可变并在构造时自校验")
                .allowEmptyShould(true);
        check(rule);
    }

    /** 规则 6：领域事件必须是 record 且以 Event 结尾 */
    @Test
    @DisplayName("领域事件必须是 record 且命名 XxxEvent")
    void domainEventsShouldBeRecords() {
        ArchRule rule = classes()
                .that().resideInAPackage("..domain.event..")
                .and().haveNameMatching("(?!.*package-info).*")
                .should().beRecords()
                .andShould().haveSimpleNameEndingWith("Event")
                .because("领域事件应不可变且命名表达已发生的事实")
                .allowEmptyShould(true);
        check(rule);
    }

    /** 规则 7：DTO 不得构造领域对象（排除 Object.toString()） */
    @Test
    @DisplayName("DTO 不得提供 toEntity()/toXxx()")
    void dtoShouldNotBuildDomainObjects() {
        ArchRule rule = noMethods()
                .that().haveNameMatching("to(?!String)[A-Z].*")
                .should().beDeclaredInClassesThat().resideInAPackage("..dto..")
                .because("DTO 通过 setter 构造领域对象会绕过全部不变量校验");
        check(rule);
    }

    /** 规则 8：事务边界不落在 Controller 和领域模型上 */
    @Test
    @DisplayName("@Transactional 只允许出现在应用服务层")
    void transactionBoundaryShouldBeApplicationServiceOnly() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..archforge.server..controller..")
                .or().resideInAPackage("..archforge.user.domain.model..")
                .should().beAnnotatedWith("org.springframework.transaction.annotation.Transactional")
                .because("事务边界应统一收口在应用服务，避免边界混乱");
        check(rule);
    }

    /** 规则 9：跨模块不得引用 internal 包 */
    @Test
    @DisplayName("server 层不得引用领域模块的 internal 包")
    void internalShouldNotBeReferencedAcrossModules() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..archforge.server..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "..archforge.user.internal..",
                        "..archforge.blog.internal..",
                        "..archforge.meta.table.internal..",
                        "..archforge.demo.task.internal..")
                .because("internal 包是模块私有实现，跨模块引用应走 api 包");
        check(rule);
    }

    /** 规则 10：整体分层依赖方向 */
    @Test
    @DisplayName("分层架构依赖方向：Controller -> Application -> Domain")
    void layeredArchitectureShouldBeRespected() {
        ArchRule rule = layeredArchitecture()
                .consideringOnlyDependenciesInLayers()
                .layer("Controller").definedBy("..archforge.server..controller..")
                .optionalLayer("Application").definedBy("..application..")
                .layer("Domain").definedBy("..domain..")
                .layer("Infrastructure").definedBy("..archforge.infrastructure..")
                .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
                .whereLayer("Application").mayOnlyBeAccessedByLayers("Controller")
                // Infrastructure 可访问 Domain：这是依赖倒置（DIP）的合法形态——
                // infrastructure 实现 domain/.../port/ 声明的端口并引用其领域类型
                //（阶段 1 引入 EnumDictionaryPortAdapter 等端口适配器）
                .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Controller", "Infrastructure")
                .because("分层依赖必须单向，否则架构会退化");
        check(rule);
    }

    /**
     * 冻结检查：首次运行会把全部当前违规写入 {@code archunit_store/} 并通过；
     * 之后只有"基线之外的新增违规"才会让测试失败。
     */
    private static void check(ArchRule rule) {
        FreezingArchRule.freeze(rule).check(classes);
    }
}
