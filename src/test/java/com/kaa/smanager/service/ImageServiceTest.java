package com.kaa.smanager.service;

import com.kaa.smanager.exception.BadRequestException;
import com.kaa.smanager.model.Image;
import com.kaa.smanager.repository.ImageRepository;
import com.kaa.smanager.uitl.ErrorCatalog;
import com.kaa.smanager.uitl.RepositoryUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

public class ImageServiceTest {
    private ImageRepository imageRepository;
    private RepositoryUtil repositoryUtil;
    private ImageService imageService;

    @BeforeEach
    public void init() {
        imageRepository = Mockito.mock(ImageRepository.class);
        repositoryUtil = Mockito.mock(RepositoryUtil.class);
        imageService = new ImageService(imageRepository, repositoryUtil);
    }

    @Test
    public void addImageTest() {
        var image = new Image(null, "url/test/image.png", 10, Timestamp.valueOf(LocalDateTime.now()), 1L);
        var result = new Image(1L, "url/test/image.png", 10, Timestamp.valueOf(LocalDateTime.now()), 1L);
        Mockito.when(imageRepository.save(image)).thenReturn(Mono.just(result));
        var argumentCaptor = ArgumentCaptor.forClass(Image.class);

        StepVerifier.create(imageService.addImage(image))
                .expectNextMatches(result::equals).verifyComplete();

        Mockito.verify(imageRepository, Mockito.times(1)).save(argumentCaptor.capture());
        Assertions.assertEquals(argumentCaptor.getValue(), image);
    }

    @Test
    public void addImageWithEmptyUrlError() {
        var image = new Image();
        Assertions.assertEquals(ErrorCatalog.NO_IMAGE_URL, Assertions
                .assertThrowsExactly(BadRequestException.class, () -> imageService.addImage(image).subscribe()).getMessage());
    }

    @Test
    public void addImageWithNoPictureUrlError() {
        var image = Image.builder().url("no image(").build();
        Assertions.assertEquals(ErrorCatalog.NO_PICTURE_SOURCE_ON_URL, Assertions
                .assertThrowsExactly(BadRequestException.class, () -> imageService.addImage(image).subscribe()).getMessage());
    }

    @Test
    public void deleteImageTest() {
        var id = 1L;
        Mockito.when(imageRepository.deleteById(id)).thenReturn(Mono.empty());
        StepVerifier.create(imageService.deleteImage(id)).verifyComplete();
        Mockito.verify(imageRepository, Mockito.times(1)).deleteById(id);
    }

    @Test
    public void getBySlideshowIdTest() {
        var id = 10L;
        var result = new Image(1L, "url/test/image.png", 10, Timestamp.valueOf(LocalDateTime.now()), id);
        var fluxResult = Flux.fromIterable(List.of(result));
        Mockito.when(repositoryUtil.provideSearchByExampleAndSort(any(Example.class), any(Sort.class), any())).thenReturn(fluxResult);
        StepVerifier.create(imageService.getBySlideshow(id)).expectNext(result).verifyComplete();
    }
}
