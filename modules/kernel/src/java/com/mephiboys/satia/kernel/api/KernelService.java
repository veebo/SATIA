package com.mephiboys.satia.kernel.api;

import javax.ejb.Local;
import javax.sql.DataSource;


@Local
public interface KernelService {

    DataSource getDataSource();

}
