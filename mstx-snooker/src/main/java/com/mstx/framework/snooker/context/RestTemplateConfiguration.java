package com.mstx.framework.user.context;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
public class RestTemplateConfiguration {

    @Autowired
    Environment environment;

    @Bean
    public RestTemplate createRestTemplate() {
        RestTemplate restTemplate = new RestTemplate(createClientHttpRequestFactory());
        List<HttpMessageConverter<?>> converterList = restTemplate.getMessageConverters();
        HttpMessageConverter<?> converter = null;
        for (HttpMessageConverter<?> item : converterList) {
            if (StringHttpMessageConverter.class == item.getClass()) {
                converter = item;
                break;
            }
        }
        if (converter != null) {
            converterList.remove(converter);
        }
        converterList.add(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        converterList.add(createFastJsonConvert());
        return restTemplate;
    }

    private HttpMessageConverter createFastJsonConvert() {
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat, SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.WriteNullNumberAsZero);
        FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
        fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);
        return fastJsonHttpMessageConverter;
    }

    private ClientHttpRequestFactory createClientHttpRequestFactory() {
        Integer maxTotalConnect = Integer.parseInt(environment.getProperty("rest.maxTotalConnect", "500"));
        Integer maxConnectPerRoute = Integer.parseInt(environment.getProperty("rest.maxConnectPerRoute", "500"));
        Integer connectTimeout = Integer.parseInt(environment.getProperty("rest.connectTimeout", "5000"));
        Integer readTimeout = Integer.parseInt(environment.getProperty("rest.readTimeout", "20000"));
        if (maxTotalConnect < 1) {
            SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();
            simpleClientHttpRequestFactory.setConnectTimeout(connectTimeout);
            simpleClientHttpRequestFactory.setReadTimeout(readTimeout);
            return simpleClientHttpRequestFactory;
        }
        HttpClient httpClient = HttpClientBuilder.create().setMaxConnTotal(maxTotalConnect).setMaxConnPerRoute(maxConnectPerRoute).build();
        HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        httpComponentsClientHttpRequestFactory.setConnectTimeout(connectTimeout);
        httpComponentsClientHttpRequestFactory.setReadTimeout(readTimeout);
        return httpComponentsClientHttpRequestFactory;
    }
}
