package com.kaa.smanager.service;

import com.kaa.smanager.exception.BadRequestException;
import com.kaa.smanager.model.Image;
import com.kaa.smanager.repository.ImageRepository;
import com.kaa.smanager.uitl.Const;
import com.kaa.smanager.uitl.ErrorCatalog;
import com.kaa.smanager.uitl.RepositoryUtil;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Service
public class ImageService {
    private final ImageRepository imageRepository;
    private final RepositoryUtil repositoryUtil;

    public ImageService(ImageRepository imageRepository, RepositoryUtil repositoryUtil) {
        this.imageRepository = imageRepository;
        this.repositoryUtil = repositoryUtil;
    }

    public Mono<Image> addImage(final Image image) {
        validateUrl(image.getUrl());
        return imageRepository.save(image);
    }

    private void validateUrl(String url) {
        if (url == null) {
            throw new BadRequestException(ErrorCatalog.NO_IMAGE_URL);
        }
        // Of course URL shortener or some specific address routing can be used.
        // In such case it is only possible to validate making the request to URl and check content type header
        // Also URL structure and syntax, like correct protocol, can be checked here
        var splitted = url.split("\\.");
        if (Arrays.stream(Const.imageExtensions).noneMatch(ext -> ext.equalsIgnoreCase(splitted[splitted.length - 1]))) {
            throw new BadRequestException(ErrorCatalog.NO_PICTURE_SOURCE_ON_URL);
        }
    }

    public Mono<Void> deleteImage(Long id) {
        return imageRepository.deleteById(id);
    }

    public Flux<Image> searchImages(final Image example) {
        return imageRepository.findAll(Example.of(example, ExampleMatcher
                .matchingAny()
                    .withMatcher("url", ExampleMatcher.GenericPropertyMatcher.of(ExampleMatcher.StringMatcher.CONTAINING))
                    .withMatcher("duration", ExampleMatcher.GenericPropertyMatchers.exact())
        ));
    }

    public Flux<Image> getBySlideshow(Long id) {
        return repositoryUtil.provideSearchByExampleAndSort(
                Example.of(Image.builder().slideshowId(id).build(),
                    ExampleMatcher
                        .matchingAny()
                        .withMatcher("slideshowId", ExampleMatcher.GenericPropertyMatchers.exact())),
                Sort.by(Sort.Order.asc("addedOn")),
                imageRepository::findAll);
    }
}
