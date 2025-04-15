package com.zzx.aspect;

import com.google.gson.Gson;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * WebLogAspect 是一个面向切面编程（AOP）的类，用于记录 Web 请求的日志。
 * 它在方法执行前后以及环绕方法执行时记录日志，包括请求的 URL、方法描述、HTTP 方法、类方法、IP 地址、请求参数、响应参数和耗时等信息。
 * 该类使用 Spring 的 @Component 注解标记为一个 Spring 组件，并使用 @Aspect 注解标记为一个切面类。
 * @Order 注解定义了该切面的执行顺序，数字越小优先级越高。
 */
@Aspect
@Component
@Order(1)
@Profile({"dev","prod"})
public class WebLogAspect {

    /**
     * 定义一个日志记录器，用于记录 WebLogAspect 类的日志。
     */
    private static final Logger logger = LoggerFactory.getLogger(WebLogAspect.class);

    /**
     * 定义一个系统换行符常量，用于格式化日志输出。
     */
    private static final String LINE_SEPARATOR = System.lineSeparator();

    /**
     * 定义一个切点，以自定义 @WebLog 注解为切点。
     * 该切点使用 @Pointcut 注解标记，表示匹配带有 @WebLog 注解的方法。
     */
    @Pointcut("@annotation(com.zzx.aspect.WebLog)")
    public void webLogPointcut() {
    }

    /**
     * 定义另一个切点，使用 execution 表达式拦截 com.zzx.controller 包及其子包下的所有方法。
     * 该切点也使用 @Pointcut 注解标记。
     */
    @Pointcut("execution(* com.zzx.controller..*.*(..))")
    public void controllerMethodsPointcut() {
    }

    /**
     * 在带有 @WebLog 注解的方法执行前执行的前置通知。
     * 该方法记录请求的详细信息，包括 URL、方法描述、HTTP 方法、类方法、IP 地址和请求参数。
     * @param joinPoint 切点对象，包含方法执行的上下文信息。
     */
    @Before("webLogPointcut()")
    public void doBeforeWithWebLog(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        String methodDescription = getMethodDescription(joinPoint);

        logger.info("========================================== Start (WebLog) ===========================================");
        logger.info("URL            : {}", request.getRequestURL());
        logger.info("Description    : {}", methodDescription);
        logger.info("HTTP Method    : {}", request.getMethod());
        logger.info("Class Method   : {}.{}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
        logger.info("IP             : {}", request.getRemoteAddr());
        logger.info("Request Args   : {}", new Gson().toJson(joinPoint.getArgs()));
    }

    /**
     * 在 com.zzx.controller 包及其子包下的所有方法执行前执行的前置通知。
     * 该方法记录请求的详细信息，包括 URL、HTTP 方法、类方法、IP 地址和请求参数。
     * @param joinPoint 切点对象，包含方法执行的上下文信息。
     */
    @Before("controllerMethodsPointcut()")
    public void doBeforeWithControllerMethods(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        logger.info("========================================== Start (Controller Methods) ===========================================");
        logger.info("URL            : {}", request.getRequestURL());
        logger.info("HTTP Method    : {}", request.getMethod());
        logger.info("Class Method   : {}.{}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
        logger.info("IP             : {}", request.getRemoteAddr());
        logger.info("Request Args   : {}", new Gson().toJson(joinPoint.getArgs()));
    }

    /**
     * 环绕通知，用于在带有 @WebLog 注解的方法执行前后执行。
     * 该方法记录方法的执行时间以及响应参数。
     * @param joinPoint 切点对象，包含方法执行的上下文信息。
     * @return 方法的返回值。
     * @throws Throwable 如果方法执行过程中抛出异常，该异常将被抛出。
     */
    @Around("webLogPointcut()")
    public Object doAroundWithWebLog(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();

        logger.info("Response Args  : {}", new Gson().toJson(result));
        logger.info("Time-Consuming : {} ms", System.currentTimeMillis() - startTime);

        return result;
    }

    /**
     * 环绕通知，用于在 com.zzx.controller 包及其子包下的所有方法执行前后执行。
     * 该方法记录方法的执行时间以及响应参数。
     * @param joinPoint 切点对象，包含方法执行的上下文信息。
     * @return 方法的返回值。
     * @throws Throwable 如果方法执行过程中抛出异常，该异常将被抛出。
     */
    @Around("controllerMethodsPointcut()")
    public Object doAroundWithControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();

        logger.info("Response Args  : {}", new Gson().toJson(result));
        logger.info("Time-Consuming : {} ms", System.currentTimeMillis() - startTime);

        return result;
    }

    /**
     * 后置通知，用于在带有 @WebLog 注解的方法或 com.zzx.controller 包及其子包下的所有方法执行后执行。
     * 该方法记录一个结束标志。
     */
    @After("webLogPointcut() || controllerMethodsPointcut()")
    public void doAfter() {
        logger.info("=========================================== End ===========================================" + LINE_SEPARATOR);
    }

    /**
     * 获取方法的描述信息。
     * 该方法检查方法是否带有 @WebLog 注解，如果有，则返回注解中的描述信息；否则返回空字符串。
     * @param joinPoint 切点对象，包含方法执行的上下文信息。
     * @return 方法的描述信息。
     */
    private String getMethodDescription(JoinPoint joinPoint) {
        Method method = getMethod(joinPoint);
        if (method != null && method.isAnnotationPresent(WebLog.class)) {
            return method.getAnnotation(WebLog.class).description();
        }
        return "";
    }

    /**
     * 获取方法对象。
     * 该方法从切点对象中提取方法签名，并尝试获取对应的方法对象。
     * @param joinPoint 切点对象，包含方法执行的上下文信息。
     * @return 方法对象，如果获取失败则返回 null。
     */
    private Method getMethod(JoinPoint joinPoint) {
        try {
            Signature signature = joinPoint.getSignature();
            if (signature instanceof MethodSignature) {
                MethodSignature methodSignature = (MethodSignature) signature;
                return methodSignature.getMethod();
            }
            return null;
        } catch (Exception e) {
            logger.error("Failed to get method description", e);
            return null;
        }
    }
}