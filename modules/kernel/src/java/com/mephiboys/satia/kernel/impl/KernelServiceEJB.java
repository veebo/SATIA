package com.mephiboys.satia.kernel.impl;


import com.mephiboys.satia.kernel.api.KernelService;
import com.mephiboys.satia.kernel.impl.entitiy.*;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

@Singleton
@TransactionManagement(TransactionManagementType.CONTAINER)
public class KernelServiceEJB implements KernelService {

    private static Logger log = org.apache.log4j.Logger.getLogger(KernelServiceEJB.class);

    @PersistenceContext (unitName = "PostgresPU")
    private EntityManager entityManager;

    @Resource(lookup="java:jboss/datasources/PostgresDSource")
    private DataSource dataSource;

    private JdbcTemplate jdbc;

    @PostConstruct
    protected void init(){
        jdbc = new JdbcTemplate(dataSource);
    }

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
        if (cls == null || ids == null || ids.isEmpty()){
            return Collections.EMPTY_LIST;
        }

        String sqlQuery;

        if (Test.class.equals(cls)){
            sqlQuery = "select e from Test e" + (ids.isEmpty() ? "" : " where e.testId IN :keys");
        } else if (Task.class.equals(cls)){
            sqlQuery = "select e from Task e" + (ids.isEmpty() ? "" : " where e.taskId IN :keys");
        } else if (Field.class.equals(cls)){
            sqlQuery = "select e from Field e" + (ids.isEmpty() ? "" : " where e.fieldId IN :keys");
        } else if (FieldValue.class.equals(cls)){
            sqlQuery = "select e from FieldValue e" + (ids.isEmpty() ? "" : " where e.fieldValueId IN :keys");
        } else if (Translation.class.equals(cls)){
            sqlQuery = "select e from Translation e" + (ids.isEmpty() ? "" : " where e.translationId IN :keys");
        } else if (Generator.class.equals(cls)){
            sqlQuery = "select e from Generator e" + (ids.isEmpty() ? "" : " where e.genId IN :keys");
        }else if (Phrase.class.equals(cls)){
            sqlQuery = "select e from Phrase e" + (ids.isEmpty() ? "" : " where e.phraseId IN :keys");
        } else if (Role.class.equals(cls)){
            sqlQuery = "select e from Role e" + (ids.isEmpty() ? "" : " where e.roleId IN :keys");
        }  else if (Lang.class.equals(cls)){
            sqlQuery = "select e from Lang e" + (ids.isEmpty() ? "" : " where e.lang IN :keys");
        } else if (User.class.equals(cls)){
            sqlQuery = "select e from User e" + (ids.isEmpty() ? "" : " where e.username IN :keys");
        }else if (Result.class.equals(cls)){
            sqlQuery = "select e from Result e" + (ids.isEmpty() ? "" : " where e.id IN :keys");
        }   else {
            throw new IllegalArgumentException("Class '"+cls+"' is not an entity");
        }

        Query q = entityManager.createQuery(sqlQuery, cls);
        if (!ids.isEmpty())
            q.setParameter("keys", new ArrayList(ids));
        List<T> result = q.getResultList();
        return result;
    }

    @Override
    public <T> T getEntityByQuery(Class<T> cls, String query, Object... params) {
        Collection<T> result = getEntitiesByQuery(cls, query, params);
        return result.isEmpty() ? null : result.iterator().next();
    }

    @Override
    public <T> Collection<T> getEntitiesByQuery(Class<T> cls, String query, Object... params) {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        Class pkClass = null;
        RowMapper rowMapper = null;
        if (Test.class.equals(cls)){
            pkClass = Long.class;
            rowMapper = (rs, rowNum) -> { return rs.getLong("test_id"); };
        } else if (Task.class.equals(cls)){
            pkClass = Long.class;
            rowMapper = (rs, rowNum) -> { return rs.getLong("task_id"); };
        } else if (Field.class.equals(cls)){
            pkClass = Long.class;
            rowMapper = (rs, rowNum) -> { return rs.getLong("field_id"); };
        } else if (FieldValue.class.equals(cls)){
            pkClass = Long.class;
            rowMapper = (rs, rowNum) -> { return rs.getLong("field_value_id"); };
        }  else if (Translation.class.equals(cls)){
            pkClass = Long.class;
            rowMapper = (rs, rowNum) -> { return rs.getLong("translation_id"); };
        } else if (Generator.class.equals(cls)){
            pkClass = Long.class;
            rowMapper = (rs, rowNum) -> { return rs.getLong("gen_id");
            };
        } else if (Phrase.class.equals(cls)){
            pkClass = Long.class;
            rowMapper = (rs, rowNum) -> { return rs.getLong("phrase_id"); };
        } else if (Role.class.equals(cls)){
            pkClass = Long.class;
            rowMapper = (rs, rowNum) -> { return rs.getLong("role_id"); };
        }  else if (Lang.class.equals(cls)){
            pkClass = String.class;
            rowMapper = (rs, rowNum) -> { return rs.getString("lang"); };
        } else if (User.class.equals(cls)){
            pkClass = String.class;
            rowMapper = (rs, rowNum) -> { return rs.getString("username"); };
        }else if (Result.class.equals(cls)){
            rowMapper = (rs, rowNum) -> {
                ResultPK pk = new ResultPK();
                pk.setSessionKey(rs.getString("session_key"));
                pk.setStartTime(rs.getTimestamp("start_time"));
                pk.setTestId(rs.getLong("test_id"));
                return pk;
            };
        } else {
            throw new IllegalArgumentException("Class '"+cls+"' is not an entity");
        }

        List keys = rowMapper == null
                ? jdbc.queryForList(query, pkClass, params)
                : jdbc.query(query, rowMapper, params);
        return getEntitiesByIds(cls, keys);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void mergeEntity(Object entity) {
        try {
            entityManager.persist(entity);
        } catch (Exception e){
            entityManager.merge(entity);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void mergeEntities(Collection entities) {
        for (Object o : entities){
            try {
                entityManager.persist(o);
            } catch (Exception e){
                entityManager.merge(o);
            }
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void saveEntity(Object entity) {
        entityManager.persist(entity);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void saveEntities(Collection entities) {
        for (Object e : entities){
            entityManager.persist(e);
        }
    }


    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public <T> void deleteEntityById(Class<T> cls, Object id) {
        Object entity = entityManager.find(cls, id);
        if (entity == null){
           throw new RuntimeException("Object not found of class '" + cls + "' with id=" + id);
        }
        entityManager.remove(entity);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public <T> void deleteEntitiesByIds(Class<T> cls, Collection ids) {
        Collection entities = getEntitiesByIds(cls, ids);
        for (Object e : entities){
            entityManager.remove(e);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public <T> void deleteEntitiesByQuery(Class<T> cls, String query, Object... params) {
        Collection entities = getEntitiesByQuery(cls, query, params);
        for (Object e : entities){
            entityManager.remove(e);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public <T> T doInTransaction(Callable<T> action) throws Exception {
        return action.call();
    }

}
