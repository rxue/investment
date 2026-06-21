package io.github.rxue.investment.portfolio.xirr;

import io.github.rxue.investment.portfolio.xirr.jpaentity.XIRRRawInput;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
class RawInputPersistenceAspect {
    private final RawInputRepository rawInputRepository;

    RawInputPersistenceAspect(RawInputRepository rawInputRepository) {
        this.rawInputRepository = rawInputRepository;
    }

    @Around("@annotation(io.github.rxue.investment.portfolio.xirr.PersistRawInput)")
    Object persistRawInput(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        if (result instanceof XIRRRawInput rawInput) {
            return rawInputRepository.save(rawInput);
        }
        return result;
    }
}
