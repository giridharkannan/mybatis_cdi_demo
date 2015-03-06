package org.zm.miki;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import controllers.SecurityApp;
import controllers.SecurityApp.SessionUser;

import org.zm.miki.annotation.Context;
import org.zm.miki.annotation.ContextType;

public class ContextManager {
	private static final ThreadLocal<Stack<ContextWrapper>> T_CONTEXT = new ThreadLocal<Stack<ContextWrapper>>();

	// Used to get present user context, no need for neutral context
	private static final ThreadLocal<Stack<SessionUser>> USER_CONTEXT = new ThreadLocal<Stack<SessionUser>>();

	static void setContext(Context ctx) {
		add(ctx, T_CONTEXT.get());
	}

	private static void add(Context ctx, Stack<ContextWrapper> cList) {
		if (cList == null) {
			cList = new Stack<ContextWrapper>();
			cList.push(new ContextWrapper(ctx));
			T_CONTEXT.set(cList);
		} else {
			ContextWrapper cw = cList.see();
			// If account and the context is same increment else add as new
			// context
			SessionUser oacc = cw.us;
			SessionUser nacc = getUserSession(ctx);
			if ((oacc == null && nacc == null)
					|| (oacc != null && nacc != null && oacc.id == nacc.id)
					&& cw.ctx.value() == ctx.value()) {
				cw.refCount++;
			} else {
				cList.push(new ContextWrapper(ctx));
			}
		}
	}

	static void setTransaction() {
		ContextWrapper cw = T_CONTEXT.get().see();

		if (cw.refCount == 1) {
			// There is only one context, change in it
			// as context will be created for this method b4 transaction
			// this cw represents the given context
			cw.newTransaction = true;
			cw.autoCommit = false;
		} else {
			// change the last context transaction to this and add it as new
			// context
			ContextWrapper newCW = new ContextWrapper(cw.ctx, true);
			// remove the last wrongly added context
			cw.refCount--;
			T_CONTEXT.get().push(newCW);
		}
	}

//	static UserSession getCurrentUser() {
//		return T_CONTEXT.get().see().us;
//	}

	static String getCtxTableName(String baseName) {
		ContextWrapper cw = T_CONTEXT.get().see();
		switch (cw.ctx.value()) {
		case APPLICATION:
			if (cw.us == null) {
				throw new RuntimeException("User Context not set");
			}
			return baseName + "_" + Long.toString(cw.us.id);
		case FRAMEWORK:
			return baseName;
		default:
			throw new RuntimeException("Unknown ContextType "
					+ cw.ctx.value());
		}
	}

	static SqlSession getSqlSession(MapperFactory factory) {
		ContextWrapper cw = T_CONTEXT.get().see();

		SqlSession sqlSession = cw.sqlSession;

		if (sqlSession != null) {
			return sqlSession;
		}

		switch (cw.ctx.value()) {
		case FRAMEWORK:
			sqlSession = factory.getFrameworkSession(cw.autoCommit);
			break;
		case APPLICATION:
			sqlSession = factory.getUserSession(cw.us.shard, cw.autoCommit);
			break;
		}
		cw.sqlSession = sqlSession;
		return sqlSession;
	}

	static void clear(boolean gotException) {
		ContextWrapper cw = T_CONTEXT.get().see();
		cw.refCount--;
		if (cw.refCount > 0 || cw.sqlSession == null) {
			return;
		}

		// pop out the last context as reference count is 0
		T_CONTEXT.get().pop();

		// Adding this code at last is not advisable as we may encounter
		// RuntimeException
		// during close or rollback
		if (T_CONTEXT.get().isEmpty()) {
			T_CONTEXT.remove();
		}

		// we can use auto commit to see whether the transaction is new or not.
		// Storing txType just for future reference
		try {
			if (gotException && cw.newTransaction) {
				cw.sqlSession.rollback();
			} else {
				if (!cw.autoCommit) {
					cw.sqlSession.commit();
				}
			}
		} finally {
			cw.sqlSession.close();
		}
	}

	private static class ContextWrapper {
		final Context ctx;
		final SessionUser us;

		int refCount;
		boolean newTransaction;
		SqlSession sqlSession;
		boolean autoCommit;

		ContextWrapper(Context ctx) {
			this(ctx, false);
		}

		ContextWrapper(Context ctx, boolean newTransaction) {
			this.ctx = ctx;
			this.refCount = 1;
			this.us = getUserSession(ctx);
			this.newTransaction = newTransaction;
			// auto-commit for supports transaction
			this.autoCommit = !newTransaction;
		}
	}

	// Will be used exclusively by executor to execute some task
	// in the given user context
	static void setCurrentUser(SessionUser us) {
		Stack<SessionUser> uStack = USER_CONTEXT.get();
		if (uStack == null) {
			uStack = new Stack<SessionUser>();
			USER_CONTEXT.set(uStack);
		}
		// add the user to current user list
		uStack.push(us);
	}

	// Will be used by executor service
	static void clearCurrentUser() {
		USER_CONTEXT.get().pop();
		// If list is empty the clear this list from thread local
		if (USER_CONTEXT.get().isEmpty()) {
			USER_CONTEXT.remove();
		}
	}

	private static SessionUser getUserSession(Context ctx) {
		Stack<SessionUser> uStack = USER_CONTEXT.get();

		// If list already exists
		if (uStack != null) {
			return uStack.see();
		}

		SessionUser session = SecurityApp.getCurrentUser();
		if (session == null && ctx.value() == ContextType.APPLICATION) {
			throw new RuntimeException("No session found");
		}

		return session;
	}
}

class Stack<T> {
	private final List<T> tList;

	public Stack() {
		tList = new ArrayList<T>(2);
	}

	public void push(T data) {
		tList.add(data);
	}

	public T pop() {
		return tList.remove(tList.size() - 1);
	}

	public T see() {
		return tList.get(tList.size() - 1);
	}

	public boolean isEmpty() {
		return tList.isEmpty();
	}
}
