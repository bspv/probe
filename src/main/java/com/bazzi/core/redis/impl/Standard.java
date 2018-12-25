package com.bazzi.core.redis.impl;

import com.bazzi.core.redis.ProbeRedis;
import com.bazzi.core.redis.RedisProfile;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.*;
import redis.clients.util.SafeEncoder;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * REDIS 单机
 *
 * @author PanJianzang
 */
@Slf4j
public class Standard implements ProbeRedis {
	private volatile RedisProfile<JedisPool, Jedis> profile;

	private volatile String host;
	private int port = Protocol.DEFAULT_PORT;
	private volatile String password = null;
	private int qps = 0;
	private int warmup = 0;
	private int database = Protocol.DEFAULT_DATABASE;

	private int maxTotal = JedisPoolConfig.DEFAULT_MAX_TOTAL;
	private int maxIdle = JedisPoolConfig.DEFAULT_MAX_IDLE;
	private int minIdle = JedisPoolConfig.DEFAULT_MIN_IDLE;
	private long maxWaitMillis = JedisPoolConfig.DEFAULT_MAX_WAIT_MILLIS;
	private int timeout = Protocol.DEFAULT_TIMEOUT;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getQps() {
		return qps;
	}

	public void setQps(int qps) {
		this.qps = qps;
	}

	public int getWarmup() {
		return warmup;
	}

	public void setWarmup(int warmup) {
		this.warmup = warmup;
	}

	public int getDatabase() {
		return database;
	}

	public void setDatabase(int database) {
		this.database = database;
	}

	public int getMaxTotal() {
		return maxTotal;
	}

	public void setMaxTotal(int maxTotal) {
		this.maxTotal = maxTotal;
	}

