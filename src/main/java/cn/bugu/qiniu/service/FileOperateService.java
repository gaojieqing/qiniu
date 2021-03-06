package cn.bugu.qiniu.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.qiniu.util.UrlSafeBase64;

import cn.bugu.qiniu.dao.FileDao;
import cn.bugu.qiniu.service.impl.IFileOperateService;
import cn.bugu.qiniu.util.ConfigConstants;

@Service("fileOperateService")
public class FileOperateService implements IFileOperateService {

	private static Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

	@Autowired
	ConfigConstants constants;

	@Resource
	private FileDao fileDao;

	@Override
	// 简单上传，使用默认策略，只需要设置上传的空间名就可以了
	public String getSimpleUpToken() {
		// 密钥配置
		Auth auth = Auth.create(constants.ACCESS_KEY, constants.SECRET_KEY);
		return auth.uploadToken(constants.BUCKET_NAME);
	}

	@Override
	// 覆写上传，使用默认策略，需要设置上传的空间名和文件名
	public String getOverrideUpToken(String key) {
		// 密钥配置
		Auth auth = Auth.create(constants.ACCESS_KEY, constants.SECRET_KEY);
		return auth.uploadToken(constants.BUCKET_NAME, key);
	}

	@Override
	public List<FileInfo> getFileInfoList() {
		// 构造一个带指定Zone对象的配置类
		Configuration cfg = new Configuration(Zone.zone0());

		// 密钥配置
		Auth auth = Auth.create(constants.ACCESS_KEY, constants.SECRET_KEY);
		BucketManager bucketManager = new BucketManager(auth, cfg);

		// 文件名前缀
		String prefix = "";
		// 每次迭代的长度限制，最大1000，推荐值 1000
		int limit = 1000;
		// 指定目录分隔符，列出所有公共前缀（模拟列出目录效果）。缺省值为空字符串
		String delimiter = "";

		// 列举空间文件列表
		BucketManager.FileListIterator fileListIterator = bucketManager.createFileListIterator(constants.BUCKET_NAME,
				prefix, limit, delimiter);

		List<FileInfo> files = new LinkedList<FileInfo>();
		while (fileListIterator.hasNext()) {
			for (FileInfo item : fileListIterator.next()) {
				files.add(item);
			}
		}

		return files;
	}

	@Override
	public FileInfo getFileInfo(String key) {
		// 构造一个带指定Zone对象的配置类
		Configuration cfg = new Configuration(Zone.zone0());

		// 密钥配置
		Auth auth = Auth.create(constants.ACCESS_KEY, constants.SECRET_KEY);
		BucketManager bucketManager = new BucketManager(auth, cfg);

		FileInfo fileInfo = null;
		try {
			fileInfo = bucketManager.stat(constants.BUCKET_NAME, key);
		} catch (QiniuException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("get file info.");
		logger.info("fileInfo.key: " + fileInfo.key);
		logger.info("fileInfo.hash: " + fileInfo.hash);
		logger.info("fileInfo.fsize: " + fileInfo.fsize);
		logger.info("fileInfo.mimeType: " + fileInfo.mimeType);
		logger.info("fileInfo.putTime: " + fileInfo.putTime);

		return fileInfo;
	}

	@Override
	public String getFileUrl(String key) {
		String encodedKey = null;
		try {
			encodedKey = URLEncoder.encode(key, "utf-8");

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String publicUrl = String.format("%s/%s", constants.BUCKET_DOMAIN, encodedKey);

		if ("public".equals(constants.BUCKET_CONTROL)) {	
			String fops = "imageView2/0/q/75|watermark/2/text/5q2l5Y-k/font/5a6L5L2T/fontsize/240/fill/IzAwMDAwMA==/dissolve/100/gravity/SouthEast/dx/10/dy/10|imageslim";
			return String.format("%s?%s", publicUrl, fops);
		}

		// 密钥配置
		Auth auth = Auth.create(constants.ACCESS_KEY, constants.SECRET_KEY);
		return auth.privateDownloadUrl(publicUrl, constants.EXPIRE_SECONDS);
	}

	@Override
	public List<DefaultPutRet> postFile(MultipartFile[] images) {
		List<DefaultPutRet> rets = new LinkedList<DefaultPutRet>();

		try {
			for (MultipartFile image : images) {
				if (!image.isEmpty()) {
					rets.add(putFile(image.getBytes(), null));
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return rets;
	};

	@Override
	public DefaultPutRet putFile(byte[] bytes, String key) {
		// 构造一个带指定Zone对象的配置类
		Configuration cfg = new Configuration(Zone.zone0());

		UploadManager uploadManager = new UploadManager(cfg);

		// 水印样式
		String watermarkFileName = key == null || "".equals("key") ? "watermark" : key;
		String fops = "imageView2/0/q/75|watermark/2/text/5q2l5Y-k/font/5a6L5L2T/fontsize/240/fill/IzAwMDAwMA==/dissolve/100/gravity/SouthEast/dx/10/dy/10|imageslim";
		String urlbase64 = UrlSafeBase64.encodeToString(constants.BUCKET_NAME + ":" + watermarkFileName);
		String pfops = fops + "|saveas/" + urlbase64;

		// 密钥配置
		Auth auth = Auth.create(constants.ACCESS_KEY, constants.SECRET_KEY);
		StringMap putPolicy = new StringMap().put("returnBody", "{\"key\":\"$(key)\",\"hash\":\"$(etag)\"}")
				.put("persistentOps", pfops);
		String upToken = auth.uploadToken(constants.BUCKET_NAME, key, constants.EXPIRE_SECONDS, putPolicy);

		ByteArrayInputStream bytesInputStream = new ByteArrayInputStream(bytes);

		Response response;
		DefaultPutRet putRet = null;
		try {
			response = uploadManager.put(bytesInputStream, key, upToken, null, null);
			// 解析上传成功的结果
			putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);

			cn.bugu.qiniu.model.FileInfo info = new cn.bugu.qiniu.model.FileInfo();
			info.setKey(putRet.key);

			fileDao.insert(info);

			logger.info("upload file.");
			logger.info("putRet.key: " + putRet.key);
			logger.info("putRet.hash: " + putRet.hash);

		} catch (QiniuException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return putRet;
	};

	@Override
	public void deleteFile(String key) {
		// 构造一个带指定Zone对象的配置类
		Configuration cfg = new Configuration(Zone.zone0());

		// 密钥配置
		Auth auth = Auth.create(constants.ACCESS_KEY, constants.SECRET_KEY);
		BucketManager bucketManager = new BucketManager(auth, cfg);
		try {
			bucketManager.delete(constants.BUCKET_NAME, key);
			fileDao.delete(key);

		} catch (QiniuException ex) {
			// 如果遇到异常，说明删除失败
			logger.error("code: " + ex.code());
			logger.error("response: " + ex.response.toString());
		}
	}

	@Override
	public void copyFile(String fromKey, String toKey) {
		// 构造一个带指定Zone对象的配置类
		Configuration cfg = new Configuration(Zone.zone0());

		// 密钥配置
		Auth auth = Auth.create(constants.ACCESS_KEY, constants.SECRET_KEY);
		BucketManager bucketManager = new BucketManager(auth, cfg);
		try {
			bucketManager.copy(constants.BUCKET_NAME, fromKey, constants.BUCKET_NAME, toKey);
		} catch (QiniuException ex) {
			// 如果遇到异常，说明复制失败
			logger.error("code: " + ex.code());
			logger.error("response: " + ex.response.toString());
		}
	}

}
