package controllers.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import play.libs.F;
import play.mvc.Action;
import play.mvc.SimpleResult;
import play.mvc.With;
import play.mvc.Http.Context;
import controllers.SecurityApp;
import controllers.SecurityApp.SessionUser;
import controllers.routes;

public class Security {
    @With(PasswordAuthenticatedAction.class)
    @Target({ElementType.TYPE, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface PasswordAuthenticated {
    }
    
    public static class PasswordAuthenticatedAction extends Action<PasswordAuthenticated> {
    	@Override
        public F.Promise<SimpleResult> call(final Context ctx) {
            try {
            	SessionUser session = SecurityApp.getCurrentUser();
            	if(session != null) {	
            		return delegate.call(ctx);
            	}
            	return F.Promise.pure((SimpleResult) redirect(routes.SecurityApp.login()));
            } catch (RuntimeException e) {
            	throw e;
            } catch (Throwable t) {
            	throw new RuntimeException(t);
            }
        }
    }
}
