package com.bazzi.core.redis;

import redis.clients.jedis.Pipeline;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ProbeRedis {
	// key

	/**
	 * 以秒为单位，返回指定key的剩余生存时间(TTL, time to live)
	 *
	 * @param key
	 * @return
	 */
	long ttl(String key);

	/**
	 * 移除key的过期时间，key将持久保持
	 *
	 * @param key
	 * @return
	 */
	long persist(String key);

	/**
	 * 设置key的过期时间，以秒为单位
	 *
	 * @param key
	 * @param seconds 以秒为单位
	 * @return
	 */
	long expire(String key, int seconds);

	/**
	 * 设置key的过期时间,它是距历元（即格林威治标准时间 1970 年 1 月 1 日的 00:00:00，格里高利历）的偏移量
	 *
	 * @param key
	 * @param timestamp 以毫秒为单位
	 * @return
	 */
	long expireAt(String key, long timestamp);

	/**
	 * 检查指定key是否存在
	 *
	 * @param key
	 * @return
	 */
	boolean exists(String key);

	/**
	 * 删除指定key
	 *
	 * @param key
	 */
	void del(String key);

	/**
	 * 返回key所储存的值的类型
	 *
	 * @param key
	 * @return
	 */
	String type(String key);

	// String

	/**
	 * 设置指定key的值
	 *
	 * @param key
	 * @param value
	 * @return
	 */
	String set(String key, String value);

	/**
	 * 设置指定key的值
	 *
	 * @param key
	 * @param value
	 * @return
	 */
	String set(String key, byte[] value);

	/**
	 * 获取指定key的值
	 *
	 * @param key
	 * @return
	 */
	String get(String key);

	/**
	 * 以byte[]形式获取指定key的值
	 *
	 * @param key
	 * @return
	 */
	byte[] getByte(String key);

	/**
	 * 将值value关联到 key ，并将key的过期时间设为 seconds (以秒为单位)。
	 *
	 * @param key
	 * @param seconds
	 * @param value
	 * @return
	 */
	String setEx(String key, int seconds, String value);

	/**
	 * 将值value关联到 key ，并将key的过期时间设为 seconds (以秒为单位)。
	 *
	 * @param key
	 * @param seconds
	 * @param value
	 * @return
	 */
	String setEx(String key, int seconds, byte[] value);

	/**
	 * 只有在key不存在时设置key的值。
	 *
	 * @param key
	 * @param value
	 * @return
	 */
	long setNx(String key, String value);

	/**
	 * 只有在key不存在时设置key的值。
	 *
	 * @param key
	 * @param value
	 * @return
	 */
	long setNx(String key, byte[] value);

	/**
	 * 如果key已经存在并且是一个字符串， APPEND命令将value追加到key原来的值的末尾。
	 *
	 * @param key
	 * @param value
	 * @return
	 */
	long append(String key, String value);

	/**
	 * 将给定key的值设为 value ，并返回key的旧值(old value)
	 *
	 * @param key
	 * @param value
	 * @return
	 */
	String getSet(String key, String value);

	/**
	 * 用value参数覆写给定key所储存的字符串值，从偏移量offset开始。
	 *
	 * @param key
	 * @param offset
	 * @param value
	 * @return
	 */
	long setRange(String key, long offset, String value);

	/**
	 * 将key对应的value加上number，并返回操作之后的结果
	 *
	 * @param key
	 * @param number
	 * @return
	 */
	long incrBy(String key, long number);

	/**
	 * 将key对应的value减去number，并返回操作之后的结果
	 *
	 * @param key
	 * @param number
	 * @return
	 */
	long decrBy(String key, long number);

	// Set

	/**
	 * 向集合添加一个或多个成员
	 *
	 * @param key
	 * @param member
	 * @return
	 */
	long sadd(String key, String... member);

	/**
	 * 获取集合的成员数
	 *
	 * @param key
	 * @return
	 */
	long scard(String key);

	/**
	 * 返回集合中的所有成员
	 *
	 * @param key
	 * @return
	 */
	Set<String> smembers(String key);

	/**
	 * 移除并返回集合中的一个随机元素
	 *
	 * @param key
	 * @return
	 */
	String spop(String key);

	/**
	 * 移除集合中一个或多个成员
	 *
	 * @param key
	 * @param member
	 * @return
	 */
	long srem(String key, String... member);

	/**
	 * 随机返回集合一个元素
	 *
	 * @param key
	 * @return
	 */
	String srandmember(String key);

	// List

	/**
	 * 获取列表长度
	 *
	 * @param key
	 * @return
	 */
	long llen(String key);

	/**
	 * 通过索引设置列表元素的值
	 *
	 * @param key
	 * @param index
	 * @param value
	 * @return
	 */
	String lset(String key, int index, String value);

	/**
	 * 通过索引获取列表中的元素
	 *
	 * @param key
	 * @param index
	 * @return
	 */
	String lindex(String key, int index);

	/**
	 * 移出列表的第一个元素，并返回这个元素
	 *
	 * @param key
	 * @return
	 */
	String lpop(String key);

	/**
	 * 移出列表的最后一个元素，并返回这个元素
	 *
	 * @param key
	 * @return
	 */
	String rpop(String key);

	/**
	 * 将值插入到列表头部
	 *
	 * @param key
	 * @param value
	 * @return
	 */
	long lpush(String key, String value);

	/**
	 * 将值插入到列表尾部
	 *
	 * @param key
	 * @param value
	 * @return
	 */
	long rpush(String key, String value);

	/**
	 * 获取列表指定范围内的元素
	 *
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	List<String> lrange(String key, long start, long end);

	/**
	 * 根据参数count的值，移除列表中与参数value相等的元素。
	 *
	 * @param key
	 * @param c     c > 0 : 从表头开始向表尾搜索，移除与 value 相等的元素，数量为 count 。<br/>
	 *              c < 0 : 从表尾开始向表头搜索，移除与 value 相等的元素，数量为 count 的绝对值。<br/>
	 *              c = 0 : 移除表中所有与 value 相等的值。
	 * @param value
	 * @return
	 */
	long lrem(String key, int c, String value);

	/**
	 * 只保留start与end之间的记录，其他元素都删除
	 *
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	String ltrim(String key, int start, int end);

	// Hash

	/**
	 * 删除一个或多个哈希表字段
	 *
	 * @param key
	 * @param fieid
	 * @return
	 */
	long hdel(String key, String fieid);

	/**
	 * 删除key对应的哈希表
	 *
	 * @param key
	 * @return
	 */
	long hdel(String key);

	/**
	 * 查看哈希表 key 中，指定的字段是否存在
	 *
	 * @param key
	 * @param fieid
	 * @return
	 */
	boolean hexists(String key, String fieid);

	/**
	 * 获取存储在哈希表中指定字段的值。
	 *
	 * @param key
	 * @param fieid
	 * @return
	 */
	String hget(String key, String fieid);

	/**
	 * 获取在哈希表中指定key的所有字段和值
	 *
	 * @param key
	 * @return
	 */
	Map<String, String> hgetAll(String key);

	/**
	 * 获取哈希表中的所有字段
	 *
	 * @param key
	 * @return
	 */
	Set<String> hkeys(String key);

	/**
	 * 获取哈希表中字段的数量
	 *
	 * @param key
	 * @return
	 */
	long hlen(String key);

	/**
	 * 获取所有指定字段的值
	 *
	 * @param key
	 * @param fieids
	 * @return
	 */
	List<String> hmget(String key, String... fieids);

	/**
	 * 同时将多个field-value(域-值)对设置到哈希表 key 中
	 *
	 * @param key
	 * @param map
	 * @return
	 */
	String hmset(String key, Map<String, String> map);

	/**
	 * 将哈希表key中的字段field的值设为 value 。
	 *
	 * @param key
	 * @param fieid
	 * @param value
	 * @return
	 */
	long hset(String key, String fieid, String value);

	/**
	 * 只有在字段field不存在时，设置哈希表字段的值。
	 *
	 * @param key
	 * @param fieid
	 * @param value
	 * @return
	 */
	long hsetnx(String key, String fieid, String value);

	/**
	 * 获取哈希表中所有值
	 *
	 * @param key
	 * @return
	 */
	List<String> hvals(String key);

	long hincrBy(String key, String field, long value);

	Pipeline pipelined();

	boolean tryLock(String key, String value, long time);

	boolean releaseLock(String key, String value);
}
