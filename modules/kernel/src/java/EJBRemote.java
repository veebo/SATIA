package java;

import javax.ejb.Remote;

@Remote
public interface EJBRemote {

    String getMessage();

}
