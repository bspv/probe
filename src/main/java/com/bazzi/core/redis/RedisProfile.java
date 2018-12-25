package com.bazzi.core.redis;

import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public abstract class RedisProfile<R, T> {
	private R resource;
	private RateLimiter rateLimiter;

	protected RedisProfile(R resource, Integer QPS, Integer WARMUP) {
		super();

		this.resource = resource;

		if (QPS == null || QPS <= 0) {
			this.rateLimiter = null;
		} else if (WARMUP != null && WARMUP > 1) {
			this.rateLimiter = RateLimiter.create(QPS, WARMUP, TimeUnit.SECONDS);
		} else {
			this.rateLimiter = RateLimiter.create(QPS);
		}
	}

	protected abstract T getConnectionFromPoll(R resource);

	public T getConnection() {
		if (this.resource == null)
			return null;
		T client ;
		try {
			client = getConnectionFromPoll(this.resource);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		}

		if (this.rateLimiter == null)
			return client;

		this.rateLimiter.acquire();

		return client;
	}

	protected abstract void closeResource(R resource);

	public void close() {
		if (this.resource != null) {
			closeResource(this.resource);
			this.resource = null;
		}
		if (this.rateLimiter != null) {
			this.rateLimiter = null;
		}
	}
}
