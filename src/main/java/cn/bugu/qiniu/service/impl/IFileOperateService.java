package cn.bugu.qiniu.service.impl;

import java.util.List;

import com.qiniu.storage.model.FileInfo;

public interface IFileOperateService {

	public String getSimpleUpToken();

	public String getOverrideUpToken(String key);

	public List<FileInfo> getFileInfoList();
	
	public FileInfo getFileInfo(String key);

	public String getFileUrl(String key);
	
	public FileInfo postFile(byte[] bytes);
	
	public FileInfo putFile(byte[] bytes,String key);

	public void deleteFile(String key);
	
	public void copyFile(String fromKey, String toKey);
}
