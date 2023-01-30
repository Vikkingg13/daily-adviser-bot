package com.github.vikkingg13.mapper;

import com.github.vikkingg13.model.db.Person;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PersonMapper implements RowMapper<Person> {

    @Override
    public Person mapRow(ResultSet rs, int rowNum) throws SQLException {
        Person person = new Person();

        person.setId(rs.getLong("Id"));
        person.setUsername(rs.getString("username"));

        return person;
    }
}
