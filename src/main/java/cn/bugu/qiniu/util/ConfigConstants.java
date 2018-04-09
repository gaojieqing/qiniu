package cn.bugu.qiniu.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = { "classpath:config-constants.properties" }, encoding = "UTF-8")
public class ConfigConstants {

	@Value("${qiniu.access_key}")
	public String ACCESS_KEY;
	@Value("${qiniu.secret_key}")
	public String SECRET_KEY;
	@Value("${qiniu.bucket_name}")
	public String BUCKET_NAME;
	@Value("${qiniu.bucket_domain}")
	public String BUCKET_DOMAIN;
	@Value("${qiniu.bucket_control}")
	public String BUCKET_CONTROL;
	@Value("${qiniu.expire_seconds}")
	public long EXPIRE_SECONDS;

}
