package com.marware.ecommerce.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    // Pointcut para todos los métodos en servicios y controladores
    @Pointcut("within(com.marware.ecommerce.service..*) || within(com.marware.ecommerce.controller..*)")
    public void applicationLayer() {}

    @Around("applicationLayer()")
    public Object logExecution(ProceedingJoinPoint pjp) throws Throwable {
        String signature = pjp.getSignature().toShortString();
        Object[] args = pjp.getArgs();

        log.info("Entering {} with arguments = {}", signature, args);
        long start = System.currentTimeMillis();

        try {
            Object result = pjp.proceed();
            long elapsed = System.currentTimeMillis() - start;
            log.info("Exiting {} with result = {} ({} ms)", signature, result, elapsed);
            return result;
        } catch (Throwable ex) {
            long elapsed = System.currentTimeMillis() - start;
            log.error("Exception in {} after {} ms: {}", signature, elapsed, ex.getMessage());
            throw ex;
        }
    }
}