	public int getMaxIdle() {
		return maxIdle;
	}

	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}

	public int getMinIdle() {
		return minIdle;
	}

	public void setMinIdle(int minIdle) {
		this.minIdle = minIdle;
	}

	public long getMaxWaitMillis() {
		return maxWaitMillis;
	}

	public void setMaxWaitMillis(long maxWaitMillis) {
		this.maxWaitMillis = maxWaitMillis;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public void setProfile(RedisProfile<JedisPool, Jedis> profile) {
		this.profile = profile;
	}

	private RedisProfile<JedisPool, Jedis> getProfile() {
		if (this.profile != null)
			return this.profile;
		synchronized (this) {
			if (this.profile != null)
				return this.profile;

			if (host == null || host.trim().length() == 0) {
				throw new IllegalArgumentException("invalid host");
			}

			JedisPoolConfig config = new JedisPoolConfig();
			config.setMaxTotal(this.maxTotal);
			config.setMaxIdle(this.maxIdle);
			config.setMinIdle(this.minIdle);
			config.setMaxWaitMillis(this.maxWaitMillis);

			password = password == null || "".equals(password) ? null : password;

			JedisPool pool = new JedisPool(config, host, port, this.timeout, this.password, this.database);

			this.profile = new RedisProfile<JedisPool, Jedis>(pool, this.qps, this.warmup) {
				public Jedis getConnectionFromPoll(JedisPool resource) {
					return resource.getResource();
				}

				public void closeResource(JedisPool resource) {
					resource.close();
				}
			};
			return this.profile;
		}
	}

	public long ttl(String key) {
		Jedis jedis = null;
		try {
			jedis = getProfile().getConnection();
			return jedis.ttl(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (jedis != null)
				jedis.close();
		}
	}

	public long persist(String key) {
		Jedis jedis = null;
		try {
			jedis = getProfile().getConnection();
			return jedis.persist(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (jedis != null)
				jedis.close();
		}
	}

	public long expire(String key, int seconds) {
		Jedis jedis = null;
		try {
			jedis = getProfile().getConnection();
			return jedis.expire(key, seconds);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (jedis != null)
				jedis.close();
		}
	}

	public long expireAt(String key, long timestamp) {
		Jedis jedis = null;
		try {
			jedis = getProfile().getConnection();
			return jedis.expireAt(key, timestamp);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (jedis != null)
				jedis.close();
		}
	}

	public boolean exists(String key) {
		Jedis jedis = null;
		try {
			jedis = getProfile().getConnection();
			return jedis.exists(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (jedis != null)
				jedis.close();
		}
	}

	public void del(String key) {
		Jedis jedis = null;
		try {
			jedis = getProfile().getConnection();
			jedis.del(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (jedis != null)
				jedis.close();
		}
	}

	public String type(String key) {
		Jedis jedis = null;
		try {
			jedis = getProfile().getConnection();
			return jedis.type(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (jedis != null)
				jedis.close();
		}
	}

	public String set(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = getProfile().getConnection();
			return jedis.set(key, value);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (jedis != null)
				jedis.close();
		}
	}

	public String set(String key, byte[] value) {
		Jedis jedis = null;
		try {
			jedis = getProfile().getConnection();
			return jedis.set(SafeEncoder.encode(key), value);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (jedis != null)
				jedis.close();
		}
	}

	public String get(String key) {
		Jedis jedis = null;
		try {
			jedis = getProfile().getConnection();
			return jedis.get(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (jedis != null)
				jedis.close();
		}
	}

	public byte[] getByte(String key) {
		Jedis jedis = null;
		try {
			jedis = getProfile().getConnection();
			return jedis.get(SafeEncoder.encode(key));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (jedis != null)
				jedis.close();
		}
	}

	public String setEx(String key, int seconds, String value) {
		Jedis jedis = null;
		try {
			jedis = getProfile().getConnection();
			return jedis.setex(key, seconds, value);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (jedis != null)
				jedis.close();
		}
	}

	public String setEx(String key, int seconds, byte[] value) {
		Jedis jedis = null;
		try {
			jedis = getProfile().getConnection();
			return jedis.setex(SafeEncoder.encode(key), seconds, value);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (jedis != null)
				jedis.close();
		}
	}

	public long setNx(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = getProfile().getConnection();
			return jedis.setnx(key, value);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (jedis != null)
				jedis.close();
		}
	}

	public long setNx(String key, byte[] value) {
		Jedis jedis = null;
		try {
			jedis = getProfile().getConnection();
			return jedis.setnx(SafeEncoder.encode(key), value);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (jedis != null)
				jedis.close();
		}
	}

	public long append(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = getProfile().getConnection();
			return jedis.append(key, value);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (jedis != null)
				jedis.close();
		}
	}

	public String getSet(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = getProfile().getConnection();
			return jedis.getSet(key, value);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (jedis != null)
				jedis.close();
		}
	}

	public long setRange(String key, long offset, String value) {
		Jedis jedis = null;
		try {
			jedis = getProfile().getConnection();
			return jedis.setrange(key, offset, value);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (jedis != null)
				jedis.close();
		}
	}

	public long incrBy(String key, long number) {
		Jedis jedis = null;
		try {
			jedis = getProfile().getConnection();
			return jedis.incrBy(key, number);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (jedis != null)
				jedis.close();
		}
	}

	public long decrBy(String key, long number) {
		Jedis jedis = null;
		try {
			jedis = getProfile().getConnection();
			return jedis.decrBy(key, number);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (jedis != null)
				jedis.close();
		}
	}

	public long sadd(String key, String... members) {
		Jedis jedis = null;
		try {
			jedis = getProfile().getConnection();
			return jedis.sadd(key, members);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (jedis != null)
				jedis.close();
		}
	}

	public long scard(String key) {
		Jedis jedis = null;
		try {
			jedis = getProfile().getConnection();
			return jedis.scard(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (jedis != null)
				jedis.close();
		}
	}

	public Set<String> smembers(String key) {
		Jedis jedis = null;
		try {
			jedis = getProfile().getConnection();
			return jedis.smembers(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (jedis != null)
				jedis.close();
		}
	}

	public String spop(String key) {
		Jedis jedis = null;
		try {
			jedis = getProfile().getConnection();
			return jedis.spop(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (jedis != null)
				jedis.close();
		}
	}

	public long srem(String key, String... member) {
		Jedis jedis = null;
		try {
			jedis = getProfile().getConnection();
			return jedis.srem(key, member);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (jedis != null)
				jedis.close();
		}
	}

	public String srandmember(String key) {
		Jedis jedis = null;
		try {
			jedis = getProfile().getConnection();
			return jedis.srandmember(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (jedis != null)
				jedis.close();
		}
	}

	public long llen(String key) {
		Jedis jedis = null;
		try {
			jedis = getProfile().getConnection();
			return jedis.llen(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (jedis != null)
				jedis.close();
		}
	}

	public String lset(String key, int index, String value) {
		Jedis jedis = null;
		try {
			jedis = getProfile().getConnection();
			return jedis.lset(key, index, value);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (jedis != null)
				jedis.close();
		}
	}

	public String lindex(String key, int index) {
		Jedis jedis = null;
		try {
			jedis = getProfile().getConnection();
			return jedis.lindex(key, index);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (jedis != null)
				jedis.close();
		}
	}

	public String lpop(String key) {
		Jedis jedis = null;
		try {
			jedis = getProfile().getConnection();
			return jedis.lpop(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (jedis != null)
				jedis.close();
		}
	}

	public String rpop(String key) {
		Jedis jedis = null;
		try {
			jedis = getProfile().getConnection();
			return jedis.rpop(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (jedis != null)
				jedis.close();
		}
	}

	public long lpush(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = getProfile().getConnection();
			return jedis.lpush(key, value);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (jedis != null)
				jedis.close();
		}
	}

	public long rpush(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = getProfile().getConnection();
			return jedis.rpush(key, value);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (jedis != null)
				jedis.close();
		}
	}

	public List<String> lrange(String key, long start, long end) {
		Jedis jedis = null;
		try {
			jedis = getProfile().getConnection();
			return jedis.lrange(key, start, end);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (jedis != null)
				jedis.close();
		}
	}

	public long lrem(String key, int c, String value) {
		Jedis jedis = null;
		try {
			jedis = getProfile().getConnection();
			return jedis.lrem(key, c, value);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (jedis != null)
				jedis.close();
		}
	}

	public String ltrim(String key, int start, int end) {
		Jedis jedis = null;
		try {
			jedis = getProfile().getConnection();
			return jedis.ltrim(key, start, end);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (jedis != null)
				jedis.close();
		}
	}

	public long hdel(String key, String fieid) {
		Jedis jedis = null;
		try {
			jedis = getProfile().getConnection();
			return jedis.hdel(key, fieid);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (jedis != null)
				jedis.close();
		}
	}

	public long hdel(String key) {
		Jedis jedis = null;
		try {
			jedis = getProfile().getConnection();
			return jedis.hdel(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (jedis != null)
				jedis.close();
		}
	}

	public boolean hexists(String key, String fieid) {
		Jedis jedis = null;
		try {
			jedis = getProfile().getConnection();
			return jedis.hexists(key, fieid);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (jedis != null)
				jedis.close();
		}
	}

	public String hget(String key, String fieid) {
		Jedis jedis = null;
		try {
			jedis = getProfile().getConnection();
			return jedis.hget(key, fieid);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (jedis != null)
				jedis.close();
		}
	}

	public Map<String, String> hgetAll(String key) {
		Jedis jedis = null;
		try {
			jedis = getProfile().getConnection();
			return jedis.hgetAll(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (jedis != null)
				jedis.close();
		}
	}

	public Set<String> hkeys(String key) {
		Jedis jedis = null;
		try {
			jedis = getProfile().getConnection();
			return jedis.hkeys(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (jedis != null)
				jedis.close();
		}
	}

	public long hlen(String key) {
		Jedis jedis = null;
		try {
			jedis = getProfile().getConnection();
			return jedis.hlen(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (jedis != null)
				jedis.close();
		}
	}

	public List<String> hmget(String key, String... fieids) {
		Jedis jedis = null;
		try {
			jedis = getProfile().getConnection();
			return jedis.hmget(key, fieids);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (jedis != null)
				jedis.close();
		}
	}

	public String hmset(String key, Map<String, String> map) {
		Jedis jedis = null;
		try {
			jedis = getProfile().getConnection();
			return jedis.hmset(key, map);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (jedis != null)
				jedis.close();
		}
	}

	public long hset(String key, String fieid, String value) {
		Jedis jedis = null;
		try {
			jedis = getProfile().getConnection();
			return jedis.hset(key, fieid, value);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (jedis != null)
				jedis.close();
		}
	}

	public long hsetnx(String key, String fieid, String value) {
		Jedis jedis = null;
		try {
			jedis = getProfile().getConnection();
			return jedis.hsetnx(key, fieid, value);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (jedis != null)
				jedis.close();
		}
	}

	public List<String> hvals(String key) {
		Jedis jedis = null;
		try {
			jedis = getProfile().getConnection();
			return jedis.hvals(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (jedis != null)
				jedis.close();
		}
	}

	public long hincrBy(String key, String field, long value) {
		Jedis jedis = null;
		try {
			jedis = getProfile().getConnection();
			return jedis.hincrBy(key, field, value);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (jedis != null)
				jedis.close();
		}
	}

	public Pipeline pipelined() {
		Jedis jedis = null;
		try {
			jedis = getProfile().getConnection();
			return jedis.pipelined();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (jedis != null)
				jedis.close();
		}
	}

	public boolean tryLock(String key, String value, long time) {
		Jedis jedis = null;
		try {
//			String lua = "if redis.call('setnx', KEYS[1], ARGV[1]) == 1 then " +
//					" return redis.call('pexpire', KEYS[1], ARGV[2]) " +
//					" else " +
//					" return 0 " +
//					" end";
//			jedis = getProfile().getConnection();
//			return Integer.parseInt(String.valueOf(jedis.eval(lua, 3, key, value, time + "")));
			jedis = getProfile().getConnection();
			return "OK".equals(jedis.set(key, value, "NX", "PX", time)) ;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (jedis != null)
				jedis.close();
		}
	}

	public boolean releaseLock(String key, String value) {
		Jedis jedis = null;
		try {
			String lua = "if redis.call('get',KEYS[1]) == ARGV[1] then " +
					" return redis.call('del',KEYS[1]) " +
					" else " +
					" return 0 " +
					" end";
			jedis = getProfile().getConnection();
			return "1".equals(String.valueOf(jedis.eval(lua, 1, key, value)));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (jedis != null)
				jedis.close();
		}
	}
}
