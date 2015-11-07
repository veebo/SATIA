package com.mephiboys.satia.kernel.api;

import javax.ejb.Local;
import javax.persistence.EntityManager;
import javax.sql.DataSource;


@Local
public interface KernelService {

    DataSource getDataSource();

    EntityManager getEntityManager();

}
