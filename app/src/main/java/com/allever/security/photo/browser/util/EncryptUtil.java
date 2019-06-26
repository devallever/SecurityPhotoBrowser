package com.allever.security.photo.browser.util;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * 用于加解密图片并保存
 */
public class EncryptUtil {
	public static final int SIZE = 3;

	public static final String BACKUP_FILE_TYPE = ".pebackup";
	public static final String DYNAMIC_START = "VIDEO_";


	/**
	 * 解密出图片根据加密后文件的路径获取它的EntryptFileInputStream
	 * @param path
	 * @return
	 * @throws IOException 
	 */
	public static EntryptFileInputStream decrypt(String path) throws IOException{
		EntryptFileInputStream efis = null;
		File f = new File(path);
		if(f.exists()){
			efis = new EntryptFileInputStream(f);
			return efis;
		}
		throw new FileNotFoundException("not found path:" + path);
	}
	
	/**
	 * 解密出图片根据加密后文件的路径获取它的EntryptFileInputStream
	 * @param f
	 * @return
	 * @throws IOException 
	 */
	public static EntryptFileInputStream decrypt(File f) throws IOException{
		EntryptFileInputStream efis = null;
		if(f.exists()){
			efis = new EntryptFileInputStream(f);
			return efis;
		}
		throw new FileNotFoundException("not found file:" + f.getPath());
	}
	
	/**
	 * 解密出图片根据加密后文件的路径获取它的EntryptFileInputStream
	 * @param fd
	 * @return
	 * @throws IOException 
	 */
	public static EntryptFileInputStream decrypt(FileDescriptor fd) throws IOException{
		EntryptFileInputStream efis = null;
		if(fd != null){
			efis = new EntryptFileInputStream(fd);
			return efis;
		}
		throw new FileNotFoundException("error FileDescriptor");
	}
}
