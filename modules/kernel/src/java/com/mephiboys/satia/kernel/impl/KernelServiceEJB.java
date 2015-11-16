package com.mephiboys.satia.kernel.impl;


import com.mephiboys.satia.kernel.api.KernelService;
import com.mephiboys.satia.kernel.impl.entitiy.*;
import org.apache.log4j.Logger;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.sql.DataSource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
    public <T> T getEntityById(Class<T> cls, Object id) {
        return entityManager.find(cls, id);
    }

    @Override
    public <T> Collection<T> getEntitiesByIds(Class<T> cls, Collection ids) {

        if (cls == null || ids == null){
            return Collections.EMPTY_LIST;
        }

        String sqlQuery = null;
        if (Test.class.equals(cls.getClass())){
            sqlQuery = "select e.testId from Test e where e.testId IN :keys";
        } else if (Task.class.equals(cls.getClass())){
            sqlQuery = "select e.id from Result e where e.id IN :keys";
        } else if (Translation.class.equals(cls.getClass())){
            sqlQuery = "select e.translationId from Translation e where e.translationId IN :keys";;
        } else if (User.class.equals(cls.getClass())){
            sqlQuery = "select e.username from User e where e.username IN :keys";;
        } else if (Role.class.equals(cls.getClass())){
            sqlQuery = "select e.roleId from Role e where e.roleId IN :keys";;
        } else if (Result.class.equals(cls.getClass())){
            sqlQuery = "select e.id from Result e where e.id IN :keys";;
        } else if (Phrase.class.equals(cls.getClass())){
            sqlQuery = "select e.phraseId from Phrase e where e.phraseId IN :keys";;
        } else if (Lang.class.equals(cls.getClass())){
            sqlQuery = "select e.lang from Lang e where e.lang IN :keys";;
        } else if (Generator.class.equals(cls.getClass())){
            sqlQuery = "select e.genId from Generator e where e.genId IN :keys";;
        }

        else {
            throw new IllegalArgumentException("Class '"+cls+"' is not an entity");
        }

        Query q = entityManager.createQuery(sqlQuery, cls);
        q.setParameter("keys", ids);
        List results = q.getResultList();
        return results;
    }

    @Override
    public <T> T getEntityByQuery(Class<T> cls, String query, Object... params) {
        return null;
    }

    @Override
    public <T> Collection<T> getEntitiesByQuery(Class<T> cls, String query, Object... params) {
        return null;
    }

    @Override
    public void saveEntity(Object entity) {

    }

    @Override
    public void saveEntities(Collection entities) {

    }

    @Override
    public <T> void deleteEntityById(Class<T> cls, Object id) {

    }

    @Override
    public <T> void deleteEntitiesByIds(Class<T> cls, Collection ids) {

    }

    @Override
    public <T> void deleteEntityByQuery(Class<T> cls, String query, Object... params) {

    }

    @Override
    public <T> void deleteEntitiesByQuery(Class<T> cls, String query, Object... params) {

    }

}
