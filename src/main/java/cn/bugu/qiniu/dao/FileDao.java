package cn.bugu.qiniu.dao;

import com.qiniu.storage.model.FileInfo;

public interface FileDao {

	int insert(FileInfo fileInfo);

	int delete(String key);

}
