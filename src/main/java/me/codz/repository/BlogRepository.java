package me.codz.repository;

import me.codz.domain.Blog;
import me.codz.domain.BlogDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>Created with IDEA
 * <p>Author: laudukang
 * <p>Date: 2016/6/4
 * <p>Time: 15:34
 * <p>Version: 1.0
 */
@Repository
public interface BlogRepository extends JpaRepository<Blog, Integer>, JpaSpecificationExecutor<Blog> {

	@Query("select new me.codz.domain.BlogDomain(blog.id,  blog.space,  blog.type,  blog.title,  blog.content, blog.tags, blog.createTime, " +
			"blog.contentType,blog.abstracts,blog.view_count, blog.reply_count,blog.vote_count) from Blog blog where id < :id ")
	List<BlogDomain> findPart(@Param("id") int id);
}