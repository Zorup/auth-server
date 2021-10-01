package com.example.authserver.common.config;

import net.rakugakibox.util.YamlResourceBundle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;
import java.util.ResourceBundle;

@Configuration
public class MessageConfig implements WebMvcConfigurer {

    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(Locale.KOREAN);
        return slr;
    }

    // request에서 'lang' 이라는 이름의 파라미터가 들어오면 값을 읽어 locale을 변경해주는 인터셉터
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang");
        return lci;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }

    @Bean // yml 파일을 참조
    public MessageSource messageSource(
            @Value("${spring.messages.basename}") String basename,
            @Value("${spring.messages.encoding}") String encoding
    ) {
        YamlMessageSource ms = new YamlMessageSource();
        ms.setBasename(basename);
        ms.setDefaultEncoding(encoding);
        ms.setAlwaysUseMessageFormat(true);     // argument가 없는 메시지의 경우에도 메시지를 String 그대로가 아닌 MessageFormat 형식으로 Parsing
        ms.setUseCodeAsDefaultMessage(true);    // 메시지가 없을경우 코드를 메시지로 사용
        ms.setFallbackToSystemLocale(true); // locale에 맞는 파일 못찾을경우 system locale에 맞는 파일 찾도록. false일경우 locale 없이 basename만으로 찾음
        return ms;
    }

    // locale 정보에 따라 다른 yml 파일을 읽도록 처리
    private static class YamlMessageSource extends ResourceBundleMessageSource {
        @Override
        protected ResourceBundle doGetBundle(String basename, Locale locale) {
            return ResourceBundle.getBundle(basename, locale, YamlResourceBundle.Control.INSTANCE);
        }
    }
}



/*
    exceptionAdvice.java 에서 messageSource.getMessage() 호출해 언어(locale)에 따른 적절한 예외코드, 예외메시지를 받아오기 위한 설정.

    대략적인 구조는 이렇다
    messageSource bean -> YamlMessageSource
                            extends ResourceBundleMessageSource
                                extends AbstractMessageSource
                                    implements MessageSource

    messageSource bean은 basename(여기선 "exception") 에 locale이 붙은 이름의 yml파일을 참조한다. ex) exception_ko.yml

    이때 locale값은, SessionLocaleResolver가 request로부터 client의 locale정보를 읽고서 session에 세팅해둔 값에 따른 문자열상수값을 사용하는것으로 보임.
    언어정보는 lowercase, 지역정보는 uppercase로 설정되어있어서 KOREA면 ko와 KR, KOREAN이면 ko 이런식으로 값이 박혀있다.
    이 값들을 이용하도록 파일이름을 exception_ko.yml, exception_en_US.yml 처럼 로케일별로 분리작성한다.


    localeChangeInterceptor bean 설정을 통해
    request에 'lang' 이라는 파라미터가 들어오면 값을 읽어 로케일을 변경하는 작업을 컨트롤러에 앞서 사전수행한다.
    따라서 요청시 lang=en 과 같은 파라미터를 함께 보내주면 로케일이 변경되어 exception_en.yml 에서 정보를 꺼내쓰게 된다.
    만약 lang파라미터 없이 보낸다면
    localeResolver bean 설정에, locale정보가 안들어올경우 Locale.KOREAN 을 디폴트 로케일로 사용하도록 해놓은 상태이므로
    자동으로 exception_ko.yml을 사용한다.


    exceptionAdvice.java 의 messageSource.getMessage() 대략적인 동작흐름
    AbstractMessageSource.getMessage(String code, Object[] args, Locale locale)
        -> 1. getMessageInternal(code, args, locale)
            -> 1-1. resolveCode(code, locale) (ResourceBundleMessageSource에 구현)
                -> 1-1-1. getResourceBundle(basename, locale) 를 통해 basename, locale에 알맞는 bundle (yml정보로 추정) 찾음
                -> 1-1-2. getMessageFormat(bundle, code, locale) 을 통해 bundle 속에서 code에 맞는 MessageFormat를 찾음
                -> 1-1-3. 찾은 MessageFormat을 반환
            -> 1-2. getMessage 호출시 받은 args들 적절히 합친 String 반환
        -> 2. getMessageInternal 결과로 얻은 String 메시지를 반환,
              결과가 null이었다면 messageSource bean 정의할때 setUseCodeAsDefaultMessage(true) 설정을 했으면 code를 메시지로 반환,
                    setUseCodeAsDefaultMessage(false) 였다면 (디폴트가 false다) NoSuchMessageException 발생.


    1-1-1의 getResourceBundle(basename, locale) 내에서는
        doGetBundle(basename, locale) 을 통해 번들을 반환하는데 우리는 그 doGetBundle 메소드를 override하여,

        ResourceBundle 클래스의 static메소드인 getBundle(baseName, locale, ClassLoader) 를 호출할 때
        ClassLoader 매개변수로 YamlResourceBundle.Control.INSTANCE를 넘겨주도록 함으로써
        .properties 파일이 아닌 .yml 파일로부터 메시지를 읽을 수 있도록 하는것으로 보인다.


 */





