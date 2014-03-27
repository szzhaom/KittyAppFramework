package kitty.kaf.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import com.aliyun.openservices.ClientException;
import com.aliyun.openservices.ServiceException;
import com.aliyun.openservices.oss.OSSClient;
import com.aliyun.openservices.oss.OSSErrorCode;
import com.aliyun.openservices.oss.OSSException;
import com.aliyun.openservices.oss.model.CannedAccessControlList;
import com.aliyun.openservices.oss.model.GetObjectRequest;
import com.aliyun.openservices.oss.model.OSSObjectSummary;
import com.aliyun.openservices.oss.model.ObjectListing;
import com.aliyun.openservices.oss.model.ObjectMetadata;

/**
 * 阿里云的操作
 * 
 * @author 赵明
 * 
 */
public class AliyunHelper {

	private static final String ACCESS_ID = "rIhCXGYwpMAgHq1z";
	private static final String ACCESS_KEY = "SGezWr5DI6gclurST0LrpPr4Ly8R06";

	public static void main(String[] args) throws Exception {
		String bucketName = "zhaom-share";// ACCESS_ID + "-object-test";
		String key = "4704cc84-400b-4bca-a8d9-0d60d922d41c.png";

		// String uploadFilePath = "d:/temp/photo.jpg";
		String downloadFilePath = "/tmp/1.png";

		// 使用默认的OSS服务器地址创建OSSClient对象。
		OSSClient client = new OSSClient(ACCESS_ID, ACCESS_KEY);

		ensureBucket(client, bucketName);

		try {
			setBucketPublicReadable(client, bucketName);

			System.out.println("正在上传...");
			uploadFile(client, bucketName, "abc/spin_back.png", "/Users/zhaoming/Downloads/spin_back.png");

			System.out.println("正在下载...");
			downloadFile(client, bucketName, key, downloadFilePath);
		} finally {
			// deleteBucket(client, bucketName);
		}
	}

	// 创建Bucket.
	private static void ensureBucket(OSSClient client, String bucketName) throws OSSException, ClientException {

		try {
			// 创建bucket
			client.createBucket(bucketName);
		} catch (ServiceException e) {
			if (!OSSErrorCode.BUCKES_ALREADY_EXISTS.equals(e.getErrorCode())) {
				// 如果Bucket已经存在，则忽略
				throw e;
			}
		}
	}

	// 删除一个Bucket和其中的Objects
	static void deleteBucket(OSSClient client, String bucketName) throws OSSException, ClientException {

		ObjectListing ObjectListing = client.listObjects(bucketName);
		List<OSSObjectSummary> listDeletes = ObjectListing.getObjectSummaries();
		for (int i = 0; i < listDeletes.size(); i++) {
			String objectName = listDeletes.get(i).getKey();
			// 如果不为空，先删除bucket下的文件
			client.deleteObject(bucketName, objectName);
		}
		client.deleteBucket(bucketName);
	}

	// 把Bucket设置为所有人可读
	private static void setBucketPublicReadable(OSSClient client, String bucketName) throws OSSException,
			ClientException {
		// 创建bucket
		client.createBucket(bucketName);

		// 设置bucket的访问权限，public-read-write权限
		client.setBucketAcl(bucketName, CannedAccessControlList.PublicRead);
	}

	// 上传文件
	private static void uploadFile(OSSClient client, String bucketName, String key, String filename)
			throws OSSException, ClientException, FileNotFoundException {
		File file = new File(filename);

		ObjectMetadata objectMeta = new ObjectMetadata();
		objectMeta.setContentLength(file.length());
		// 可以在metadata中标记文件类型
		objectMeta.setContentType("image/png");

		InputStream input = new FileInputStream(file);
		client.putObject(bucketName, key, input, objectMeta);
	}

	// 下载文件
	private static void downloadFile(OSSClient client, String bucketName, String key, String filename)
			throws OSSException, ClientException {
		client.getObject(new GetObjectRequest(bucketName, key), new File(filename));
	}
}
