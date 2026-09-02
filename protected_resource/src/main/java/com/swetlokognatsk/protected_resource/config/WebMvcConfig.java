package com.swetlokognatsk.protected_resource.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.swetlokognatsk.protected_resource.OAuthInterceptor;

@Configuration
class WebMvcConfig implements WebMvcConfigurer {

    private final OAuthInterceptor oauthPreHandler;

    public WebMvcConfig(final OAuthInterceptor oauthPreHandler) {
        this.oauthPreHandler = oauthPreHandler;
    }

    public void addInterceptors(final InterceptorRegistry interceptorRegistry) {
        interceptorRegistry.addInterceptor(oauthPreHandler);
    }

}
