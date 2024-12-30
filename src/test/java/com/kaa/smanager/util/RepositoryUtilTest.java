package com.kaa.smanager.util;

import com.kaa.smanager.model.Image;
import com.kaa.smanager.repository.ImageRepository;
import com.kaa.smanager.uitl.RepositoryUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public class RepositoryUtilTest {

    private RepositoryUtil repositoryUtil;
    private ImageRepository imageRepository;

    @BeforeEach
    public void init() {
        imageRepository = Mockito.mock(ImageRepository.class);
        repositoryUtil = new RepositoryUtil();
    }

    @Test
    public void provideBatchRepositoryOperationTest() {
        var image1 = new Image(2L, "url1", 5, Timestamp.valueOf(LocalDateTime.now()), 1L);
        var image2 = new Image(3L, "url2", 10, Timestamp.valueOf(LocalDateTime.now()), 1L);
        ArgumentCaptor<Collection<Image>> collectionCaptor = ArgumentCaptor.captor();


        repositoryUtil.provideBatchRepositoryOperation(List.of(image1, image2), imageRepository::saveAll);
        Mockito.verify(imageRepository, Mockito.times(1)).saveAll(collectionCaptor.capture());
        Assertions.assertTrue(collectionCaptor.getValue().contains(image1));
        Assertions.assertTrue(collectionCaptor.getValue().contains(image2));
    }

    @Test
    public void provideSearchByExampleAndSortTest() {
        var image = new Image(2L, "url1", 5, Timestamp.valueOf(LocalDateTime.now()), 1L);
        var example = Example.of(image);
        var sort = Sort.unsorted();
        ArgumentCaptor<Example<Image>> exampleCaptor = ArgumentCaptor.captor();
        ArgumentCaptor<Sort> sortCaptor = ArgumentCaptor.captor();

        repositoryUtil.provideSearchByExampleAndSort(example, sort, imageRepository::findAll);
        Mockito.verify(imageRepository, Mockito.times(1)).findAll(exampleCaptor.capture(), sortCaptor.capture());
        Assertions.assertEquals(exampleCaptor.getValue().getProbe(), image);
        Assertions.assertEquals(sortCaptor.getValue(), sort);
    }
}
