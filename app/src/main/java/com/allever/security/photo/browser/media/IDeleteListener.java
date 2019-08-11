package com.allever.security.photo.browser.media;

/**
 * @author allever
 */
public interface IDeleteListener<T> {
    //一个个文件删除调用这个
    void onDeleteFile(T object, boolean success);

    //整个目录删除
    void onDeleteFiles(boolean success);
}
