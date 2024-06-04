package com.fiafeng.mybatis.dao;


import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UtilsDao {

    @Insert({
            "<script>",
            "CREATE PROCEDURE your_procedure_name() ",
            "BEGIN ",
            "   -- 这里是存储过程的SQL逻辑 ",
            "   SELECT 'Hello from stored procedure' AS message; ",
            "END;",
            "</script>"
    })
    public void createQueryChildrenId(String tableName);
}
