package com.marware.ecommerce.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class LoggingAspectTest {

    private LoggingAspect aspect;
    private ProceedingJoinPoint pjp;
    private Signature signature;

    @BeforeEach
    void setUp() {
        aspect = new LoggingAspect();
        pjp = mock(ProceedingJoinPoint.class);
        signature = mock(Signature.class);
    }

    @Test
    void cuandoProceedDevuelveValor_entoncesLogExecutionRetornaEseValor() throws Throwable {
        // 1) Stub del signature.toShortString() y de los args
        when(pjp.getSignature()).thenReturn(signature);
        when(signature.toShortString()).thenReturn("MyClass.myMethod(arg)");
        when(pjp.getArgs()).thenReturn(new Object[]{"foo", 123});

        // 2) Stub de proceed()
        when(pjp.proceed()).thenReturn("¡OK!");

        // 3) Llamada al aspecto
        Object resultado = aspect.logExecution(pjp);

        // 4) Verificaciones
        assertEquals("¡OK!", resultado);
        verify(pjp).proceed();
    }

    @Test
    void cuandoProceedLanzaError_entoncesLogExecutionPropagaLaExcepcion() throws Throwable {
        when(pjp.getSignature()).thenReturn(signature);
        when(signature.toShortString()).thenReturn("MyClass.myMethod()");
        when(pjp.getArgs()).thenReturn(new Object[]{});

        RuntimeException fallo = new RuntimeException("boom");
        when(pjp.proceed()).thenThrow(fallo);

        RuntimeException lanzada = assertThrows(RuntimeException.class, () -> {
            aspect.logExecution(pjp);
        });
        assertSame(fallo, lanzada);
    }
}