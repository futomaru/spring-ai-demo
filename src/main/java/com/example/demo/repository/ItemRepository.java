package com.example.demo.repository;

import com.example.demo.model.Item;
import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ItemRepository {

    String BASE_SELECT = "SELECT id, name, price, description FROM items";

    @Select(BASE_SELECT + " ORDER BY id")
    List<Item> findAll();

    @Select(BASE_SELECT + " WHERE name LIKE CONCAT('%', #{name}, '%') ORDER BY id")
    List<Item> findByName(String name);

    @Select(BASE_SELECT + " WHERE id = #{id}")
    Item findById(Long id);

    @Delete("DELETE FROM items WHERE id = #{id}")
    void deleteById(Long id);

    @Insert("INSERT INTO items(name, price, description) VALUES(#{name}, #{price}, #{description})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    Item save(Item item);

    @Update("UPDATE items SET name = #{name}, price = #{price}, description = #{description} WHERE id = #{id}")
    int update(Item item);
}
