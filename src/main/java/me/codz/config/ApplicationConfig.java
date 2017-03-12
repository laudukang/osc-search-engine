package me.codz.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


/**
 * <p>Created with IDEA
 * <p>Author: laudukang
 * <p>Date: 2016/5/30
 * <p>Time: 22:54
 * <p>Version: 1.0
 */
@Configuration
//@EnableSpringDataWebSupport
@Import(PersistenceJPAConfig.class)
//@ComponentScan({"me.codz.domain", "me.codz.repository"})
public class ApplicationConfig {
}
