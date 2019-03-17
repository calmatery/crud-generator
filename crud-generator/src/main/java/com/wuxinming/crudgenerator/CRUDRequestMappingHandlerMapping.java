package com.wuxinming.crudgenerator;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 自动生成URL映射关系
 */
public class CRUDRequestMappingHandlerMapping extends RequestMappingHandlerMapping {
    @Override
    protected void detectHandlerMethods(Object handler) {
        Class<?> handlerType = (handler instanceof String ?
                obtainApplicationContext().getType((String) handler) : handler.getClass());

        if (handlerType != null) {
            final Class<?> userType = ClassUtils.getUserClass(handlerType);
            Map<Method, RequestMappingInfo> methods = MethodIntrospector.selectMethods(userType,
                    (MethodIntrospector.MetadataLookup<RequestMappingInfo>) method -> {
                        try {
                            String beanName = null;
                            if (handler instanceof String)
                                beanName = handler.toString();
                            return getMappingForMethod(method, userType, beanName);
                        } catch (Throwable ex) {
                            throw new IllegalStateException("Invalid mapping on handler class [" +
                                    userType.getName() + "]: " + method, ex);
                        }
                    });
            if (logger.isDebugEnabled()) {
                logger.debug(methods.size() + " request handler methods found on " + userType + ": " + methods);
            }
            methods.forEach((method, mapping) -> {
                Method invocableMethod = AopUtils.selectInvocableMethod(method, userType);
                registerHandlerMethod(handler, invocableMethod, mapping);
            });
        }
    }


    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType, String beanName) {
        String simpleClassName = null;
        if (method.getDeclaringClass() == CRUDControllerImpl.class) {
            Type type = handlerType.getAnnotatedSuperclass().getType();
            if (type instanceof ParameterizedTypeImpl) {
                Class cla = (Class) ((ParameterizedTypeImpl) type).getActualTypeArguments()[0];
                simpleClassName = cla.getSimpleName();
            }
        }
        RequestMappingInfo info = createRequestMappingInfo(method, simpleClassName, beanName);

        if (info != null) {
            RequestMappingInfo typeInfo = createRequestMappingInfo(handlerType, null, beanName);
            if (typeInfo != null) {
                info = typeInfo.combine(info);
            }
        }
        return info;
    }


    private RequestMappingInfo createRequestMappingInfo(AnnotatedElement element,
                                                        String simpleClassName, String beanName) {
        RequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(element, RequestMapping.class);
        if (simpleClassName != null) {
            requestMapping = new CRUDRequestMapping(requestMapping);
            //生成中划线的url
            String itemUrl = generatorLineThroughUrl(simpleClassName);
            requestMapping.path()[0] =
                    requestMapping.path()[0].replace(CRUDControllerImpl.CRUD_ITEM_PATH, itemUrl);
        }
        RequestCondition<?> condition = (element instanceof Class ?
                getCustomTypeCondition((Class<?>) element) : getCustomMethodCondition((Method) element));
        return (requestMapping != null ? createRequestMappingInfo(requestMapping, condition, beanName) : null);
    }


    protected RequestMappingInfo createRequestMappingInfo(RequestMapping requestMapping, RequestCondition<?> customCondition, String beanName) {
        if (requestMapping.path().length >= 1) {
            BeanDefinition beanDefinition = CRUDRegistrar.registry.getBeanDefinition(beanName);
            Object object = beanDefinition.getAttribute("basePackage");
            if (object != null && object instanceof String) {
                String className = beanDefinition.getBeanClassName();
                String moduleUrl = generatorModuleURL(className, (String) object);
                requestMapping = new CRUDRequestMapping(requestMapping);
                requestMapping.path()[0] =
                        requestMapping.path()[0].replace(CRUDControllerImpl.MODULE_PATH, moduleUrl);
            }
        }
        return super.createRequestMappingInfo(requestMapping, customCondition);
    }

    private static String generatorModuleURL(String className, String basePackage) {
        int lastDotPos = className.lastIndexOf(".");
        if (lastDotPos <= 0)
            return "";
        className = className.substring(0, lastDotPos);
        if (className.length() >= basePackage.length()) {
            if (className.substring(0, basePackage.length()).equals(basePackage)) {
                String url = className.substring(basePackage.length());
                if (url.startsWith(".")) {
                    url = url.substring(1);
                }
                url = url.replace(".", "/");
                url = generatorLineThroughUrl(url);
                return url;
            } else
                return "";
        } else
            return "";
    }

    private static String generatorLineThroughUrl(String simpleClassName) {
        Pattern pattern = Pattern.compile("[A-Z]{1}");
        Matcher matcher = pattern.matcher(simpleClassName);
        StringBuffer stringBuffer = new StringBuffer(simpleClassName);
        int posDeviation = 0;
        while (matcher.find()) {
            String lowerLetter = "";
            if (matcher.start() > 0)
                lowerLetter = "-";
            lowerLetter = lowerLetter + matcher.group(0).toLowerCase();
            stringBuffer.replace(matcher.start() + posDeviation,
                    matcher.end() + posDeviation, lowerLetter);
            if (matcher.start() > 0)
                posDeviation++;

        }
        return stringBuffer.toString();
    }


    private static class CRUDRequestMapping implements RequestMapping {

        public CRUDRequestMapping(RequestMapping requestMapping) {
            this._name = requestMapping.name();
            this._path = requestMapping.path();
            this._annotationType = requestMapping.annotationType();
            this._consumes = requestMapping.consumes();
            this._headers = requestMapping.headers();
            this._method = requestMapping.method();
            this._params = requestMapping.params();
            this._produces = requestMapping.produces();
            this._value = requestMapping.value();
        }

        public String _name;


        @Override
        public String name() {
            return _name;
        }

        public String[] _value;

        @Override
        public String[] value() {
            return _value;
        }

        public String[] _path;

        @Override
        public String[] path() {
            return _path;
        }

        public RequestMethod[] _method;

        @Override
        public RequestMethod[] method() {
            return _method;
        }

        public String[] _params;

        @Override
        public String[] params() {
            return _params;
        }

        public String[] _headers;

        @Override
        public String[] headers() {
            return _headers;
        }

        public String[] _consumes;

        @Override
        public String[] consumes() {
            return _consumes;
        }

        public String[] _produces;

        @Override
        public String[] produces() {
            return _produces;
        }

        public Class<? extends Annotation> _annotationType;

        @Override
        public Class<? extends Annotation> annotationType() {
            return _annotationType;
        }
    }

}
