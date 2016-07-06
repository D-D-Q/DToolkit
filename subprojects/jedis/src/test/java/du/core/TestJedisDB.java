package du.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Set;

import org.junit.Test;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import du.core.modle.Msg;
import redis.clients.jedis.Jedis;

public class TestJedisDB {

	public void set() {
		
		JedisDB jedisDB = new JedisDB();
		Jedis jedis = jedisDB.getJedis();
		
		jedis.set("key", "value1");
		
		Set<String> keys = jedis.keys("*");
		
		System.out.println(jedis.get("key"));
		
		System.out.println(keys);
		
		jedisDB.colse();
	}
	
	@Test
	public void object() {
		
		JedisDB jedisDB = new JedisDB();
		Jedis jedis = jedisDB.getJedis();
		
		Msg msg = new Msg();
		msg.setId(1);
		msg.setName("world");
		
		Kryo kryo = new Kryo();
		
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		Output output = new Output(byteArrayOutputStream);
		
		kryo.writeObject(output, msg);
		output.close();
		
		jedis.set("key".getBytes(), byteArrayOutputStream.toByteArray());
		
		byte[] bs = jedis.get("key".getBytes());
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bs);
		Input input = new Input(byteArrayInputStream);
		
		Msg msg2 = kryo.readObject(input, Msg.class);
		input.close();
		
		System.out.println(msg2.getId() + ":" + msg2.getName());
		
		jedisDB.colse();
	}
}
