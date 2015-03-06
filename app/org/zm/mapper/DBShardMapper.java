package org.zm.mapper;

import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.zm.dao.DBShardDO.ShardKey;
import org.zm.model.DBShard;

public interface DBShardMapper {
	
	@Select("SELECT * FROM DBShard WHERE id = #{id}")
	DBShard get(long id);
	
	@Update("UPDATE DBShard SET weight=weight+1 WHERE id = "
			+ "(SELECT id FROM DBShard GROUP BY id HAVING weight=MIN(weight)"
            + " ORDER BY weight LIMIT 1)")
	@Options(useGeneratedKeys = true, keyProperty = "id")
	void incAndGetFree(ShardKey key);
	
	@Select("SELECT * FROM DBShard WHERE id=(SELECT shard FROM UserAccount WHERE id=${id})")
	DBShard getForUser(long user);

}
