package ppp.util;

public class AnnotationReflection {
	public static String getString(Class<?> annotation, String fieldName) {
		try {
			return (String) annotation.getMethod(fieldName).getDefaultValue();
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO log
			return null;
		}
	}
}
