package com.bazzi.core.redis.impl;

import com.bazzi.core.redis.ProbeRedis;
import com.bazzi.core.redis.RedisProfile;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.*;
import redis.clients.util.SafeEncoder;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * REDIS 集群
 * 
 * @author PanJianzang
 *
 */
@Slf4j
public class Cluster implements ProbeRedis {
	private volatile RedisProfile<JedisCluster, JedisCluster> profile;

	private String cluster;
	private String password;
	private int qps = 0;
	private int warmup = 0;

	private int maxTotal = JedisPoolConfig.DEFAULT_MAX_TOTAL;
	private int maxIdle = JedisPoolConfig.DEFAULT_MAX_IDLE;
	private int minIdle = JedisPoolConfig.DEFAULT_MIN_IDLE;
	private long maxWaitMillis = JedisPoolConfig.DEFAULT_MAX_WAIT_MILLIS;
	private int connectionTimeout = Protocol.DEFAULT_TIMEOUT;
	private int soTimeout = 3000;
	private int maxAttempts = 5;

//	public String getCluster() {
//		return cluster;
//	}
//
//	public void setCluster(String cluster) {
//		this.cluster = cluster;
//	}

//	public String getPassword() {
//		return password;
//	}
//
//	public void setPassword(String password) {
//		this.password = password;
//	}

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

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public int getSoTimeout() {
		return soTimeout;
	}

	public void setSoTimeout(int soTimeout) {
		this.soTimeout = soTimeout;
	}

	public int getMaxAttempts() {
		return maxAttempts;
	}

	public void setMaxAttempts(int maxAttempts) {
		this.maxAttempts = maxAttempts;
	}

	public void setProfile(RedisProfile<JedisCluster, JedisCluster> profile) {
		this.profile = profile;
	}

	public RedisProfile<JedisCluster, JedisCluster> getProfile() {
		if (this.profile != null)
			return this.profile;
		synchronized (this) {
			if (this.profile != null)
				return this.profile;

			String regEx = "^((\\d{1,3}\\.){3}\\d{1,3}:\\d{1,5},)*((\\d{1,3}\\.){3}\\d{1,3}:\\d{1,5})$";
			if (cluster == null || !Pattern.compile(regEx).matcher(cluster).matches()) {
				throw new IllegalArgumentException("invalid cluster");
			}

			JedisPoolConfig config = new JedisPoolConfig();
			config.setMaxTotal(this.maxTotal);
			config.setMaxIdle(this.maxIdle);
			config.setMinIdle(this.minIdle);
			config.setMaxWaitMillis(this.maxWaitMillis);

			password = password == null || "".equals(password) ? null : password;

			Set<HostAndPort> nodes = new HashSet<>();
			for (String str : cluster.split(",")) {
				String[] arr = str.split(":");
				nodes.add(new HostAndPort(arr[0], Integer.parseInt(arr[1])));
			}

			JedisCluster client = password == null ? new JedisCluster(nodes, config)
					: new JedisCluster(nodes, connectionTimeout, soTimeout, maxAttempts, password, config);
			this.profile = new RedisProfile<JedisCluster, JedisCluster>(client, this.qps, this.warmup) {
				public JedisCluster getConnectionFromPoll(JedisCluster resource) {
					return resource;
				}

				public void closeResource(JedisCluster resource) {
				}
			};
			return this.profile;
		}
	}

