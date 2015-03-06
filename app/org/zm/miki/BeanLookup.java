package org.zm.miki;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

public class BeanLookup {
	private static Weld weld;
	private static WeldContainer container;
	
	public static void init() {
		weld = new Weld();
		container = weld.initialize();
	}
	
	public static void destroy() {
		weld.shutdown();
		container = null;
		weld = null;
	}
	
	public static <T extends BaseDO<?>> T getDOInstance(Class<T> clazz) {
		return container.instance().select(clazz).get();
	}
	
	public static <A> A getControllerInstance(Class<A> clazz) {
		return container.instance().select(clazz).get();
	}
}
