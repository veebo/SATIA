package java;

import javax.ejb.Stateless;

@Stateless
public class EJB implements EJBRemote {
    @Override
    public String getMessage() {
        return "Message from real bean";
    }
}
