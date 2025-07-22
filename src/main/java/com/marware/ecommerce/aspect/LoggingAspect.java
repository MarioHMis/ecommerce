package com.marware.ecommerce.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void restControllerMethods() {}

    @Around("restControllerMethods()")
    public Object logRequestAndResponse(ProceedingJoinPoint joinPoint) throws Throwable {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = (attrs instanceof ServletRequestAttributes)
                ? ((ServletRequestAttributes) attrs).getRequest()
                : null;

        long start = System.currentTimeMillis();

        if (request != null) {
            StringBuilder params = new StringBuilder();
            var names = request.getParameterNames();
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                params.append(name)
                        .append("=")
                        .append(request.getParameter(name))
                        .append(" ");
            }
            logger.info("Incoming request: {} {} Params=[{}]",
                    request.getMethod(),
                    request.getRequestURI(),
                    params.toString().trim());
        } else {
            logger.info("Entering {}.{}",
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName());
        }

        Object result = joinPoint.proceed();
        long elapsed = System.currentTimeMillis() - start;

        logger.info("Completed {}.{} in {} ms",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                elapsed);

        return result;
    }
}