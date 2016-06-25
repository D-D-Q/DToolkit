package du.core;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisDB {
	
	private JedisPool pool;
	private Jedis jedis;

	public JedisDB() {
		
//		jedis = new Jedis("192.168.1.102");
		
		pool = new JedisPool(new JedisPoolConfig(), "192.168.1.102", 6379, 10000, "d");
		jedis = pool.getResource();
	}

	public Jedis getJedis() {
		return jedis;
	}

	public void setJedis(Jedis jedis) {
		this.jedis = jedis;
	}
	
	public void colse() {
		
		pool.close();
	}
}
