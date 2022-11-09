package com.learn.pojo;

import java.io.Serializable;

/**
 * 作业:基于RedisTemplate将Blog类型的对象存储到redis
 */
public class Blog implements Serializable {
    private static final long serialVersionUID = -8140392948116872035L;
    private Integer id;
    private String title;
    private Boolean delete=false;

    public Boolean isDelete() {
        return delete;
    }

    public void setDelete(Boolean delete) {
        this.delete = delete;
    }

    public Integer getId() {//jack
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "Blog{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", delete=" + delete +
                '}';
    }
}
