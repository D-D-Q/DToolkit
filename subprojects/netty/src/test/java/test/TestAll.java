package test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.Test;

public class TestAll {

	
	public void time() {
		
		System.out.println(System.currentTimeMillis());
		System.out.println(Long.MAX_VALUE);
		
		HashMap<Object,Object> map = new HashMap<>(10, 0.75f);
		map.put(null, null);
		
		new ArrayList();
		
		Object[] a = {new Object(), new Object()};
		String[] b = {"a", "b"};
		int [] c = {1, 2};
		
		
		ConcurrentHashMap<Object,Object> concurrentHashMap = new ConcurrentHashMap<>(0, 0.75f, 16);
		concurrentHashMap.put(null, null);
	}
	
	public void angle() {
		
		float angle = 90f;
		
		angle -= 22.5f; // 方便判断，例:45度到90度之间是朝上
		
		System.out.println(angle/45);
	}
	
	@Test
	public void format() {
		System.out.println(String.format("%.0f", 0.5354));
	}
}
