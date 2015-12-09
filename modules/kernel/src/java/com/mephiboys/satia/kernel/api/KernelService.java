package com.mephiboys.satia.kernel.api;

import com.mephiboys.satia.kernel.impl.entitiy.Generator;
import com.mephiboys.satia.kernel.impl.entitiy.Result;
import com.mephiboys.satia.kernel.impl.entitiy.Task;
import com.mephiboys.satia.kernel.impl.entitiy.Test;

import javax.ejb.Local;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Callable;


@Local
public interface KernelService {

    DataSource getDataSource();

    EntityManager getEntityManager();

    <T> T getEntityById(Class<T> cls, Object id);

    <T> Collection<T> getEntitiesByIds(Class<T> cls, Collection ids);

    <T> T getEntityByQuery(Class<T> cls, String query, Object... params);

    <T> Collection<T> getEntitiesByQuery(Class<T> cls, String query, Object... params);

    void updateEntity(Object entity);

    void updateEntities(Collection entities);

    void saveEntity(Object entity);

    void saveEntities(Collection entities);

    <T> void deleteEntityById(Class<T> cls, Object id);

    <T> void deleteEntitiesByIds(Class<T> cls, Collection ids);

    <T> void deleteEntitiesByQuery(Class<T> cls, String query, Object... params);

    <T> T doInTransaction(Callable<T> action) throws Exception;

    void updateTest(Test test, boolean createTest, Map<String, String> testReqParams) throws IllegalArgumentException;

    Task newTask(String[] values, Generator gen, Test test)  throws IllegalArgumentException;

    void removeTask(Task task, Test test);

    void updateTask(Test test, Task task, String[] values, Long genId) throws IllegalArgumentException;

    void updateTaskFieldValues(Task task, HttpServletRequest request, String paramPrefix) throws IllegalArgumentException;

    Result saveResult(String username, Test test, String sessionId, int rightAnswers);

}
