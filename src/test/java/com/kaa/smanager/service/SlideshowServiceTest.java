package com.kaa.smanager.service;

import com.kaa.smanager.model.Image;
import com.kaa.smanager.model.ProofOfPlay;
import com.kaa.smanager.model.Slideshow;
import com.kaa.smanager.repository.ImageRepository;
import com.kaa.smanager.repository.ProofOfPlayRepository;
import com.kaa.smanager.repository.SlideshowRepository;
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
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.*;

public class SlideshowServiceTest {
    private SlideshowRepository slideshowRepository;
    private ImageRepository imageRepository;
    private ProofOfPlayRepository proofOfPlayRepository;
    private RepositoryUtil repositoryUtil;
    private SlideshowService slideshowService;

    @BeforeEach
    public void init() {
        slideshowRepository = Mockito.mock(SlideshowRepository.class);
        imageRepository = Mockito.mock(ImageRepository.class);
        proofOfPlayRepository = Mockito.mock(ProofOfPlayRepository.class);
        repositoryUtil = Mockito.mock(RepositoryUtil.class);
        slideshowService = new SlideshowService(slideshowRepository, imageRepository, proofOfPlayRepository, repositoryUtil);
    }

    @Test
    public void addSlideshowTest() {
        var duration1 = 10;
        var duration2 = 5;
        var url1 = "url/test/image1.png";
        var url2 = "url/test/image2.png";
        var slideshowId = 1L;
        var image1 = new Image(null, url1, duration1, Timestamp.valueOf(LocalDateTime.now()), null);
        var image2 = new Image(null, url2, duration2, Timestamp.valueOf(LocalDateTime.now()), null);
        var imageResult1 = new Image(2L, url1, duration1, Timestamp.valueOf(LocalDateTime.now()), slideshowId);
        var imageResult2 = new Image(3L, url2, duration1, Timestamp.valueOf(LocalDateTime.now()), slideshowId);
        var result = new Slideshow(slideshowId, duration1 + duration2);
        ArgumentCaptor<Function<List<Image>, Flux<Image>>> captor = ArgumentCaptor.captor();
        Mockito.when(slideshowRepository.save(any(Slideshow.class))).thenReturn(Mono.just(result));
        Mockito.when(repositoryUtil.provideBatchRepositoryOperation(anyCollection(), any()))
                .thenReturn(Flux.fromIterable(List.of(imageResult1, imageResult2)));

        StepVerifier.create(slideshowService.addSlideshow(List.of(image1, image2), (long) (duration1 + duration2)))
                .expectNextMatches(result::equals).verifyComplete();
        Mockito.verify(repositoryUtil, Mockito.times(1)).provideBatchRepositoryOperation(anyList(), captor.capture());
    }

    @Test
    public void deleteSlideshowTest() {
        var slideshowId = 1L;
        var image1 = new Image(2L, "url/test/image1.png", 5, Timestamp.valueOf(LocalDateTime.now()), slideshowId);
        var image2 = new Image(3L, "url/test/image2.png", 10, Timestamp.valueOf(LocalDateTime.now()), slideshowId);
        ArgumentCaptor<Example<Image>> exampleArgument = ArgumentCaptor.captor();
        ArgumentCaptor<BiFunction<Example<Image>, Sort, Flux<Image>>> searchBiFunctionArgument = ArgumentCaptor.captor();
        ArgumentCaptor<Function<Flux<Image>, Mono<Void>>> deleteFunctionArgument = ArgumentCaptor.captor();
        ArgumentCaptor<Flux<Image>> imagesFluxArgument = ArgumentCaptor.captor();
        Mockito.when(slideshowRepository.deleteById(slideshowId)).thenReturn(Mono.empty());
        Mockito.when(repositoryUtil.provideSearchByExampleAndSort(any(), any(), any())).thenReturn(Flux.fromIterable(List.of(image1, image2)));
        Mockito.when(repositoryUtil.provideBatchRepositoryOperation(any(), any())).thenReturn(Mono.empty());

        StepVerifier.create(slideshowService.deleteSlideshow(slideshowId)).verifyComplete();
        Mockito.verify(repositoryUtil, Mockito.times(1))
                .provideSearchByExampleAndSort(exampleArgument.capture(), any(Sort.class), searchBiFunctionArgument.capture());
        Assertions.assertEquals(exampleArgument.getValue().getProbe().getSlideshowId(), slideshowId);
        Mockito.verify(repositoryUtil, Mockito.times(1))
                .provideBatchRepositoryOperation(imagesFluxArgument.capture(), deleteFunctionArgument.capture());
        StepVerifier.create(imagesFluxArgument.getValue()).expectNext(image1, image2).verifyComplete();
    }

    @Test
    public void addProofOfPlayTest() {
        var slideshowId = 1L;
        var imageId = 2L;
        var proofOfPlay = new ProofOfPlay(slideshowId, imageId, new Timestamp(System.nanoTime()));
        Mockito.when(proofOfPlayRepository.save(any(ProofOfPlay.class))).thenReturn(Mono.just(proofOfPlay));
        StepVerifier.create(slideshowService.addProofOfPlay(slideshowId, imageId)).expectNext(proofOfPlay).verifyComplete();
    }
}
