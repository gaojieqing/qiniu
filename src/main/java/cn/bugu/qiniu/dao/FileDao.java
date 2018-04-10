package cn.bugu.qiniu.dao;

import cn.bugu.qiniu.model.FileInfo;

public interface FileDao {

	int insert(FileInfo fileInfo);

	int delete(String key);

}
