package com.kl.example;

import com.kl.core.thread.KlThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = KlExamplesWebApplication.class)
@Slf4j
public class MonogdbTest {

    @Autowired
    private MongoTemplate mongoOps;

    @Test
    public void insertTest() {
        KlThreadLocal.setTenantId(10000);
        Person p = new Person("Joe", 34);

        // 插入文档
        mongoOps.insert(p);
        log.info("Insert: " + p);

        // 查询文档
        p = mongoOps.findById(p.getId(), Person.class);
        log.info("Found: " + p);

        Query query=new Query(Criteria.where("name").is("Joe"));
        Update update= new Update().set("age", 35);
        // 更新文档
        mongoOps.updateFirst(query, update, Person.class);
        p = mongoOps.findOne(query, Person.class);
        log.info("Updated: " + p);

        // 删除文档
        mongoOps.remove(p);

        // Check that deletion worked
        List<Person> people = mongoOps.findAll(Person.class);
        log.info("Number of people = : " + people.size());

        mongoOps.dropCollection(Person.class);
    }
}
