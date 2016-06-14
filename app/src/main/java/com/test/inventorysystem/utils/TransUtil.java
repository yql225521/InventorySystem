package com.test.inventorysystem.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by youmengli on 6/3/16.
 */

public class TransUtil {

    /**
     * 编码
     * @parambstr
     * @return String
     */
    public static String encode(String str){
        try {
            return URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return str;
        }

    }

    /**
     * 解码
     * @paramstr
     * @return string
     */
    public static String decode(String str){
        String decode_str="";
        try {
            decode_str= URLDecoder.decode(str, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();

        }
        return decode_str;
    }
}
