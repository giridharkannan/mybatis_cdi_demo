package org.zm.dao;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.zm.mapper.UserAccountMapper;
import org.zm.miki.BaseDO;
import org.zm.miki.UserUtil;
import org.zm.miki.annotation.Context;
import org.zm.miki.annotation.ContextType;
import org.zm.miki.annotation.Transaction;
import org.zm.model.UserAccount;

@ApplicationScoped
@Context(ContextType.FRAMEWORK)
public class UserAccountDO extends BaseDO<UserAccountMapper> {
	
	@Inject
	private DBShardDO shardDO;
	
	@Inject
	private UserUtil uutil;
	
	public UserAccountDO() {
		super("UserAccount", UserAccountMapper.class);
	}
	
	public UserAccount get(long id) {
		return getMapper().get(id);
	}
	
	public UserAccount getByUname(String uname) {
		return getMapper().getByUname(uname);
	}
	
	@Transaction
	public UserAccount create(UserAccount.Builder builder) {
		long shard = shardDO.incAndGetFree();
		UserAccount acc = builder.build(shard);
		getMapper().create(acc);
		uutil.createTables(acc);
		return acc;
	}
	
	public void changePasswd(UserAccount acc, String oldPwd, String newPwd) {
		if(acc.getPasswd().equals(oldPwd)) {
			throw new RuntimeException("Passwd mismatch");
		}
		
		getMapper().changePasswd(acc.getId(), newPwd);
	}
	
	public void delete(long id) {
		getMapper().delete(id);
	}

}
