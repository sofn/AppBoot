package com.lesofn.archforge.blog.infrastructure.config;

import com.lesofn.archforge.common.persistence.GroupDataSourceProxy;
import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * blog 模块持久化配置。
 *
 * <p>
 * 阶段 2 从 {@code blog.internal.config} 迁到本包，与模块内基础设施层
 * （{@code blog.infrastructure.persistence}）放在一起：JPA 职责属于基础设施，
 * 不应留在 internal 实现包中。
 *
 * <p>
 * 扫描范围包含两代仓储：文章走新形态（{@code infrastructure.persistence.dao}），
 * 分类暂留旧形态（{@code api.dao} + {@code api.domain} 实体），阶段 4 迁移后收编。
 */
@Configuration
@EnableJpaRepositories(
        basePackages = {
                "com.lesofn.archforge.blog.infrastructure.persistence.dao",
                "com.lesofn.archforge.blog.api.dao"
        },
        entityManagerFactoryRef = "blogEntityManagerFactory",
        transactionManagerRef = "blogTransactionManager")
public class BlogDbConfig {

    @Value("${spring.jpa.hibernate.ddl-auto:update}")
    private String ddlAuto;

    @Resource
    private DataSource dataSource;

    private DataSource blogDataSource() {
        return new GroupDataSourceProxy(this.dataSource, "user");
    }

    @Bean
    PlatformTransactionManager blogTransactionManager() {
        return new JpaTransactionManager(blogEntityManagerFactory().getObject());
    }

    @Bean
    LocalContainerEntityManagerFactoryBean blogEntityManagerFactory() {
        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setGenerateDdl(true);
        jpaVendorAdapter.setShowSql(false);
        jpaVendorAdapter.setDatabasePlatform("org.hibernate.dialect.PostgreSQLDialect");

        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(blogDataSource());
        factoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        factoryBean.setPersistenceUnitName("blog");
        factoryBean.setPackagesToScan(
                "com.lesofn.archforge.blog.infrastructure.persistence.po",
                "com.lesofn.archforge.blog.api.domain",
                "com.lesofn.archforge.common.repository.converter");

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", this.ddlAuto);
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.put(
                "hibernate.physical_naming_strategy",
                "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy");
        properties.put(
                "hibernate.implicit_naming_strategy",
                "org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl");
        factoryBean.setJpaPropertyMap(properties);

        return factoryBean;
    }
}
