package cn.bugu.qiniu.model;

import java.io.Serializable;

public class FileInfo implements Serializable{

	private Integer id;
	
	private String key;
	
	private static final long serialVersionUID = 1L;
    
	public Integer getId() {
    	return id;
    }
    
    public void setId(Integer id){
    	this.id = id;
    }
	
    public String getKey() {
    	return key;
    }
    
    public void setKey(String key){
    	this.key = key  == null ? null : key.trim();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", key=").append(key);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
