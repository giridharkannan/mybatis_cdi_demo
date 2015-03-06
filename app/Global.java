import org.zm.miki.BeanLookup;

import play.Application;
import play.GlobalSettings;

public class Global extends GlobalSettings {
	
	@Override
	public void onStart(Application app) {
		BeanLookup.init();
	}

	@Override
	public void onStop(Application app) {
		BeanLookup.destroy();
	}

	@Override
	public <A> A getControllerInstance(Class<A> clazz) {
		return BeanLookup.getControllerInstance(clazz);
	}
}