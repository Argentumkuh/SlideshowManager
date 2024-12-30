package com.kaa.smanager.handler.exceptionhadler;

import com.kaa.smanager.exception.BadRequestException;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Map;

@Component
//This class should be tested as part of integration testing
public class GlobalErrorAttributes extends DefaultErrorAttributes {

    private final static String errorKey = "ExceptionHandlingWebHandler.handledException";
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Map<String, Object> map = super.getErrorAttributes(
                request, options);
        var attr = request.exchange().getAttributes();
        //Any checked exception inside application must be caught and rethrow as unchecked!
        var error = (RuntimeException) attr.get(errorKey);
        map.put("status", getStatus(error));
        map.put("message", error.getMessage());
        map.put("error", error.getClass());
        return map;
    }

    private HttpStatus getStatus(RuntimeException e) {
        //Can be extended for various type of server error statuses
        if (e instanceof BadRequestException) {
            return HttpStatus.BAD_REQUEST;
        } else {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }

    }

}
