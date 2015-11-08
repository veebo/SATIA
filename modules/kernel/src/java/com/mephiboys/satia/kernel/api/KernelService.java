package com.mephiboys.satia.kernel.api;

import com.mephiboys.satia.kernel.impl.entitiy.Generator;
import com.mephiboys.satia.kernel.impl.entitiy.Phrase;
import com.mephiboys.satia.kernel.impl.entitiy.Task;
import com.mephiboys.satia.kernel.impl.entitiy.Test;

import javax.ejb.Local;
import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.Collection;


@Local
public interface KernelService {

    DataSource getDataSource();

    EntityManager getEntityManager();

    Test getTestById(long id);

    Collection<Test> getTestsById(long id);

    Task getTaskById(long id);

    Collection<Task> getTasksById(long id);

    Object getByQuery(String query);

    Phrase getPhraseById(long id);

    Collection<Phrase> getPhrasesById(long id);

    Generator getGeneratorById(long id);

    Collection<Generator> getGeneratorsById(long id);

}
