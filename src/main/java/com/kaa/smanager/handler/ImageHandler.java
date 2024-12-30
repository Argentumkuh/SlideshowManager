package com.kaa.smanager.handler;

import com.kaa.smanager.exception.BadRequestException;
import com.kaa.smanager.model.Image;
import com.kaa.smanager.service.ImageService;
import com.kaa.smanager.uitl.ErrorCatalog;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Component
public class ImageHandler {
    private final ImageService imageService;

    public ImageHandler(ImageService imageService) {
        this.imageService = imageService;
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        // Slideshow id null validation implemented as DB constraint in DB init script.
        // The best practice may be to implement double check verification on backend layer (to manage it more precisely)
        // But let it be for now)
        return request.bodyToMono(Image.class)
                .flatMap(imageService::addImage)
                .flatMap(image -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(image))
                .doOnError(e -> {
                     if (e instanceof DataIntegrityViolationException){
                        throw new BadRequestException(ErrorCatalog.NO_SLIDESHOW_ID);
                    }
                });
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        try {
            return imageService.deleteImage(Long.valueOf(request.pathVariable("id")))
                    .flatMap(empty -> ServerResponse.ok().build());
        } catch (NumberFormatException e) {
            return Mono.error(new BadRequestException(ErrorCatalog.WRONG_ID));
        }
    }

    public Mono<ServerResponse> search(ServerRequest request) {
        try {
            var example = new Image();
            request.queryParam("url").ifPresent(example::setUrl);
            request.queryParam("duration").ifPresent(duration -> example.setDuration(Integer.parseInt(duration)));
            return imageService.searchImages(example).collect(Collectors.toList())
                    .flatMap(images -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(images));
        } catch (NumberFormatException e) {
            return Mono.error(new BadRequestException(ErrorCatalog.WRONG_DURATION));
        }
    }

    public Mono<ServerResponse> searchBySlideshow(ServerRequest request) {
        try {
            return imageService.getBySlideshow(Long.valueOf(request.pathVariable("id"))).collect(Collectors.toList())
                    .flatMap(images -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(images));
        } catch (NumberFormatException e) {
            return Mono.error(new BadRequestException(ErrorCatalog.WRONG_ID));
        }
    }

}
