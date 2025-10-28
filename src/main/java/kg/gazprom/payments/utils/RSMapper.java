package kg.gazprom.payments.utils;

import java.lang.reflect.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RSMapper {

	// TODO
	// поддержкой snake_case → camelCase,
	// автоматическим приведением типов,
	// возможностью работать со списками (List<T> mapAll(ResultSet, Class<T>))?

	// Разобрать
	// ConcurrentHashMap
	// Class, Field
	// Class<?> - дженерик

	// Кэш метаданных классов: Class<?> → Map(columnName → Field)
	private static final Map<Class<?>, Map<String, Field>> CACHE = new ConcurrentHashMap<>();

	/**
	 * Маппинг одной строки ResultSet в объект типа T
	 */
	public static <T> T mapRow(ResultSet rs, Class<T> classDTO) throws Exception {
		T instanceDTO = classDTO.getDeclaredConstructor().newInstance();
		ResultSetMetaData metaRS = rs.getMetaData();
		int columnCount = metaRS.getColumnCount();

		// Берём поля из кеша или создаём при первом вызове
		Map<String, Field> fieldMap = CACHE.computeIfAbsent(classDTO, RSMapper::analyzeClass);

		for (int i = 1; i <= columnCount; i++) {
			String column = metaRS.getColumnLabel(i).toLowerCase();
			// Ищем соответствующее поле
			Field field = fieldMap.get(column);
			if (field == null) continue;

			Object value = rs.getObject(i); // Посколько мы не знаем тип значения - берем как Object (ссылку), текущей строки
			// Можно добавить типизацию (int, double, boolean и т.п.)
			field.set(instanceDTO, value);
		}

		return instanceDTO;
	}

	/**
	 * Маппинг всего ResultSet → List<T>
	 */
	public static <T> List<T> mapToList(ResultSet rs, Class<T> type) throws Exception {
		List<T> list = new ArrayList<>();
		while (rs.next()) {
			list.add(mapRow(rs, type));
		}
		return list;
	}

	/**
	 * Анализирует класс, сохраняет соответствие "имя_поля_в_базе" → Field.
	 * Ключ: snake_case (т.е. как в БД)
	 */
	private static Map<String, Field> analyzeClass(Class<?> classDTO) {
		Map<String, Field> map = new HashMap<>();
		for (Field f : classDTO.getDeclaredFields()) {
			f.setAccessible(true);
			// Добавляем два варианта ключей: прямое имя и его snake_case
			map.put(f.getName().toLowerCase(), f);
			map.put(toSnakeCase(f.getName()), f);
		}
		return map;
	}

	/**
	 * Преобразует имя поля из camelCase → snake_case.
	 * Пример: "userName" → "user_name"
	 */
	private static String toSnakeCase(String name) {
		StringBuilder sb = new StringBuilder();
		for (char c : name.toCharArray()) {
			if (Character.isUpperCase(c)) {
				sb.append('_').append(Character.toLowerCase(c));
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}
}
