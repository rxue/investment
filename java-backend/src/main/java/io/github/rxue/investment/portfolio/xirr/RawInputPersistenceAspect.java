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
        XIRRRawInput rawInput = result instanceof XIRRRawInput resultRawInput
                ? resultRawInput
                : findRawInputArgument(joinPoint.getArgs());
        if (rawInput != null) {
            XIRRRawInput persistedRawInput = rawInputRepository.save(rawInput);
            rawInput.setCashFlows(persistedRawInput.getCashFlows());
        }
        return result;
    }

    private static XIRRRawInput findRawInputArgument(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof XIRRRawInput rawInput) {
                return rawInput;
            }
        }
        return null;
    }
}
