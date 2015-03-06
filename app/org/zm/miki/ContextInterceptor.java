package org.zm.miki;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.zm.miki.annotation.Context;
import org.zm.miki.annotation.ContextType;

@Interceptor
@Context(ContextType.APPLICATION)
public class ContextInterceptor {

	@AroundInvoke
	public Object ctx(InvocationContext ctx) throws Exception {
		Context c = getContext(ctx.getTarget().getClass());
		ContextManager.setContext(c);
		
		boolean gotException = false;
		Object rValue = null;
		
		try {
			rValue = ctx.proceed();
		} catch(Exception e) {
			gotException = true;
			throw e;
		} finally {
			ContextManager.clear(gotException);
		}
		
		return rValue;
	}
	
	private static Context getContext(Class<?> proxy) throws ClassNotFoundException {
		String name = proxy.getCanonicalName();
		name = name.substring(0, name.indexOf('$'));
		return Class.forName(name).getAnnotation(Context.class);
	}
}
