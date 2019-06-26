package com.allever.security.photo.browser.util;


public class MediaTypeUtil {
	/**
	 * png jpg
	 */
	public static final int TYPE_OTHER_IMAGE = 0;

	/**
	 * GIF
	 */
	public static final int TYPE_GIF = 1;
	
	/**
	 * png
	 */
	public static final int TYPE_PNG = 2;
	
	/**
	 * jpg
	 */
	public static final int TYPE_JPG = 3;
	
	/**
	 * video
	 */
	public static final int TYPE_VIDEO = 4;

	/**
	 * text
	 */
	public static final int TYPE_TEXT = 5;

	public static boolean isGif(int type){
		return (type == TYPE_GIF);
	}
	
	public static boolean isPNG(int type){
		return (type == TYPE_PNG);
	}
	
	public static boolean isJPG(int type){
		return (type == TYPE_JPG);
	}
	
	public static boolean isJPGOrPNG(int type){
		return (type == TYPE_JPG || type == TYPE_PNG);
	}
	
	public static boolean isOtherImage(int type){
		return (type == TYPE_OTHER_IMAGE);
	}
	
	public static boolean isImage(int type){
		return (type == TYPE_OTHER_IMAGE || type == TYPE_GIF || type == TYPE_PNG || type == TYPE_JPG);
	}
	
	public static boolean isVideo(int type){
		return (type == TYPE_VIDEO);
	}

	public static boolean isText(int type) {
		return type == TYPE_TEXT;
	}
}
