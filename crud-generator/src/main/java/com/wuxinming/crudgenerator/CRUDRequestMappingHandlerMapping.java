package com.wuxinming.crudgenerator;

import org.springframework.aop.support.AopUtils;
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

public class CRUDRequestMappingHandlerMapping extends RequestMappingHandlerMapping{
    @Override
    protected void detectHandlerMethods(Object handler) {
        Class<?> handlerType = (handler instanceof String ?
                obtainApplicationContext().getType((String) handler) : handler.getClass());

        if (handlerType != null) {
            final Class<?> userType = ClassUtils.getUserClass(handlerType);
            Map<Method, RequestMappingInfo> methods = MethodIntrospector.selectMethods(userType,
                    (MethodIntrospector.MetadataLookup<RequestMappingInfo>) method -> {
                        try {
                            return getMappingForMethod(method, userType);
                        }
                        catch (Throwable ex) {
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


    @Override
    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        String simpleClassName= null;
        if(method.getDeclaringClass()== CRUDControllerImpl.class){
            Type type = handlerType.getAnnotatedSuperclass().getType();
            if(type instanceof ParameterizedTypeImpl){
                Class cla =(Class) ((ParameterizedTypeImpl) type).getActualTypeArguments()[0];
                simpleClassName=cla.getSimpleName();
            }
        }
        RequestMappingInfo info = createRequestMappingInfo(method,simpleClassName);

        if (info != null) {
            RequestMappingInfo typeInfo = createRequestMappingInfo(handlerType,null);
            if (typeInfo != null) {
                info = typeInfo.combine(info);
            }
        }
        return info;
    }

    private RequestMappingInfo createRequestMappingInfo(AnnotatedElement element,
                                                        String simpleClassName) {
        RequestMapping  requestMapping= AnnotatedElementUtils.findMergedAnnotation(element, RequestMapping.class);
        if(simpleClassName!=null){
            requestMapping= new CRUDRequestMapping(requestMapping);
            requestMapping.path()[0]=
                    requestMapping.path()[0].replace(CRUDControllerImpl.CRUD_ITEM_PATH,simpleClassName);
        }
        RequestCondition<?> condition = (element instanceof Class ?
                getCustomTypeCondition((Class<?>) element) : getCustomMethodCondition((Method) element));
        return (requestMapping != null ? createRequestMappingInfo(requestMapping, condition) : null);
    }

    private static class CRUDRequestMapping implements RequestMapping{

        public CRUDRequestMapping(RequestMapping requestMapping) {
            this._name=requestMapping.name();
            this._path=requestMapping.path();
            this._annotationType=requestMapping.annotationType();
            this._consumes=requestMapping.consumes();
            this._headers=requestMapping.headers();
            this._method=requestMapping.method();
            this._params=requestMapping.params();
            this._produces=requestMapping.produces();
            this._value=requestMapping.value();
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
