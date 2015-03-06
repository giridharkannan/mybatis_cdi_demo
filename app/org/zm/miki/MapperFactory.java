package org.zm.miki;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.zm.dao.DBShardDO;
import org.zm.model.DBShard;

import controllers.SecurityApp;
import controllers.SecurityApp.SessionUser;
import play.api.Mode;
import scala.Enumeration.Value;

@ApplicationScoped
public class MapperFactory {
	
	@Inject
	private DBShardDO shardDO;
	private final SqlSessionFactory factory;
	private final ConcurrentHashMap<Long, SqlSessionFactory> fa_map;

	public MapperFactory() throws IOException {
		factory = new SqlSessionFactoryBuilder().build(Resources
				.getResourceAsStream("mybatis-config.xml"), getEnv());
		fa_map = new ConcurrentHashMap<Long, SqlSessionFactory>();
	}
	
	private String getEnv() {
		Value v = play.api.Play.current().mode();
		if(Mode.Dev() == v) {
			//default
			return "dev";
		} else if(Mode.Prod() == v) {
			return "prod";
		} else if(Mode.Test() == v) {
			return "test";
		} else {
			throw new RuntimeException("Unknown mode "+v);
		}
	}
	
	<T> T getMapper(Class<T> mapper) {
		return ContextManager.getSqlSession(this).getMapper(mapper);
	}
	
	SqlSession getFrameworkSession(boolean autoCommit) {
		return factory.openSession(autoCommit);
	}
	
	SqlSession getFrameworkSession() {
		return getFrameworkSession(true);
	}
	
	SqlSession getUserSession() {
		return getUserSession(true);
	}
	
	SqlSession getUserSession(boolean autoCommit) {
		SessionUser user = SecurityApp.getCurrentUser();
		if(user == null) {
			throw new RuntimeException("User Context not set");
		}
		
		return getUserSession(user.shard, autoCommit);
	}
	
	SqlSession getUserSession(long shard, boolean autoCommit) {
		DBShard shardObj = shardDO.get(shard);
		SqlSessionFactory fa = fa_map.get(shard);

		if (fa == null) { // factory not yet associated
			try {
				fa = createSSFactory(shardObj);
			} catch (IOException e) {
				throw new RuntimeException(
						"Exception while getting connection", e);
			}
		}
		return fa.openSession(autoCommit);
	}
	
	private synchronized SqlSessionFactory createSSFactory(DBShard shard) throws IOException {
		SqlSessionFactory fa = null;
		fa = fa_map.get(shard.getId());
		
		if (fa != null) {
			return fa;
		}

		InputStream in = getConfig(shard);
		fa = new SqlSessionFactoryBuilder().build(in);
		fa_map.put(shard.getId(), fa);
		return fa;
	}
	
	private InputStream getConfig(DBShard shard) throws IOException {
		InputStream in = Resources
				.getResourceAsStream("mybatis-user-config.xml");
		
		String conf = IOUtils.toString(in);
		conf = MessageFormat.format(conf, shard.getDriver(), shard.getUrl(), shard.getUsr(), shard.getPasswd());
		return IOUtils.toInputStream(conf);
	}
}
