package angularBeans.js.cache;

public class StaticJsCacheFactory {

	private Class<? extends StaticJsLoader> staticJsLoader;
	
	public StaticJsCacheFactory(Class<? extends StaticJsLoader> staticJsLoader){
		this.staticJsLoader = staticJsLoader;
	}
	
	public void BuildStaticJsCache(){
		StaticJsLoader loader;
		try {
			loader = staticJsLoader.newInstance();
			loader.LoadCoreScript();
			loader.LoadExtensions();
			StaticJsCache.Compress();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
}
