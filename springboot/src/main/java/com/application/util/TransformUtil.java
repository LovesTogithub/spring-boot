package com.application.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by acer_liuyutong on 2017/5/24.
 */
public class TransformUtil {

	/**
	 * 把对象的非空属性转为map
	 * @param obj Object
	 * @return Map<String, String>
	 */
	public static Map<String, Object> object2Map(Object obj) {

		Map<String, Object> map = new HashMap<>();
		// System.out.println(obj.getClass());
		// 获取f对象对应类中的所有属性域
		Field[] fields = obj.getClass().getDeclaredFields();
		for (Field field : fields) {
			String varName = field.getName();
			try {
				// 获取原来的访问控制权限
				boolean accessFlag = field.isAccessible();
				// 修改访问控制权限
				field.setAccessible(true);
				// 获取在对象f中属性fields[i]对应的对象中的变量
				Object o = field.get(obj);
				if (o != null)
					map.put(varName, o);
				// System.out.println("传入的对象中包含一个如下的变量：" + varName + " = " + o);
				// 恢复访问控制权限
				field.setAccessible(accessFlag);
			} catch (IllegalArgumentException | IllegalAccessException ex) {
				ex.printStackTrace();
			}
		}
		return map;

	}
}
