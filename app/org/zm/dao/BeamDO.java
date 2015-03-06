package org.zm.dao;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.zm.mapper.BeamMapper;
import org.zm.miki.BaseDO;
import org.zm.miki.annotation.Context;
import org.zm.miki.annotation.ContextType;
import org.zm.model.Beam;

@ApplicationScoped
@Context(ContextType.APPLICATION)
public class BeamDO extends BaseDO<BeamMapper> {
	
	public BeamDO() {
		super("beam", BeamMapper.class);
	}
	
	public void create(Beam beam) {
		getMapper().create(getTableName(), beam);
	}
	
	public Beam get(long id) {
		return getMapper().get(getTableName(), id);
	}
	
	public List<Beam> getRange(long start, int limit) {
			return getMapper().getRange(getTableName(), start, limit);
	}
	
	public void delete(long id) {
		getMapper().delete(getTableName(), id);
	}

}
