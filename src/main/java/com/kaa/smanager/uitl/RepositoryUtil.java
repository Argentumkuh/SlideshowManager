package com.kaa.smanager.uitl;

import org.springframework.stereotype.Component;

import java.util.function.BiFunction;
import java.util.function.Function;

@Component
public class RepositoryUtil {
    public <T, U> U provideBatchRepositoryOperation(T collection, Function<T, U> repositoryOperation) {
        return repositoryOperation.apply(collection);
    }

    public <T, U, V> V provideSearchByExampleAndSort(T example, U sort, BiFunction<T, U, V> findOperation) {
        return findOperation.apply(example, sort);
    }
}