	public long ttl(String key) {
		try {
			JedisCluster jedis = getProfile().getConnection();
			return jedis.ttl(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public long persist(String key) {
		try {
			JedisCluster jedis = getProfile().getConnection();
			return jedis.persist(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public long expire(String key, int seconds) {
		try {
			JedisCluster jedis = getProfile().getConnection();
			return jedis.expire(key, seconds);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public long expireAt(String key, long timestamp) {
		try {
			JedisCluster jedis = getProfile().getConnection();
			return jedis.expireAt(key, timestamp);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public boolean exists(String key) {
		try {
			JedisCluster jedis = getProfile().getConnection();
			return jedis.exists(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public void del(String key) {
		try {
			JedisCluster jedis = getProfile().getConnection();
			jedis.del(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public String type(String key) {
		try {
			JedisCluster jedis = getProfile().getConnection();
			return jedis.type(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public String set(String key, String value) {
		try {
			JedisCluster jedis = getProfile().getConnection();
			return jedis.set(key, value);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public String set(String key, byte[] value) {
		try {
			JedisCluster jedis = getProfile().getConnection();
			return jedis.set(SafeEncoder.encode(key), value);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public String get(String key) {
		try {
			JedisCluster jedis = getProfile().getConnection();
			return jedis.get(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public byte[] getByte(String key) {
		try {
			JedisCluster jedis = getProfile().getConnection();
			return jedis.get(SafeEncoder.encode(key));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public String setEx(String key, int seconds, String value) {
		try {
			JedisCluster jedis = getProfile().getConnection();
			return jedis.setex(key, seconds, value);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public String setEx(String key, int seconds, byte[] value) {
		try {
			JedisCluster jedis = getProfile().getConnection();
			return jedis.setex(SafeEncoder.encode(key), seconds, value);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public long setNx(String key, String value) {
		try {
			JedisCluster jedis = getProfile().getConnection();
			return jedis.setnx(key, value);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public long setNx(String key, byte[] value) {
		try {
			JedisCluster jedis = getProfile().getConnection();
			return jedis.setnx(SafeEncoder.encode(key), value);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public long append(String key, String value) {
		try {
			JedisCluster jedis = getProfile().getConnection();
			return jedis.append(key, value);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public String getSet(String key, String value) {
		try {
			JedisCluster jedis = getProfile().getConnection();
			return jedis.getSet(key, value);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public long setRange(String key, long offset, String value) {
		try {
			JedisCluster jedis = getProfile().getConnection();
			return jedis.setrange(key, offset, value);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public long incrBy(String key, long number) {
		try {
			JedisCluster jedis = getProfile().getConnection();
			return jedis.incrBy(key, number);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public long decrBy(String key, long number) {
		try {
			JedisCluster jedis = getProfile().getConnection();
			return jedis.decrBy(key, number);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public long sadd(String key, String... member) {
		try {
			JedisCluster jedis = getProfile().getConnection();
			return jedis.sadd(key, member);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public long scard(String key) {
		try {
			JedisCluster jedis = getProfile().getConnection();
			return jedis.scard(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public Set<String> smembers(String key) {
		try {
			JedisCluster jedis = getProfile().getConnection();
			return jedis.smembers(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public String spop(String key) {
		try {
			JedisCluster jedis = getProfile().getConnection();
			return jedis.spop(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public long srem(String key, String... member) {
		try {
			JedisCluster jedis = getProfile().getConnection();
			return jedis.srem(key, member);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public String srandmember(String key) {
		try {
			JedisCluster jedis = getProfile().getConnection();
			return jedis.srandmember(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public long llen(String key) {
		try {
			JedisCluster jedis = getProfile().getConnection();
			return jedis.llen(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public String lset(String key, int index, String value) {
		try {
			JedisCluster jedis = getProfile().getConnection();
			return jedis.lset(key, index, value);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public String lindex(String key, int index) {
		try {
			JedisCluster jedis = getProfile().getConnection();
			return jedis.lindex(key, index);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public String lpop(String key) {
		try {
			JedisCluster jedis = getProfile().getConnection();
			return jedis.lpop(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public String rpop(String key) {
		try {
			JedisCluster jedis = getProfile().getConnection();
			return jedis.rpop(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public long lpush(String key, String value) {
		try {
			JedisCluster jedis = getProfile().getConnection();
			return jedis.lpush(key, value);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public long rpush(String key, String value) {
		try {
			JedisCluster jedis = getProfile().getConnection();
			return jedis.rpush(key, value);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public List<String> lrange(String key, long start, long end) {
		try {
			JedisCluster jedis = getProfile().getConnection();
			return jedis.lrange(key, start, end);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public long lrem(String key, int c, String value) {
		try {
			JedisCluster jedis = getProfile().getConnection();
			return jedis.lrem(key, c, value);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public String ltrim(String key, int start, int end) {
		try {
			JedisCluster jedis = getProfile().getConnection();
			return jedis.ltrim(key, start, end);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public long hdel(String key, String fieid) {
		try {
			JedisCluster jedis = getProfile().getConnection();
			return jedis.hdel(key, fieid);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public long hdel(String key) {
		try {
			JedisCluster jedis = getProfile().getConnection();
			return jedis.hdel(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public boolean hexists(String key, String fieid) {
		try {
			JedisCluster jedis = getProfile().getConnection();
			return jedis.hexists(key, fieid);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public String hget(String key, String fieid) {
		try {
			JedisCluster jedis = getProfile().getConnection();
			return jedis.hget(key, fieid);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public Map<String, String> hgetAll(String key) {
		try {
			JedisCluster jedis = getProfile().getConnection();
			return jedis.hgetAll(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public Set<String> hkeys(String key) {
		try {
			JedisCluster jedis = getProfile().getConnection();
			return jedis.hkeys(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public long hlen(String key) {
		try {
			JedisCluster jedis = getProfile().getConnection();
			return jedis.hlen(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public List<String> hmget(String key, String... fieids) {
		try {
			JedisCluster jedis = getProfile().getConnection();
			return jedis.hmget(key, fieids);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public String hmset(String key, Map<String, String> map) {
		try {
			JedisCluster jedis = getProfile().getConnection();
			return jedis.hmset(key, map);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public long hset(String key, String fieid, String value) {
		try {
			JedisCluster jedis = getProfile().getConnection();
			return jedis.hset(key, fieid, value);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public long hsetnx(String key, String fieid, String value) {
		try {
			JedisCluster jedis = getProfile().getConnection();
			return jedis.hsetnx(key, fieid, value);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public List<String> hvals(String key) {
		try {
			JedisCluster jedis = getProfile().getConnection();
			return jedis.hvals(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public long hincrBy(String key, String field, long value) {
		try {
			JedisCluster jedis = getProfile().getConnection();
			return jedis.hincrBy(key, field, value);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public Pipeline pipelined() {
		return null;
	}

	public boolean tryLock(String key, String value, long time) {
		try {
			JedisCluster jedis = getProfile().getConnection();
			return "OK".equals(jedis.set(key, value, "NX", "PX", time));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public boolean releaseLock(String key, String value) {
		try {
			JedisCluster jedis = getProfile().getConnection();
			String lua = "if redis.call('get',KEYS[1]) == ARGV[1] then " +
					" return redis.call('del',KEYS[1]) " +
					" else " +
					" return 0 " +
					" end";
			return "1".equals(String.valueOf(jedis.eval(lua, 1, key, value)));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}
}
