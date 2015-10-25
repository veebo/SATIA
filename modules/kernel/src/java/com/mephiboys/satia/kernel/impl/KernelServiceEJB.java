package com.mephiboys.satia.kernel.impl;


import com.mephiboys.satia.kernel.api.KernelService;

import javax.ejb.Stateless;
import javax.sql.DataSource;

@Stateless
public class KernelServiceEJB implements KernelService {

    public DataSource getDataSource(){
        return null;
    }
}
