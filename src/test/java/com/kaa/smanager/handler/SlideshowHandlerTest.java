package com.kaa.smanager.handler;

import com.kaa.smanager.exception.BadRequestException;
import com.kaa.smanager.model.Image;
import com.kaa.smanager.model.ProofOfPlay;
import com.kaa.smanager.model.Slideshow;
import com.kaa.smanager.service.SlideshowService;
import com.kaa.smanager.uitl.ErrorCatalog;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.EntityResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public class SlideshowHandlerTest {
    private SlideshowService slideshowService;
    private SlideshowHandler slideshowHandler;

    public SlideshowHandlerTest() {
    }

    @BeforeEach
    public void init() {
        slideshowService = Mockito.mock(SlideshowService.class);
        slideshowHandler = new SlideshowHandler(slideshowService);
    }

    @Test
    public void createTest() {
        var duration1 = 5;
        var duration2 = 10;
        var image1 = new Image(null, "url/test/image1.png", duration1, Timestamp.valueOf(LocalDateTime.now()), null);
        var image2 = new Image(null, "url/test/image2.png", duration2, Timestamp.valueOf(LocalDateTime.now()), null);
        var slideshow = new Slideshow(1L, duration1 + duration2);
        Mockito.when(slideshowService.addSlideshow(List.of(image1, image2), (long) (duration1 + duration2))).thenReturn(Mono.just(slideshow));
        var mockRequest = MockServerRequest.builder().body(Flux.just(image1, image2));

        StepVerifier.create(slideshowHandler.create(mockRequest)).expectNextMatches(response ->
                response.statusCode().value() == 200
                        && response.headers().get(HttpHeaders.CONTENT_TYPE).contains(MediaType.APPLICATION_JSON.toString())
                        && ((EntityResponse)response).entity().equals(slideshow)).verifyComplete();
    }

    @Test
    public void deleteTest() {
        var builder = new StringBuilder();
        var slideshowId = 1L;
        var uri = URI.create(builder.append("http://localhost:8080/").append("deleteSlideshow/").append(slideshowId).toString());
        var mockRequest = MockServerRequest.builder().uri(uri).pathVariable("id", String.valueOf(slideshowId)).build();
        Mockito.when(slideshowService.deleteSlideshow(slideshowId)).thenReturn(Mono.empty());

        slideshowHandler.delete(mockRequest).subscribe(response -> Assertions.assertEquals(response.statusCode().value(), 200));
        Mockito.verify(slideshowService, Mockito.times(1)).deleteSlideshow(slideshowId);
    }

    @Test
    public void deleteWithWrongIdTest() {
        var slideshowId = "wrongId";
        var builder = new StringBuilder();
        var uri = URI.create(builder.append("http://localhost:8080/").append("deleteSlideshow/").toString());
        var mockRequest = MockServerRequest.builder().uri(uri).pathVariable("id", slideshowId).build();

        StepVerifier.create(slideshowHandler.delete(mockRequest))
                .expectErrorMatches(error -> error instanceof BadRequestException && ErrorCatalog.WRONG_ID.equals(error.getMessage()))
                .verify();
    }

    @Test
    public void createProofOfPlayTest() {
        var slideshowId = 1L;
        var imageId = 2L;
        var builder = new StringBuilder();
        var uri = URI.create(builder.append("http://localhost:8080/").append("slideshow/")
                .append(slideshowId).append("/proof-of-play/").append(imageId).toString());
        var result = new ProofOfPlay(slideshowId, imageId, new Timestamp(System.nanoTime()));
        var mockRequest = MockServerRequest.builder().uri(uri)
                .pathVariable("id", String.valueOf(slideshowId)).pathVariable("imageId", String.valueOf(imageId)).build();
        Mockito.when(slideshowService.addProofOfPlay(slideshowId, imageId)).thenReturn(Mono.just(result));

        StepVerifier.create(slideshowHandler.createProofOfPlay(mockRequest)).expectNextMatches(response ->
                response.statusCode().value() == 200
                        && response.headers().get(HttpHeaders.CONTENT_TYPE).contains(MediaType.APPLICATION_JSON.toString())
                        && ((EntityResponse)response).entity().equals(result)).verifyComplete();
    }

    @Test
    public void createProofOfPlayWithWrongSlideshowIdTest() {
        var slideshowId = "wrongId";
        var imageId = 1L;
        var builder = new StringBuilder();
        var uri = URI.create(builder.append("http://localhost:8080/").append("slideshow/")
                .append(slideshowId).append("/proof-of-play/").append(imageId).toString());
        var mockRequest = MockServerRequest.builder().uri(uri)
                .pathVariable("id", slideshowId).pathVariable("imageId", String.valueOf(imageId)).build();

        StepVerifier.create(slideshowHandler.createProofOfPlay(mockRequest))
                .expectErrorMatches(error -> error instanceof BadRequestException && ErrorCatalog.WRONG_ID.equals(error.getMessage()))
                .verify();
    }

    @Test
    public void createProofOfPlayWithWrongImageIdTest() {
        var slideshowId = 1L;
        var imageId = "wrongId";
        var builder = new StringBuilder();
        var uri = URI.create(builder.append("http://localhost:8080/").append("slideshow/")
                .append(slideshowId).append("/proof-of-play/").append(imageId).toString());
        var mockRequest = MockServerRequest.builder().uri(uri)
                .pathVariable("id", String.valueOf(slideshowId)).pathVariable("imageId", imageId).build();

        StepVerifier.create(slideshowHandler.createProofOfPlay(mockRequest))
                .expectErrorMatches(error -> error instanceof BadRequestException && ErrorCatalog.WRONG_ID.equals(error.getMessage()))
                .verify();
    }
}
