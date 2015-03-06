package org.zm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.zm.model.Beam;

public interface BeamMapper {

	@Select("SELECT * FROM ${table} WHERE id = #{id}")
	Beam get(@Param("table") String table, @Param("id") long id);
	
	@Select("SELECT * FROM ${table} WHERE id>=#{start} ORDER BY id LIMIT #{limit}")
	List<Beam> getRange(
			@Param("table") String table,
			@Param("start") Long start,
			@Param("limit") Integer limit);
	
	@Insert("INSERT INTO ${table} (ctime, content) VALUES (#{beam.ctime}, #{beam.content})")
	@Options(useGeneratedKeys = true, keyProperty = "beam.id")
	void create(@Param("table") String table, @Param("beam") Beam beam);
		
	@Delete("DELETE FROM ${table} WHERE id=#{id}")
	void delete(@Param("table") String table, @Param("id") long id);
}
