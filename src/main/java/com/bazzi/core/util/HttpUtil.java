package com.bazzi.core.util;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;

/**
 * HTTP请求，支持GET、POST，以及把请求参数放在requestBody里
 *
 * @author PanJianzang
 *
 */
@Slf4j
public final class HttpUtil {
	public static final String KEY_TYPE = "PKCS12";
	public static final String PROTOCOL_TLSV1 = "TLSv1";

	/**
	 * 发送GET请求
	 *
	 * @param url
	 * @return
	 */
	public static String sendGet(String url) {
		return sendGet(url, null);
	}

	/**
	 * 会把请求参数paramMap拼接到url上，再发送GET请求
	 *
	 * @param url
	 * @param paramMap
	 *            请求参数
	 * @return
	 */
	public static String sendGet(String url, Map<String, String> paramMap) {
		return sendGet(url, paramMap, null);
	}

	/**
	 * 会把普通Bean对象转换成Map<String,String>，再拼接到url上，再发送GET请求
	 *
	 * @param url
	 * @param t
	 * @return
	 */
	public static <T> String sendGet(String url, T t) {
		return sendGet(url, convertToMap(t), null);
	}

	/**
	 * 会把普通Bean对象转换成Map<String,String>，再拼接到url上，设置header相关信息，再发送GET请求
	 *
	 * @param url
	 * @param t
	 *            普通Bean对象
	 * @param headerMap
	 *            请求头参数
	 * @return
	 */
	public static <T> String sendGet(String url, T t, Map<String, String> headerMap) {
		return sendGet(url, convertToMap(t), headerMap);
	}

