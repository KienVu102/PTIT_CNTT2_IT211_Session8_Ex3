package com.example.session8_ex3.aspect;

import com.example.session8_ex3.annotation.RequireManagerApproval;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Aspect that intercepts methods annotated with @RequireManagerApproval.
 * Uses @annotation() pointcut (NOT execution()) for encapsulation and flexibility.
 *
 * Validates the role from the "X-Role" HTTP header — only "MANAGER" is allowed.
 */
@Aspect
@Component
public class ManagerApprovalAspect {

    @Around("@annotation(requireManagerApproval)")
    public Object verifyManagerRole(ProceedingJoinPoint joinPoint,
                                    RequireManagerApproval requireManagerApproval) throws Throwable {
        // Retrieve the current HTTP request from the Spring context
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            throw new SecurityException("Manager approval failed: No HTTP request context available.");
        }

        HttpServletRequest request = attributes.getRequest();
        String roleHeader = request.getHeader("X-Role");

        if (roleHeader == null || !roleHeader.equals("MANAGER")) {
            throw new SecurityException(
                    "Security Error: Access denied. Only users with MANAGER role can process refunds.");
        }

        // Role is valid — proceed with the original method
        return joinPoint.proceed();
    }
}
