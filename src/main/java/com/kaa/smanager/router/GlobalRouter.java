package com.kaa.smanager.router;

import com.kaa.smanager.handler.ImageHandler;
import com.kaa.smanager.handler.SlideshowHandler;
import com.kaa.smanager.model.Image;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.Explode;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
//All route mappings should be gathered into constants file. May be... next time)
//This class should be tested as part of integration testing
public class GlobalRouter {

    @Bean
    @RouterOperations({@RouterOperation(path = "/addImage", beanClass = ImageHandler.class, beanMethod = "create", operation = @Operation(
            operationId = "opAddImage", requestBody = @RequestBody(required = true, content = @Content(schema = @Schema(implementation = Image.class))))),
        @RouterOperation(path = "/deleteImage/{id}", beanClass = ImageHandler.class, beanMethod = "delete", operation = @Operation(
            operationId =  "opDeleteImage", method = "DELETE",
            parameters = @Parameter(name = "id", in = ParameterIn.PATH, style = ParameterStyle.SIMPLE, explode = Explode.FALSE, required = true))),
        @RouterOperation(path = "/images/search", beanClass = ImageHandler.class, beanMethod = "search", operation = @Operation(
            operationId = "opSearchImage", parameters = {@Parameter(name = "url", in = ParameterIn.QUERY), @Parameter(name = "duration", in = ParameterIn.QUERY)})),
        @RouterOperation(path = "/slideshow/{id}/slideshowOrder", beanClass = ImageHandler.class, beanMethod = "delete", operation = @Operation(
            operationId =  "opSearchImageBySlideshow", method = "GET",
            parameters = @Parameter(name = "id", in = ParameterIn.PATH, style = ParameterStyle.SIMPLE, explode = Explode.FALSE, required = true)))
    })
    public RouterFunction<ServerResponse> imageRouter(ImageHandler handler) {
        return RouterFunctions.route(POST("/addImage"), handler::create)
                .andRoute(DELETE("/deleteImage/{id}"), handler::delete)
                .andRoute(GET("/images/search"), handler::search)
                .andRoute(GET("/slideshow/{id}/slideshowOrder"), handler::searchBySlideshow);
    }

    @Bean
    @RouterOperations({@RouterOperation(path = "/addSlideshow", beanClass = SlideshowHandler.class, beanMethod = "create", operation = @Operation(
            operationId = "opAddSlideshow", requestBody = @RequestBody(required = true, content = @Content(array = @ArraySchema(schema = @Schema(implementation = Image.class)))))),
        @RouterOperation(path = "/deleteSlideshow/{id}", beanClass = SlideshowHandler.class, beanMethod = "delete", operation = @Operation(
            operationId =  "opDeleteSlideshow", method = "DELETE",
            parameters = @Parameter(name = "id", in = ParameterIn.PATH, style = ParameterStyle.SIMPLE, explode = Explode.FALSE, required = true))),
        @RouterOperation(path = "/slideshow/{id}/proof-of-play/{imageId}", beanClass = SlideshowHandler.class, beanMethod = "createProofOfPlay", operation = @Operation(
            operationId =  "opAddProofOfPlay", method = "POST",
            parameters = {@Parameter(name = "id", in = ParameterIn.PATH, style = ParameterStyle.SIMPLE, explode = Explode.FALSE, required = true),
                    @Parameter(name = "imageId", in = ParameterIn.PATH, style = ParameterStyle.SIMPLE, explode = Explode.FALSE, required = true)}))
    })
    public RouterFunction<ServerResponse> slideshowRouter(SlideshowHandler handler) {
        return RouterFunctions.route(POST("/addSlideshow"), handler::create)
                .andRoute(DELETE("/deleteSlideshow/{id}"), handler::delete)
                .andRoute(POST("/slideshow/{id}/proof-of-play/{imageId}"), handler::createProofOfPlay);
    }
}
