package com.example.session8_ex3.aspect;

import com.example.session8_ex3.annotation.RequireOtp;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class OtpVerificationAspect {

    private static final String VALID_OTP = "123456";

    @Around("@annotation(requireOtp)")
    public Object verifyOtp(ProceedingJoinPoint joinPoint, RequireOtp requireOtp) throws Throwable {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            throw new SecurityException("OTP verification failed: No HTTP request context available.");
        }

        HttpServletRequest request = attributes.getRequest();
        String otpHeader = request.getHeader("X-OTP");

        if (otpHeader == null || !otpHeader.equals(VALID_OTP)) {
            throw new SecurityException("OTP verification failed: Invalid or missing OTP. Transaction rejected.");
        }

        return joinPoint.proceed();
    }
}
