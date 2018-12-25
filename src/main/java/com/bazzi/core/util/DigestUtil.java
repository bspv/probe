package com.bazzi.core.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

@Slf4j
public final class DigestUtil {

	/**
	 * 将data转为对应的SHA256值，小写
	 * 
	 * @param data
	 * @return
	 */
	public static String toSha256(String data) {
		if (data == null)
			return null;
		return Hex.encodeHexString(DigestUtils.getSha256Digest().digest(StringUtils.getBytesUtf8(data)));
	}

	/**
	 * 将data转为对应的SHA256值，大写
	 * 
	 * @param data
	 * @return
	 */
	public static String toSha256Upper(String data) {
		if (data == null)
			return null;
		return Hex.encodeHexString(DigestUtils.getSha256Digest().digest(StringUtils.getBytesUtf8(data))).toUpperCase();
	}

	/**
	 * 计算文件的md5值
	 * 
	 * @param file
	 * @return
	 */
	public static String fileToMd5(File file) {
		if (file == null)
			return null;
		try {
			return DigestUtils.md5Hex(new FileInputStream(file));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 计算文件的md5值
	 * 
	 * @param in
	 * @return
	 */
	public static String fileToMd5(InputStream in) {
		if (in == null)
			return null;
		try {
			return DigestUtils.md5Hex(in);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 将data转为对应的MD5值，小写
	 * 
	 * @param data
	 * @return
	 */
	public static String toMd5(String data) {
		if (data == null)
			return null;
		return DigestUtils.md5Hex(data);
	}

	/**
	 * 将data转为对应的MD5值，大写
	 * 
	 * @param data
	 * @return
	 */
	public static String toMd5Upper(String data) {
		if (data == null)
			return null;
		return DigestUtils.md5Hex(data).toUpperCase();
	}

	/**
	 * Base64加密，字符串转为byte[]
	 * 
	 * @param base64String
	 * @return
	 */
	public static byte[] encodeBase64(String base64String) {
		if (base64String == null)
			return null;
		return Base64.encodeBase64(StringUtils.getBytesUtf8(base64String));
	}

	/**
	 * Base64加密，字符串转为字符串
	 * 
	 * @param base64String
	 * @return
	 */
	public static String encodeBase64String(String base64String) {
		if (base64String == null)
			return null;
		return Base64.encodeBase64String(StringUtils.getBytesUtf8(base64String));
	}

	/**
	 * Base64加密，byte[]转为byte[]
	 * 
	 * @param base64
	 * @return
	 */
	public static byte[] encodeBase64(byte[] base64) {
		if (base64 == null)
			return null;
		return Base64.encodeBase64(base64);
	}

	/**
	 * Base64加密，byte[]转为字符串
	 * 
	 * @param base64
	 * @return
	 */
	public static String encodeBase64String(byte[] base64) {
		if (base64 == null)
			return null;
		return Base64.encodeBase64String(base64);
	}

	/**
	 * Base64解密，字符串转为byte[]
	 * 
	 * @param base64String
	 * @return
	 */
	public static byte[] decodeBase64(String base64String) {
		if (base64String == null)
			return null;
		return Base64.decodeBase64(base64String);
	}

	/**
	 * Base64解密，字符串转为字符串
	 * 
	 * @param base64String
	 * @return
	 */
	public static String decodeBase64String(String base64String) {
		if (base64String == null)
			return null;
		return StringUtils.newStringUtf8(Base64.decodeBase64(base64String));
	}

	/**
	 * Base64解密，byte[]转为byte[]
	 * 
	 * @param base64
	 * @return
	 */
	public static byte[] decodeBase64(byte[] base64) {
		if (base64 == null)
			return null;
		return Base64.decodeBase64(base64);
	}

	/**
	 * Base64解密，byte[]转为字符串
	 * 
	 * @param base64
	 * @return
	 */
	public static String decodeBase64String(byte[] base64) {
		if (base64 == null)
			return null;
		return StringUtils.newStringUtf8(Base64.decodeBase64(base64));
	}

}
