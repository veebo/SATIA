package com.mephiboys.satia.kernel.impl;


import com.mephiboys.satia.kernel.api.KernelService;
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
    public <T> T getEntityById(Class<T> cls, Object id) {
        return null;
    }

    @Override
    public <T> Collection<T> getEntitiesByIds(Class<T> cls, Collection ids) {
        return null;
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
