package com.kaa.smanager.handler;

import com.kaa.smanager.exception.BadRequestException;
import com.kaa.smanager.model.Image;
import com.kaa.smanager.service.SlideshowService;
import com.kaa.smanager.uitl.ErrorCatalog;
import io.netty.util.IllegalReferenceCountException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Component
public class SlideshowHandler {

    private final SlideshowService slideshowService;

    public SlideshowHandler(SlideshowService slideshowService) {
        this.slideshowService = slideshowService;
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        try {
            return request
                    .bodyToFlux(Image.class)
                    .collect(Collectors.toList())
                    .flatMap(images -> {
                        var totalDuration = images
                                .stream()
                                .collect(Collectors.summarizingInt(Image::getDuration))
                                .getSum();
                        return slideshowService.addSlideshow(images, totalDuration);
                    })
                    .flatMap(slideshow -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(slideshow));
        } catch (IllegalReferenceCountException e) {
            throw new BadRequestException("No images array in the request body!");
        }
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        try {
            return slideshowService.deleteSlideshow(Long.valueOf(request.pathVariable("id")))
                    .flatMap(empty -> ServerResponse.ok().build());
        } catch (NumberFormatException e) {
            return Mono.error(new BadRequestException(ErrorCatalog.WRONG_ID));
        }
    }

    public Mono<ServerResponse> createProofOfPlay(ServerRequest request) {
        try {
            var slideshowId = Long.valueOf(request.pathVariable("id"));
            var imageId = Long.valueOf(request.pathVariable("imageId"));
            return slideshowService.addProofOfPlay(slideshowId, imageId)
                    .flatMap(proofOfPlay -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(proofOfPlay));
        } catch (NumberFormatException e) {
            return Mono.error(new BadRequestException(ErrorCatalog.WRONG_ID));
        }
    }
}
