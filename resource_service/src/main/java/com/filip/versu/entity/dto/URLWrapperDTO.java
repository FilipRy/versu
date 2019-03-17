package com.filip.versu.entity.dto;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class URLWrapperDTO implements Serializable {

    public List<String> urls = new ArrayList<>();

    public List<PhotoObject> objectKeys = new ArrayList<>();

    public static class PhotoObject {

        public String key;
        public String contentType;

    }

}
