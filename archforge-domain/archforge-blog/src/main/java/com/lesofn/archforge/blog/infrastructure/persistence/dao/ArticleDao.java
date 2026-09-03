package com.lesofn.archforge.blog.infrastructure.persistence.dao;

import com.lesofn.archforge.blog.api.enums.BlogArticleStatus;
import com.lesofn.archforge.blog.infrastructure.persistence.po.ArticlePO;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * 文章 JPA DAO。
 *
 * <p>
 * 仅被 {@code blog} 模块内部的仓储实现使用，不对外暴露。
 */
@Repository
public interface ArticleDao extends JpaRepository<ArticlePO, Long>, JpaSpecificationExecutor<ArticlePO> {

    Optional<ArticlePO> findBySlugAndDeletedFalse(String slug);

    long countByDeletedFalse();

    boolean existsBySlugAndDeletedFalse(String slug);

    /** 分类删除校验用：统计该分类下处于给定状态且未删除的文章数。 */
    long countByCategoryIdAndStatusAndDeletedFalse(Long categoryId, BlogArticleStatus status);
}
