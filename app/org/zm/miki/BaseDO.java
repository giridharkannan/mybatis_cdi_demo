package org.zm.miki;

import javax.inject.Inject;

public abstract class BaseDO<M> {
	
	private final Class<M> mapper;
	private final String tableName;
	
	@Inject
	private MapperFactory mf;
	
	public BaseDO(String tableName, Class<M> mapper) {
		this.mapper = mapper;
		this.tableName = tableName;
	}
	
	protected String getTableName() {
		return ContextManager.getCtxTableName(tableName);
	}
	
	protected M getMapper() {
		return mf.getMapper(mapper);
	}

}
