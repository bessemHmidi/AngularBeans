/**
 * Copyright (C) 2014 Red Hat, Inc, and individual contributors.
 * Copyright (C) 2011-2012 VMware, Inc.
 */

package org.projectodd.sockjs;

//import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.boon.json.JsonFactory;
import org.boon.json.ObjectMapper;

public class Utils {

    public static String md5Hex(String content) throws SockJsException {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(content.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new SockJsException("Error generating MD5 hex string", e);
        }
    }

    public static String generateExpires(Date date) {
        SimpleDateFormat df =  new SimpleDateFormat(RFC1123_PATTERN, LOCALE_US);
        df.setTimeZone(GMT_ZONE);
        return df.format(date);
    }

    public static String join(List<String> strings, String separator) {
        StringBuilder sb = new StringBuilder();
        for (Iterator<String> i = strings.iterator(); i.hasNext();) {
            sb.append(i.next()).append(i.hasNext() ? separator : "");
        }
        return sb.toString();
    }

    public static String uuid() {
        return UUID.randomUUID().toString();
    }

    public static String escapeSelected(String str, String chars) {
        chars = "%" + chars;
        for (int i = 0; i < chars.length(); i++) {
            String charAsStr = chars.substring(i, i + 1);
            try {
                str = str.replace(charAsStr, URLEncoder.encode(charAsStr, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("UTF-8 encoding not found:", e);
            }
        }
        return str;
    }

    public static String quote(String string) {
        String quoted = jsonStringify(string);

        Matcher matcher = ESCAPABLE.matcher(quoted);
        StringBuffer escapedQuoted = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(escapedQuoted, escapableLookup.get(matcher.group()));
        }
        matcher.appendTail(escapedQuoted);
        if (escapedQuoted.length() == 0) {
            return quoted;
        }
        return escapedQuoted.toString();
    }

    public static String jsonStringify(String string) {
    	
    	
        return mapper.toJson(string);
    }

    public static <T> T parseJson(String json, Class<T> clazz) {
        return mapper.fromJson(json, clazz);
    }

    private static final String RFC1123_PATTERN = "EEE, dd MMM yyyy HH:mm:ss z";
    private static final Locale LOCALE_US = Locale.US;
    private static final TimeZone GMT_ZONE = TimeZone.getTimeZone("GMT");

    private static final Pattern ESCAPABLE = Pattern.compile("[\\x00-\\x1f\\ud800-\\udfff\\u200c-\\u200f\\u2028-\\u202f\\u2060-\\u206f\\ufff0-\\uffff]");
    private static final Map<String, String> escapableLookup = new HashMap<>();

    private static final ObjectMapper mapper = JsonFactory.create();

    static {
        StringBuilder chars = new StringBuilder();
        for (int i = 0; i < 65536; i++) {
            chars.append((char) i);
        }
        Matcher matcher = ESCAPABLE.matcher(chars);
        while (matcher.find()) {
            String a = matcher.group();
            String escaped = "0000" + Integer.toHexString(a.charAt(0));
            escaped = "\\\\u" + escaped.substring(escaped.length() - 4);
            escapableLookup.put(a, escaped);
        }
    }
}
