package kg.gazprom.payments.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Универсальная утилита для чтения строк из:
 * 1) classpath (src/main/resources) — через ClassLoader
 * 2) файловой системы — через java.nio.file.Files
 *
 * Совместима с Java 8 (нет вызовов Path.of/Files.readString/readAllBytes(InputStream)).
 */
public class fs {
	private static final Map<String, String> cache = new ConcurrentHashMap<>();

	/**
	 * Читает содержимое как строку UTF-8.
	 * Сначала пытается найти ресурс в classpath (например, "template/ReadingSection.html"),
	 * если не найден — читает как обычный файл из ФС (абсолютный или относительный путь).
	 */
	public static String readFileAsString(String path) throws IOException {
		String cached = cache.get(path);
		if (cached != null) {
			return cached;
		}

		// 1) Пытаемся прочитать из classpath
		String fromCp = readFromClasspath(path);
		if (fromCp != null) {
			cache.put(path, fromCp);
			return fromCp;
		}

		// 2) Фоллбэк — из файловой системы
		byte[] bytes = Files.readAllBytes(Paths.get(path));
		String content = new String(bytes, StandardCharsets.UTF_8);
		cache.put(path, content);
		return content;
	}

	/**
	 * Читает ресурс из classpath (src/main/resources) как UTF-8-строку.
	 * Возвращает null, если ресурса нет.
	 */
	private static String readFromClasspath(String resourcePath) throws IOException {
		InputStream is = fs.class.getClassLoader().getResourceAsStream(resourcePath);
		if (is == null) return null;

		try (InputStream in = is) {
			byte[] bytes = toBytes(in);
			return new String(bytes, StandardCharsets.UTF_8);
		}
	}

	/**
	 * Замена readAllBytes(InputStream) для Java 8.
	 */
	private static byte[] toBytes(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
		byte[] buf = new byte[8192];
		int n;
		while ((n = in.read(buf)) != -1) {
			out.write(buf, 0, n);
		}
		return out.toByteArray();
	}

	/** Сброс кэша. */
	public static void clearCache() {
		cache.clear();
	}
}
