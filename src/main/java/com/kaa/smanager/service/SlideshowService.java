package com.kaa.smanager.service;

import com.kaa.smanager.model.Image;
import com.kaa.smanager.model.ProofOfPlay;
import com.kaa.smanager.model.Slideshow;
import com.kaa.smanager.repository.ImageRepository;
import com.kaa.smanager.repository.ProofOfPlayRepository;
import com.kaa.smanager.repository.SlideshowRepository;
import com.kaa.smanager.uitl.RepositoryUtil;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Objects;

@Service
public class SlideshowService {

    private final SlideshowRepository slideshowRepository;
    private final ImageRepository imageRepository;
    private final ProofOfPlayRepository proofOfPlayRepository;
    private final RepositoryUtil repositoryUtil;

    public SlideshowService(SlideshowRepository slideshowRepository, ImageRepository imageRepository, ProofOfPlayRepository proofOfPlayRepository, RepositoryUtil repositoryUtil) {
        this.slideshowRepository = slideshowRepository;
        this.imageRepository = imageRepository;
        this.proofOfPlayRepository = proofOfPlayRepository;
        this.repositoryUtil = repositoryUtil;
    }

    @Transactional
    public Mono<Slideshow> addSlideshow(Collection<Image> images, Long totalDuration) {
        return slideshowRepository.save(new Slideshow(totalDuration))
                .publishOn(Schedulers.boundedElastic())
                .doOnSuccess(slideshow -> {
            images.forEach(image -> Objects.requireNonNull(image).setSlideshowId(slideshow.getId()));
            repositoryUtil.provideBatchRepositoryOperation(images, imageRepository::saveAll).subscribe();
        });
    }

    @Transactional
    public Mono<Void> deleteSlideshow(Long id) {
        return repositoryUtil.provideBatchRepositoryOperation(
                repositoryUtil.provideSearchByExampleAndSort(Example.of(Image.builder().slideshowId(id).build(),
                ExampleMatcher
                        .matchingAny()
                        .withMatcher("slideshowId", ExampleMatcher.GenericPropertyMatchers.exact())), Sort.unsorted(), imageRepository::findAll),
                        imageRepository::deleteAll)
                .then(slideshowRepository.deleteById(id));
    }

    public Mono<ProofOfPlay> addProofOfPlay(Long slideshowId, Long imageId) {
        return proofOfPlayRepository.save(new ProofOfPlay(slideshowId, imageId, new Timestamp(System.nanoTime())));
    }
}