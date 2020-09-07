package com.bitwise;

import com.bitwise.model.Person;
import com.bitwise.orm.EntityManager;
import com.bitwise.util.MetaModel;
import lombok.SneakyThrows;

import java.sql.SQLException;

public class Application {


    public static void main(String[] args) throws Exception {

        EntityManager<Person> entityManager = EntityManager.of(Person.class);

        //WriteObjects(entityManager);

        ReadObjects(entityManager);

    }

    private static void WriteObjects(EntityManager<Person> entityManager) throws Exception {
        var alex = new Person("Alex", 42);
        var aris = new Person("Aris", 4);
        var ayan = new Person("Ayan", 1);
        var maria = new Person("Maria", 42);

        System.out.println(alex);
        System.out.println(aris);
        System.out.println(ayan);
        System.out.println(maria);

        entityManager.persist(alex);
        entityManager.persist(aris);
        entityManager.persist(ayan);
        entityManager.persist(maria);

        System.out.println(alex);
        System.out.println(aris);
        System.out.println(ayan);
        System.out.println(maria);
    }

    @SneakyThrows
    private static void ReadObjects(EntityManager<Person> entityManager) {

        Person alex = entityManager.find(Person.class, 1L);
        Person aris = entityManager.find(Person.class, 2L);
        Person ayan = entityManager.find(Person.class, 3L);
        Person maria = entityManager.find(Person.class, 4L);

        System.out.println(alex);
        System.out.println(aris);
        System.out.println(ayan);
        System.out.println(maria);

    }
}

