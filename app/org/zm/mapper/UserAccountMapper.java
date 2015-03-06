package org.zm.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.zm.model.UserAccount;

public interface UserAccountMapper {
	
	@Select("SELECT * FROM UserAccount WHERE id = #{id}")
	UserAccount get(long id);
	
	@Select("SELECT * FROM UserAccount WHERE uname = #{uname}")
	UserAccount getByUname(String uname);
	
	@Insert("INSERT INTO UserAccount (uname, fname, lname, passwd, shard)"
            + " VALUES (#{uname}, #{fname}, #{lname}, #{passwd}, #{shard})")
	@Options(useGeneratedKeys = true, keyProperty = "id")
	void create(UserAccount acc);
	
	@Update("UPDATE UserAccount SET passwd=#{passwd} WHERE id=#{id}")
	void changePasswd(@Param("id") long id, @Param("passwd") String newPasswd);
	
	@Delete("DELETE FROM UserAccount WHERE id=#{id}")
	void delete(long id);

}
