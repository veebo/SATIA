package com.mephiboys.satia.kernel.api;

import javax.ejb.Local;
import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.Collection;
import java.util.concurrent.Callable;


@Local
public interface KernelService {

    DataSource getDataSource();

    EntityManager getEntityManager();

    <T> T getEntityById(Class<T> cls, Object id);

    <T> Collection<T> getEntitiesByIds(Class<T> cls, Collection ids);

    <T> T getEntityByQuery(Class<T> cls, String query, Object... params);

    <T> Collection<T> getEntitiesByQuery(Class<T> cls, String query, Object... params);

    void saveEntityIfNotExists(Object entity);

    void saveEntitiesIfNotExist(Collection entities);

    void saveEntity(Object entity);

    void saveEntities(Collection entities);

    <T> void deleteEntityById(Class<T> cls, Object id);

    <T> void deleteEntitiesByIds(Class<T> cls, Collection ids);

    <T> void deleteEntityByQuery(Class<T> cls, String query, Object... params);

    <T> void deleteEntitiesByQuery(Class<T> cls, String query, Object... params);

    <T> T doInTransaction(Callable<T> action) throws Exception;

}
