package net.prettyrandom.spring.thrift.aop;

import io.micrometer.core.instrument.*;
import lombok.RequiredArgsConstructor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MetricsThriftMethodInterceptor implements MethodInterceptor {
    private final MeterRegistry meterRegistry;

    public MetricsThriftMethodInterceptor(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        final Timer.Sample timer = Timer.start(meterRegistry);

        try {
            final Object result = invocation.proceed();
            timer.stop(meterRegistry.timer("thrift_handler", getTags(invocation)));
            return result;
        } catch (Exception e) {
            timer.stop(meterRegistry.timer("thrift_handler", getTags(invocation, e)));
            throw e;
        }
    }

    private Iterable<Tag> getTags(MethodInvocation invocation) {
        return getTags(invocation, null);
    }

    private Iterable<Tag> getTags(MethodInvocation invocation, Exception exception) {
        List<Tag> tags = new LinkedList<>(Arrays.asList(
                new ImmutableTag("handler", invocation.getThis().getClass().getCanonicalName()),
                new ImmutableTag("method", invocation.getMethod().getName())
        ));
        if (exception == null) {
            tags.add(new ImmutableTag("status", "ok"));
        } else {
            tags.add(new ImmutableTag("status", "error"));
            tags.add(new ImmutableTag("exception", exception.getClass().getCanonicalName()));
        }
        return tags;
    }
}
