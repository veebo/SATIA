package com.mephiboys.satia.kernel.mock

import com.mephiboys.satia.kernel.api.KernelService

import javax.persistence.EntityManager

import static org.mockito.Mockito.*;

import javax.sql.DataSource

class MockedKernelService implements KernelService {

    def tests = []

    @Override
    DataSource getDataSource() {
        return mock(DataSource.class)
    }

    @Override
    javax.persistence.EntityManager getEntityManager() {
        return mock(EntityManager.class)
    }

    {

    }

}
