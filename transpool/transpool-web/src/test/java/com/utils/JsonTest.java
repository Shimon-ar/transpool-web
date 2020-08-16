package com.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class JsonTest {

    @Test
    public void testSerialize() throws JsonProcessingException {

        Person[] persons = new Person[]{
                new Person("Shimon", "Arshavsky", 25),
                new Person("Sofi", "Arshavsky", 30)
        };

        List<Person> people = new ArrayList<>();
        people.add(new Person("Shimon", "Arshavsky", 25));


        ObjectMapper mapper = new ObjectMapper();

        String json = mapper.writeValueAsString(people);
        System.out.println(json);

        //Person[] parsedPersons = mapper.readValue(json, Person[].class);

        //Assert.assertEquals(2, parsedPersons.length);


    }

}
