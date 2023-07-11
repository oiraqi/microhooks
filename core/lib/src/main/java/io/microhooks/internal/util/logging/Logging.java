package io.microhooks.internal.util.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Slf4j
public class Logging {

    @Around("@annotation(io.microhooks.util.logging.Logged)")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            log.trace("Entering: " + joinPoint.getSignature().toLongString());
            Object proceed = joinPoint.proceed();
            log.trace("Exiting: " + joinPoint.getSignature().toLongString());
            return proceed;
        } catch (Throwable throwable) {
            log.warn("An error occured at: " + joinPoint.getSignature().toLongString());
            throw throwable;
        }
    }

}