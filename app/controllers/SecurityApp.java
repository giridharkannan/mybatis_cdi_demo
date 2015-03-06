package controllers;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.codec.binary.Base64;
import org.zm.model.UserAccount;

import play.mvc.Controller;
import play.mvc.Result;

@ApplicationScoped
public class SecurityApp extends Controller {

	private static final String SID = "sid";
	
	private static final Map<String, SessionUser> SESSION_MAP = 
			new ConcurrentHashMap<String, SessionUser>();
	
	private String generateSessionID() {
		String sid = UUID.randomUUID().toString();
		return new String(Base64.encodeBase64(sid.getBytes()));
	}
	
	public static class SessionUser {
		public final long id;
		public final long shard;
		public final String uname;
		
		private SessionUser(UserAccount acc) {
			this.id = acc.getId();
			this.shard = acc.getShard();
			this.uname = acc.getUname();
		}
	}
	
	public static SessionUser getCurrentUser() {
		String session = session().get(SID);
		SessionUser user = null;
		if(session != null) {
			user = SESSION_MAP.get(session);
			if(user == null) {
				//Stored in ram memory so will not survive restart
				session().clear();
			}
			return user;
		}
		return null;
	}
	
	public Result loginGet() {
		if(session().get(SID) != null) {
    		return redirect("/");
    	}
		return ok(views.html.login.render());
	}
	
	private void createSession(UserAccount acc) {
		String sid = generateSessionID();
		SESSION_MAP.put(sid, new SessionUser(acc));
		session().put(SID, sid);
	}
	
    public Result login() {
    	if(session().get(SID) != null) {
    		return redirect("/");
    	}
    	
    	Map<String, String[]> fromData = request().body().asFormUrlEncoded();
    	String email = fromData.get("email")[0];
    	String password = fromData.get("password")[0];
    	
    	UserAccount acc = UserAccount.getDO().getByUname(email);
    	
    	if(acc == null || !acc.getPasswd().equals(password)) {
    		return unauthorized();
    	} else {
    		createSession(acc);
    		return redirect("/");
    	}
    }
    
    public Result signup() {
    	Map<String, String[]> fromData = request().body().asFormUrlEncoded();
    	String fname = fromData.get("fname")[0];
    	String lname = fromData.get("lname")[0];
    	String email = fromData.get("email")[0];
    	String password = fromData.get("password")[0];
    	
    	UserAccount acc = UserAccount.create(email, fname, lname, password);
    	createSession(acc);
    	return redirect("/");
    }
    
    public Result logout() {
		String sid = session().get(SID);
		session().clear();
		SESSION_MAP.remove(sid);
    	return redirect(routes.Application.index());
    }
}
