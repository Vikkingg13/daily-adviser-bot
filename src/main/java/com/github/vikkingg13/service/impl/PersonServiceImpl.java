package com.github.vikkingg13.service.impl;

import com.github.vikkingg13.mapper.PersonMapper;
import com.github.vikkingg13.model.db.Person;
import com.github.vikkingg13.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonServiceImpl implements PersonService {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public void save(Person person) {
        var namedParameters = new BeanPropertySqlParameterSource(person);
        namedParameterJdbcTemplate.update("INSERT OR IGNORE INTO Person VALUES (:id, :username)",
                namedParameters);
    }

    @Override
    public List<Person> findAll() {
        return namedParameterJdbcTemplate.getJdbcTemplate().query("SELECT * FROM Person", new PersonMapper());
    }


    @Override
    public boolean deleteById(Long id) {
        var namedParameters = new MapSqlParameterSource()
                .addValue("id", id);
        int res = namedParameterJdbcTemplate.update(
                "DELETE FROM Person WHERE id=:id", namedParameters);
        return res == 1;

    }

    public static void main(String[] args) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.sqlite.JDBC");
        dataSource.setUrl("jdbc:sqlite:C://sqlite/tgBot.db");
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dataSource);
        var namedParameters = new MapSqlParameterSource()
                .addValue("id", 13)
                .addValue("username", "Viktor");
        template.update("INSERT OR IGNORE INTO Person VALUES (:id, :username)", namedParameters);
    }
}
