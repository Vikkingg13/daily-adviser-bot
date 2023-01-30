package com.github.vikkingg13.service;

import com.github.vikkingg13.model.db.Person;

import java.util.List;

public interface PersonService {

    void save(Person person);

    List<Person> findAll();

    boolean deleteById(Long chatId);
}
