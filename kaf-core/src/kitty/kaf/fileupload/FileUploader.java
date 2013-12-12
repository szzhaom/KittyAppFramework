package kitty.kaf.fileupload;

import java.io.InputStream;

public interface FileUploader {
	String getFieldName();

	String getName();

	String getOriginalName();

	String getContentType();

	void upload(InputStream stream) throws Throwable;
}
