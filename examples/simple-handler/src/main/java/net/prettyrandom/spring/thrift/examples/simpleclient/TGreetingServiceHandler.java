package net.prettyrandom.spring.thrift.examples.simpleclient;

import example.TGreetingService;
import example.TName;
import org.apache.thrift.TException;
import net.prettyrandom.spring.thrift.annotation.ThriftController;

/**
 * Created by aleksandr on 01.09.15.
 */
@ThriftController("/api")
public class TGreetingServiceHandler implements TGreetingService.Iface {

    @Override
    public String greet(TName name) throws TException {
        StringBuilder result = new StringBuilder();

        result.append("Hello ");

        if(name.isSetStatus()) {
            result.append(org.springframework.util.StringUtils.capitalize(name.getStatus().name().toLowerCase()));
            result.append(" ");
        }

        result.append(name.getFirstName());
        result.append(" ");
        result.append(name.getSecondName());

        return result.toString();
    }
}
