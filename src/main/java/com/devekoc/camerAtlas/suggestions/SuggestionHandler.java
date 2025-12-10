package com.devekoc.camerAtlas.suggestions;

import com.devekoc.camerAtlas.enumerations.TargetType;

public interface SuggestionHandler<T> {
    void create(T dto);
    void update(int id, T dto);
    void delete(int id);
    Class<T> dtoType();
    TargetType handledType();
}


