package org.zm.miki;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.zm.miki.annotation.Transaction;

@Interceptor
@Transaction
public class TransactionInterceptor {
	@AroundInvoke
	public Object ctx(InvocationContext ctx) throws Exception {
		ContextManager.setTransaction();
		Object rValue = ctx.proceed();
		//Don't catch exception; ContextInterceptor will handle it
		return rValue;
	}
}
