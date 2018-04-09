package cn.bugu.qiniu.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.qiniu.storage.model.FileInfo;

import cn.bugu.qiniu.service.FileOperateService;

@Controller
public class ImageOperateController {

	@Autowired
	FileOperateService fileOperateService;

	@ResponseBody
	@RequestMapping(value = "/upToken", method = RequestMethod.GET)
	public String getUpToken() {
		return fileOperateService.getSimpleUpToken();
	}
	
	@ResponseBody
	@RequestMapping(value = "/upToken/{key}", method = RequestMethod.GET)
	public String getUpToken(@PathVariable String key) {
		if (key == null || "".equals(key)) {
			return fileOperateService.getSimpleUpToken();
		}
		return fileOperateService.getOverrideUpToken(key);
	}

	@ResponseBody
	@RequestMapping(value = "/images", method = RequestMethod.GET)
	public List<FileInfo> getImageList() {
		return fileOperateService.getFileInfoList();
	}

	@ResponseBody
	@RequestMapping(value = "/images/{key}", method = RequestMethod.GET)
	public String getImage(@PathVariable String key) {
		return fileOperateService.getFileUrl(key);
	}
	
	public class Pojo {
		private List<String> file;
	}

	@ResponseBody
	@RequestMapping(value = "/images", method = RequestMethod.POST)
	public FileInfo postImage(Pojo pojo) throws IOException {
		return null;
		//System.out.println(image[0]);
		//return fileOperateService.postFile(image[0].getBytes());
	}

	@ResponseBody
	@RequestMapping(value = "/images/{key}", method = RequestMethod.POST)
	public FileInfo postImage(@RequestParam(value = "file") MultipartFile image, @PathVariable String key)
			throws IOException {
		if (key == null || "".equals(key)) {
			return fileOperateService.postFile(image.getBytes());
		}
		return fileOperateService.putFile(image.getBytes(), key);
	}

	@ResponseBody
	@RequestMapping(value = "/images/{key}", method = RequestMethod.DELETE)
	public void deleteImage(@PathVariable(required = true) String key) {
		fileOperateService.deleteFile(key);
	}

	@RequestMapping(value = "/upload", method = RequestMethod.GET)
	public String upload() {
		return "upload";
	}

	@RequestMapping(value = "/manage", method = RequestMethod.GET)
	public String manage() {
		return "manage";
	}

}
