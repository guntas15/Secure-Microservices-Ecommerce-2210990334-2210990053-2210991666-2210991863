package com.Ecommerce.payment_service.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class FeignClientConfigTest {

    private FeignClientConfig feignClientConfig;

    @BeforeEach
    void setUp() {
        feignClientConfig = new FeignClientConfig();
    }

    @AfterEach
    void tearDown() {
        
        RequestContextHolder.resetRequestAttributes();
    }

   

    @Test
    void interceptor_doesNothing_whenNoRequestContext() {

        RequestInterceptor interceptor =
                feignClientConfig.requestInterceptor();

        RequestTemplate requestTemplate = new RequestTemplate();

        interceptor.apply(requestTemplate);

        assertThat(requestTemplate.headers())
                .doesNotContainKey("Authorization");
    }



    @Test
    void interceptor_doesNothing_whenAuthorizationHeaderMissing() {

        HttpServletRequest request =
                mock(HttpServletRequest.class);

        when(request.getHeader("Authorization"))
                .thenReturn(null);

        ServletRequestAttributes attributes =
                new ServletRequestAttributes(request);

        RequestContextHolder.setRequestAttributes(attributes);

        RequestInterceptor interceptor =
                feignClientConfig.requestInterceptor();

        RequestTemplate requestTemplate = new RequestTemplate();

        interceptor.apply(requestTemplate);

        assertThat(requestTemplate.headers())
                .doesNotContainKey("Authorization");
    }

    

    @Test
    void interceptor_addsAuthorizationHeader_whenPresent() {

        HttpServletRequest request =
                mock(HttpServletRequest.class);

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer test-token");

        ServletRequestAttributes attributes =
                new ServletRequestAttributes(request);

        RequestContextHolder.setRequestAttributes(attributes);

        RequestInterceptor interceptor =
                feignClientConfig.requestInterceptor();

        RequestTemplate requestTemplate = new RequestTemplate();

        interceptor.apply(requestTemplate);

        assertThat(requestTemplate.headers())
                .containsKey("Authorization");

        assertThat(requestTemplate.headers()
                .get("Authorization"))
                .containsExactly("Bearer test-token");
    }
}
