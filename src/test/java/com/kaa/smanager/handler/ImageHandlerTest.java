package com.kaa.smanager.handler;

import com.kaa.smanager.exception.BadRequestException;
import com.kaa.smanager.model.Image;
import com.kaa.smanager.service.ImageService;
import com.kaa.smanager.uitl.ErrorCatalog;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
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

import static org.mockito.ArgumentMatchers.any;

public class ImageHandlerTest {
    private ImageService imageService;
    private ImageHandler imageHandler;

    @BeforeEach
    public void init() {
        imageService = Mockito.mock(ImageService.class);
        imageHandler = new ImageHandler(imageService);
    }

    @Test
    public void createTest() {
        var image = new Image(1L, "url/test/image.png", 10, Timestamp.valueOf(LocalDateTime.now()), 1L);
        Mockito.when(imageService.addImage(image)).thenReturn(Mono.just(image));
        var mockRequest = MockServerRequest.builder().body(Mono.just(image));

        StepVerifier.create(imageHandler.create(mockRequest)).expectNextMatches(response ->
                response.statusCode().value() == 200
                && response.headers().get(HttpHeaders.CONTENT_TYPE).contains(MediaType.APPLICATION_JSON.toString())
                && ((EntityResponse)response).entity().equals(image)).verifyComplete();
    }

    @Test
    public void createWithErrorTest() {
        var image = new Image(1L, "url/test/image.png", 10, Timestamp.valueOf(LocalDateTime.now()), null);
        Mockito.when(imageService.addImage(image)).thenThrow(new DataIntegrityViolationException(""));
        var mockRequest = MockServerRequest.builder().body(Mono.just(image));

        StepVerifier.create(imageHandler.create(mockRequest)).expectErrorMatches(error ->
                error instanceof BadRequestException && ErrorCatalog.NO_SLIDESHOW_ID.equals(error.getMessage())).verify();
    }

    //Of course URI should be gotten from server config properties, and route mappings are from constants file, but not now))
    @Test
    public void deleteTest() {
        var builder = new StringBuilder();
        var uri = URI.create(builder.append("http://localhost:8080/").append("deleteImage/").toString());
        var imageId = 1L;
        var mockRequest = MockServerRequest.builder().uri(uri).pathVariable("id", String.valueOf(imageId)).build();
        Mockito.when(imageService.deleteImage(imageId)).thenReturn(Mono.empty());

        imageHandler.delete(mockRequest)
                .subscribe(response -> Assertions.assertEquals(200, response.statusCode().value()));
        Mockito.verify(imageService, Mockito.times(1)).deleteImage(imageId);
    }

    @Test
    public void deleteWithWrongIdTest() {
        var builder = new StringBuilder();
        var uri = URI.create(builder.append("http://localhost:8080/").append("deleteImage/").toString());
        var mockRequest = MockServerRequest.builder().uri(uri).pathVariable("id", "wrongId").build();

        StepVerifier.create(imageHandler.delete(mockRequest))
                .expectErrorMatches(error -> error instanceof BadRequestException && ErrorCatalog.WRONG_ID.equals(error.getMessage()))
                .verify();
    }

    @Test
    public void searchTest() {
        var slideshowId = 1L;
        var image1 = new Image(2L, "url/test/image1.png", 5, Timestamp.valueOf(LocalDateTime.now()), slideshowId);
        var image2 = new Image(3L, "url/test/image2.png", 5, Timestamp.valueOf(LocalDateTime.now()), slideshowId);
        var builder = new StringBuilder();
        var uri = URI.create(builder.append("http://localhost:8080/").append("images/search").toString());
        var searchUrl = "url/test/";
        var duration = 5;
        var mockRequest = MockServerRequest.builder().uri(uri)
                .queryParam("url", searchUrl).queryParam("duration", String.valueOf(duration)).build();
        var captor = ArgumentCaptor.forClass(Image.class);
        Mockito.when(imageService.searchImages(any(Image.class))).thenReturn(Flux.just(image1, image2));

        StepVerifier.create(imageHandler.search(mockRequest)).expectNextMatches(response -> response.statusCode().value() == 200
                && response.headers().get(HttpHeaders.CONTENT_TYPE).contains(MediaType.APPLICATION_JSON.toString())
                && ((EntityResponse)response).entity().equals(List.of(image1, image2))).verifyComplete();
        Mockito.verify(imageService, Mockito.times(1))
                .searchImages(captor.capture());
        Assertions.assertEquals(captor.getValue().getUrl(), searchUrl);
        Assertions.assertEquals(captor.getValue().getDuration(), duration);
    }

    @Test
    public void searchWithDurationIdTest() {
        var builder = new StringBuilder();
        var uri = URI.create(builder.append("http://localhost:8080/").append("images/search").toString());
        var mockRequest = MockServerRequest.builder().uri(uri).queryParam("duration", "wrongDuration").build();

        StepVerifier.create(imageHandler.search(mockRequest))
                .expectErrorMatches(error -> error instanceof BadRequestException && ErrorCatalog.WRONG_DURATION.equals(error.getMessage()))
                .verify();
    }

    @Test
    public void searchBySlideshowTest() {
        var slideshowId = 1L;
        var image1 = new Image(2L, "url/test/image1.png", 5, Timestamp.valueOf(LocalDateTime.now()), slideshowId);
        var image2 = new Image(3L, "url/test/image2.png", 5, Timestamp.valueOf(LocalDateTime.now()), slideshowId);
        var builder = new StringBuilder();
        var uri = URI.create(builder.append("http://localhost:8080/").append("/slideshow").append("/").append(slideshowId).append("/slideshowOrder").toString());
        var mockRequest = MockServerRequest.builder().uri(uri).pathVariable("id" , String.valueOf(slideshowId)).build();
        Mockito.when(imageService.getBySlideshow(any(Long.class))).thenReturn(Flux.just(image1, image2));

        StepVerifier.create(imageHandler.searchBySlideshow(mockRequest)).expectNextMatches(response -> response.statusCode().value() == 200
                && response.headers().get(HttpHeaders.CONTENT_TYPE).contains(MediaType.APPLICATION_JSON.toString())
                && ((EntityResponse)response).entity().equals(List.of(image1, image2))).verifyComplete();
        Mockito.verify(imageService, Mockito.times(1)).getBySlideshow(slideshowId);
    }

    @Test
    public void searchBySlideshowWithWrongIdTest() {
        var wrongId = "wrongId";
        var builder = new StringBuilder();
        var uri = URI.create(builder.append("http://localhost:8080/").append("/slideshow").append("/").append(wrongId).append("/slideshowOrder").toString());
        var mockRequest = MockServerRequest.builder().uri(uri).pathVariable("id", wrongId).build();

        StepVerifier.create(imageHandler.searchBySlideshow(mockRequest))
                .expectErrorMatches(error -> error instanceof BadRequestException && ErrorCatalog.WRONG_ID.equals(error.getMessage()))
                .verify();
    }
}
