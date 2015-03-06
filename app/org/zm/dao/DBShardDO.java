package org.zm.dao;

import javax.enterprise.context.ApplicationScoped;

import org.zm.mapper.DBShardMapper;
import org.zm.miki.BaseDO;
import org.zm.miki.annotation.Context;
import org.zm.miki.annotation.ContextType;
import org.zm.model.DBShard;

@ApplicationScoped
@Context(ContextType.FRAMEWORK)
public class DBShardDO extends BaseDO<DBShardMapper> {
	
	public DBShardDO() {
		super("DBShard", DBShardMapper.class);
	}
	
	public static class ShardKey {
		private long id;
		private ShardKey() {}
	}
	
	public DBShard get(long id) {
		return getMapper().get(id);
	}
	
	public long incAndGetFree() {
		ShardKey key = new ShardKey();
		getMapper().incAndGetFree(key);
		return key.id;
	}
	
	public DBShard getForUser(long user) {
		return getMapper().getForUser(user);
	}

}
