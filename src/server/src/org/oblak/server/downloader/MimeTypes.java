package org.oblak.server.downloader;

/**
 * Stores information about known mime types. 
 */
public class MimeTypes {
	
	protected static final String MIME_PDF = ".*/pdf";
	protected static final String MIME_AUDIO = "audio/.*";
	protected static final String MIME_ZIP = ".*zip";	
	protected static final String MIME_VIDEO = "video/.*";
	protected static final String MIME_IMAGE = "image/.*";
	
	protected static final String DIRECTORY_PDF = "pdfs";
	protected static final String DIRECTORY_AUDIO = "audios";
	protected static final String DIRECTORY_ZIP = "zips";	
	protected static final String DIRECTORY_VIDEO = "videos";
	protected static final String DIRECTORY_IMAGE = "images";	
}