	/**
	 * 会把请求参数paramMap拼接到url上，设置header相关信息，再发送GET请求
	 *
	 * @param url
	 * @param paramMap
	 *            请求参数
	 * @param headerMap
	 *            请求头参数
	 * @return
	 */
	public static String sendGet(String url, Map<String, String> paramMap, Map<String, String> headerMap) {
		if (url == null || "".equals(url))
			throw new IllegalArgumentException("Property 'url' is required");
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		try {
			URIBuilder builder = new URIBuilder(url);
			if (paramMap != null) {
				for (Map.Entry<String, String> entry : paramMap.entrySet()) {
					builder.addParameter(entry.getKey(), entry.getValue());
				}
			}
			URI uri = builder.build();
			HttpGet getReq = new HttpGet(uri);
			headerMap = headerMap == null ? new HashMap<>() : headerMap;
			for (Map.Entry<String, String> entry : headerMap.entrySet()) {
				getReq.addHeader(entry.getKey(), entry.getValue());
			}
			response = httpClient.execute(getReq);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				HttpEntity httpEntity = response.getEntity();
				return httpEntity == null ? null : EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
			} else {
				log.info("通讯异常，错误码：HTTP CODE(" + statusCode + ")");
				return null;
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		} finally {
			if (response != null)
				try {
					response.close();
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
			if (httpClient != null)
				try {
					httpClient.close();
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
		}
	}

	/**
	 * 发送GET请求，结果为byte数组
	 * 
	 * @param url
	 * @return
	 */
	public static byte[] sendGetByte(String url) {
		return sendGetByte(url, null);
	}

	/**
	 * 会把请求参数paramMap拼接到url上，再发送GET请求，结果为byte数组
	 * 
	 * @param url
	 * @param paramMap
	 *            请求参数
	 * @return
	 */
	public static byte[] sendGetByte(String url, Map<String, String> paramMap) {
		return sendGetByte(url, paramMap, null);
	}

	/**
	 * 会把普通Bean对象转换成Map<String,String>，再拼接到url上，再发送GET请求，结果为byte数组
	 * 
	 * @param url
	 * @param t
	 * @return
	 */
	public static <T> byte[] sendGetByte(String url, T t) {
		return sendGetByte(url, convertToMap(t), null);
	}

	/**
	 * 会把普通Bean对象转换成Map<String,String>，再拼接到url上，设置header相关信息，再发送GET请求，结果为byte数组
	 * 
	 * @param url
	 * @param t
	 *            普通Bean对象
	 * @param headerMap
	 *            请求头参数
	 * @return
	 */
	public static <T> byte[] sendGetByte(String url, T t, Map<String, String> headerMap) {
		return sendGetByte(url, convertToMap(t), headerMap);
	}

	/**
	 * 会把请求参数paramMap拼接到url上，设置header相关信息，再发送GET请求，结果为byte数组
	 * 
	 * @param url
	 * @param paramMap
	 *            请求参数
	 * @param headerMap
	 *            请求头参数
	 * @return
	 */
	public static byte[] sendGetByte(String url, Map<String, String> paramMap, Map<String, String> headerMap) {
		if (url == null || "".equals(url))
			throw new IllegalArgumentException("Property 'url' is required");
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		try {
			URIBuilder builder = new URIBuilder(url);
			if (paramMap != null) {
				for (Map.Entry<String, String> entry : paramMap.entrySet()) {
					builder.addParameter(entry.getKey(), entry.getValue());
				}
			}
			URI uri = builder.build();
			HttpGet getReq = new HttpGet(uri);
			headerMap = headerMap == null ? new HashMap<>() : headerMap;
			for (Map.Entry<String, String> entry : headerMap.entrySet()) {
				getReq.addHeader(entry.getKey(), entry.getValue());
			}
			response = httpClient.execute(getReq);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				HttpEntity httpEntity = response.getEntity();
				return httpEntity == null ? null : EntityUtils.toByteArray(httpEntity);
			} else {
				log.info("通讯异常，错误码：HTTP CODE(" + statusCode + ")");
				return null;
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		} finally {
			if (response != null)
				try {
					response.close();
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
			if (httpClient != null)
				try {
					httpClient.close();
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
		}
	}

	/**
	 * 发送POST请求
	 *
	 * @param url
	 * @param paramMap
	 *            请求参数
	 * @return
	 */
	public static String sendPost(String url, Map<String, String> paramMap) {
		return sendPost(url, JSON.toJSONString(paramMap), null);
	}

	/**
	 * 发送POST请求
	 *
	 * @param url
	 * @param paramMap
	 *            请求参数
	 * @param headerMap
	 *            请求头参数
	 * @return
	 */
	public static String sendPost(String url, Map<String, String> paramMap, Map<String, String> headerMap) {
		return sendPost(url, JSON.toJSONString(paramMap), headerMap);
	}

	/**
	 * 参数为普通Bean对象，发送POST请求
	 *
	 * @param url
	 * @param t
	 *            普通Bean对象
	 * @return
	 */
	public static <T> String sendBeanPost(String url, T t) {
		return sendJSONPost(url, JSON.toJSONString(t), null);
	}

	/**
	 * 参数为普通Bean对象，设置header参数，再发送POST请求
	 *
	 * @param url
	 * @param t
	 *            普通Bean对象
	 * @param headerMap
	 * @return
	 */
	public static <T> String sendBeanPost(String url, T t, Map<String, String> headerMap) {
		return sendPost(url, JSON.toJSONString(t), headerMap);
	}

	/**
	 * 参数格式为JSON，发送POST请求
	 *
	 * @param url
	 * @param json
	 *            JSON格式的请求参数
	 * @return
	 */
	public static String sendJSONPost(String url, String json) {
		return sendPost(url, json, null);
	}

	/**
	 * 参数格式为JSON，设置header参数，再发送POST请求
	 *
	 * @param url
	 * @param json
	 *            JSON格式的请求参数
	 * @param headerMap
	 *            请求头参数
	 * @return
	 */
	public static String sendJSONPost(String url, String json, Map<String, String> headerMap) {
		return sendPost(url, json, headerMap);
	}

	/**
	 * 参数格式为XML，发送POST请求
	 *
	 * @param url
	 * @param xml
	 *            XML格式的请求参数
	 * @return
	 */
	public static String sendXMLPost(String url, String xml) {
		return sendPost(url, xml, null);
	}

	/**
	 * 参数格式为XML，设置header参数，再发送POST请求
	 *
	 * @param url
	 * @param xml
	 *            XML格式的请求参数
	 * @param headerMap
	 *            请求头参数
	 * @return
	 */
	public static String sendXMLPost(String url, String xml, Map<String, String> headerMap) {
		return sendPost(url, xml, headerMap);
	}

	/**
	 * 发送POST请求
	 *
	 * @param url
	 * @param param
	 *            请求参数
	 * @param headerMap
	 *            请求头参数
	 * @return
	 */
	private static String sendPost(String url, String param, Map<String, String> headerMap) {
		if (url == null || "".equals(url) || param == null || "".equals(param))
			return null;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		try {
			HttpPost postReq = new HttpPost(url);
			headerMap = headerMap == null ? new HashMap<>() : headerMap;
			for (Map.Entry<String, String> entry : headerMap.entrySet()) {
				postReq.addHeader(entry.getKey(), entry.getValue());
			}
			StringEntity entity = new StringEntity(param, StandardCharsets.UTF_8);
			postReq.setEntity(entity);
			response = httpClient.execute(postReq);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				HttpEntity httpEntity = response.getEntity();
				return httpEntity == null ? null : EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
			} else {
				log.info("通讯异常，错误码：HTTP CODE(" + statusCode + ")");
				return null;
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		} finally {
			if (response != null)
				try {
					response.close();
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
			if (httpClient != null)
				try {
					httpClient.close();
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
		}
	}

	/**
	 * 将请求参数放在RequestBody中，发送POST请求
	 *
	 * @param url
	 * @param t
	 *            请求参数，会用FASTJSON转成JSON字符串
	 * @return
	 */
	public static <T> String sendPostInBody(String url, T t) {
		return sendPostInBody(url, JSON.toJSONString(t));
	}

	/**
	 * 将请求参数放在RequestBody中，发送POST请求
	 *
	 * @param url
	 * @param param
	 *            请求参数，支持JSON或者XML格式的字符串
	 * @return
	 */
	public static String sendPostInBody(String url, String param) {
		return sendPostInBody(url, param, null);
	}

	/**
	 * 将请求参数放在RequestBody中，发送POST请求
	 *
	 * @param url
	 * @param param
	 *            请求参数，支持JSON或者XML格式的字符串
	 * @param headerMap
	 *            请求头参数
	 * @return
	 */
	public static String sendPostInBody(String url, String param, Map<String, String> headerMap) {
		if (url == null || "".equals(url))
			throw new IllegalArgumentException("Property 'url' is required");
		if (param == null || "".equals(param))
			throw new IllegalArgumentException("Property 'param' is required");
		CloseableHttpClient httpClient = HttpClients.createDefault();
		return sendPostInBody(url, param, headerMap, httpClient);
	}

	/**
	 * 带证书的HTTPS请求，POST方式，请求参数放在请求body里
	 *
	 * @param url
	 * @param param
	 *            请求参数，支持JSON或者XML格式的字符串
	 * @param sslContext
	 *            SSL参数
	 * @return
	 */
	public static String sendPostSSL(String url, String param, SSLContext sslContext) {
		return sendPostSSL(url, param, sslContext, null);
	}

	/**
	 * 带证书的HTTPS请求，POST方式，请求参数放在请求body里
	 *
	 * @param url
	 * @param param
	 *            请求参数，支持JSON或者XML格式的字符串
	 * @param sslContext
	 *            SSL参数
	 * @param headerMap
	 * @return
	 */
	public static String sendPostSSL(String url, String param, SSLContext sslContext, Map<String, String> headerMap) {
		if (url == null || "".equals(url))
			throw new IllegalArgumentException("Property 'url' is required");
		if (param == null || "".equals(param))
			throw new IllegalArgumentException("Property 'param' is required");
		if (sslContext == null)
			throw new IllegalArgumentException("Property 'sslcontext' is required");
		CloseableHttpClient httpClient = HttpClientBuilder.create().setSSLContext(sslContext).build();
		return sendPostInBody(url, param, headerMap, httpClient);
	}

	/**
	 * 根据证书创建对应的SSLContext，采用PKCS12类型，TLSv1协议
	 *
	 * @param fileName
	 *            证书文件所在的全路径
	 * @param password
	 *            密匙
	 * @return
	 */
	public static SSLContext buildSSLContext(String fileName, String password) {
		return buildSSLContext(fileName, password, KEY_TYPE, PROTOCOL_TLSV1);
	}

	/**
	 * 根据证书创建对应的SSLContext
	 *
	 * @param fileName
	 *            证书文件所在的全路径
	 * @param password
	 *            密匙
	 * @param keyType
	 *            类型，如jks、PKCS12
	 * @param protocol
	 *            协议，如TLS、TLSv1、TLSv2
	 * @return
	 */
	public static SSLContext buildSSLContext(String fileName, String password, String keyType, String protocol) {
		if (fileName == null || "".equals(fileName))
			throw new IllegalArgumentException("Property 'fileName' is required");
		if (password == null || "".equals(password))
			throw new IllegalArgumentException("Property 'password' is required");
		if (keyType == null || "".equals(keyType))
			throw new IllegalArgumentException("Property 'keyType' is required");
		if (protocol == null || "".equals(protocol))
			throw new IllegalArgumentException("Property 'protocol' is required");
		FileInputStream fis = null;
		try {
			KeyStore keyStore = KeyStore.getInstance(keyType);
			fis = new FileInputStream(new File(fileName));

			keyStore.load(fis, password.toCharArray());

			// Trust own CA and all self-signed certs，Allow TLSv1 protocol only
			return SSLContextBuilder.create().setProtocol(protocol).loadKeyMaterial(keyStore, password.toCharArray())
					.build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		} finally {
			if (fis != null)
				try {
					fis.close();
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
		}
	}

	/**
	 * 发送POST请求，并且请求参数放在请求body里
	 *
	 * @param url
	 * @param param
	 * @param headerMap
	 * @param httpClient
	 * @return
	 */
	private static String sendPostInBody(String url, String param, Map<String, String> headerMap,
			CloseableHttpClient httpClient) {
		if (httpClient == null)
			throw new IllegalArgumentException("Property 'httpClient' is required");
		CloseableHttpResponse response = null;
		try {
			HttpPost postReq = new HttpPost(url);
			headerMap = headerMap == null ? new HashMap<>() : headerMap;
			for (Map.Entry<String, String> entry : headerMap.entrySet()) {
				postReq.addHeader(entry.getKey(), entry.getValue());
			}
			postReq.setEntity(new ByteArrayEntity(param.getBytes(StandardCharsets.UTF_8)));
			response = httpClient.execute(postReq);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				HttpEntity httpEntity = response.getEntity();
				return httpEntity == null ? null : EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
			} else {
				log.info("通讯异常，错误码：HTTP CODE(" + statusCode + ")");
				return null;
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		} finally {
			if (response != null)
				try {
					response.close();
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
			try {
				httpClient.close();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * 把普通Bean对象转换成Map<String, String>
	 *
	 * @param t
	 * @return
	 */
	private static <T> Map<String, String> convertToMap(T t) {
		Map<String, String> map = new HashMap<>();
		if (t == null)
			return map;
		Field[] fields = t.getClass().getDeclaredFields();
		for (Field field : fields) {
			String fieldName = field.getName();
			// 排除serialVersionUID
			if (!"serialVersionUID".equals(fieldName)) {
				String value = "";
				try {
					value = BeanUtils.getSimpleProperty(t, fieldName);
				} catch (Exception e) {
					log.error(e.getMessage());
				}
				map.put(fieldName, value);
			}
		}
		return map;
	}

}
