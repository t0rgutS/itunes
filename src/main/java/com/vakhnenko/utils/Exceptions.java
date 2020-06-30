package com.vakhnenko.utils;

import com.vakhnenko.exception.NotFoundException;
import lombok.experimental.UtilityClass;

import java.util.function.Supplier;

@UtilityClass
public class Exceptions {
    public static final Supplier<NotFoundException> ALBUM_NOT_FOUND = () -> new NotFoundException("Указанный альбом не найден!");
    public static final Supplier<NotFoundException> PLAYLIST_NOT_FOUND = () -> new NotFoundException("Указанный плейлист не найден!");
    public static final Supplier<NotFoundException> SONG_NOT_FOUND = () -> new NotFoundException("Указанная песня не найдена!");
    public static final Supplier<NotFoundException> GENRE_NOT_FOUND = () -> new NotFoundException("Указанный жанр не найден!");
    public static final Supplier<NotFoundException> PERFORMER_NOT_FOUND = () -> new NotFoundException("Указанный исполнитель не найден!");
    public static final Supplier<NotFoundException> USER_NOT_FOUND = () -> new NotFoundException("Указанный пользователь не найден!");
    public static final Supplier<NotFoundException> ROLE_NOT_FOUND = () -> new NotFoundException("Ошибка назначения роли!");
    /*public static final Supplier<Exception> PERFORMER_ALREADY_EXISTS = () -> new Exception("Указанный исполнитель уже добавлен!");
    public static final Supplier<Exception> ALBUM_ALREADY_EXISTS = () -> new Exception("Указанный альбом уже добавлен!");
    public static final Supplier<Exception> SONG_ALREADY_EXISTS = () -> new Exception("Указанная песня уже добавлена!");
    public static final Supplier<Exception> GENRE_ALREADY_EXISTS = () -> new Exception("Указанный жанр уже добавлен!");
    public static final Supplier<Exception> USER_ALREADY_EXISTS = () -> new Exception("Указанный пользователь уже зарегистрирован!");
    public static final Supplier<Exception> PLAYLIST_ALREADY_EXISTS = () -> new Exception("Указанный плейлист уже существует!");*/


}
