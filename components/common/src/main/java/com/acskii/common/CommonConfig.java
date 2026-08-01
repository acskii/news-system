package com.acskii.common;

import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan(basePackageClasses = CommonConfig.class)
@EnableJpaRepositories(basePackageClasses = CommonConfig.class)
@EntityScan(basePackageClasses = CommonConfig.class)
public class CommonConfig {}