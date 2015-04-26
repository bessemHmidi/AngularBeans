package angularBeans.util;

public interface ModelQuery {

	public ModelQuery setProperty(String model, Object value);

	// Map<String, Object> getData();

	ModelQuery pushTo(String objectName, Object value);

	ModelQuery removeFrom(String objectName, Object value);

	ModelQuery removeFrom(String objectName, Object value, String key);

}
