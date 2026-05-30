package com.example.reportes.infra.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	private static final String[] ALLOWED_ORIGINS = {
		"https://front-end-fe20261.vercel.app",
		"https://front-end-fe20261-c4otfrley-junior-morenos-projects.vercel.app",
		"http://front-end-fe20261.vercel.app",
		"http://front-end-fe20261-c4otfrley-junior-morenos-projects.vercel.app"
	};

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/api/**")
			.allowedOrigins(ALLOWED_ORIGINS)
			.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
			.allowedHeaders("*");
	}
}
