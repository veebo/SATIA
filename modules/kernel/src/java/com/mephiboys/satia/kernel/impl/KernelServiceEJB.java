package com.mephiboys.satia.kernel.impl;


import com.mephiboys.satia.kernel.api.KernelService;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.sql.DataSource;

@Stateless
public class KernelServiceEJB implements KernelService {

    @Resource(lookup="java:jboss/datasources/PostgresDSource")
    private DataSource dataSource;

    public DataSource getDataSource(){
        return dataSource;
    }

}
