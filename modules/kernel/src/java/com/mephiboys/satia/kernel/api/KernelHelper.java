package com.mephiboys.satia.kernel.api;


import javax.naming.InitialContext;
import javax.naming.NamingException;

public class KernelHelper {

    public final static String KERNEL_SERVICE_JNDI = "java:app/satia-kernel/KernelServiceEJB";

    public static KernelService getKernelService(){
        try {
            InitialContext ic = new InitialContext();
            return (KernelService)ic.lookup(KERNEL_SERVICE_JNDI);
        } catch (NamingException e) {
            throw new RuntimeException("Can't get KernelService instance via JNDI: "+KERNEL_SERVICE_JNDI, e);
        }
    }
}
