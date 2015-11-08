package com.mephiboys.satia.kernel.impl;


import com.mephiboys.satia.kernel.api.KernelService;
import com.mephiboys.satia.kernel.impl.entitiy.Generator;
import com.mephiboys.satia.kernel.impl.entitiy.Phrase;
import com.mephiboys.satia.kernel.impl.entitiy.Task;
import com.mephiboys.satia.kernel.impl.entitiy.Test;
import org.apache.log4j.Logger;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.util.Collection;

@Stateless
public class KernelServiceEJB implements KernelService {

    private static Logger log = org.apache.log4j.Logger.getLogger(KernelServiceEJB.class);

    @PersistenceContext (unitName = "PostgresPU")
    private EntityManager entityManager;

    @Resource(lookup="java:jboss/datasources/PostgresDSource")
    private DataSource dataSource;

    public DataSource getDataSource(){
        return dataSource;
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public Test getTestById(long id) {
        return null;
    }

    @Override
    public Collection<Test> getTestsById(long id) {
        return null;
    }

    @Override
    public Task getTaskById(long id) {
        return null;
    }

    @Override
    public Collection<Task> getTasksById(long id) {
        return null;
    }

    @Override
    public Object getByQuery(String query) {
        return null;
    }

    @Override
    public Phrase getPhraseById(long id) {
        return null;
    }

    @Override
    public Collection<Phrase> getPhrasesById(long id) {
        return null;
    }

    @Override
    public Generator getGeneratorById(long id) {
        return null;
    }

    @Override
    public Collection<Generator> getGeneratorsById(long id) {
        return null;
    }

}
