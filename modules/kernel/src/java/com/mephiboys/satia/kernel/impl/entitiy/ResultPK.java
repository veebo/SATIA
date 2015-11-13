package com.mephiboys.satia.kernel.impl.entitiy;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;

@Embeddable
public class ResultPK implements Serializable {
    private Test test;
    private User user;
    private Date startTime;
    private String sessionKey;

    @ManyToOne
    @JoinColumn(name = "test_id", referencedColumnName = "test_id", nullable = false)
    public Test getTest() {
        return test;
    }

    public void setTest(Test testsByTestId) {
        this.test = testsByTestId;
    }


    @ManyToOne
    @JoinColumn(name = "username", referencedColumnName = "username")
    public User getUser() {
        return user;
    }

    public void setUser(User usersByUsername) {
        this.user = usersByUsername;
    }


    @Column(name = "start_time")
    @Id
    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    @Column(name = "session_key")
    @Id
    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResultPK resultPK = (ResultPK) o;

        if (test.getTestId() != resultPK.test.getTestId()) return false;
        if (user.getUsername() != resultPK.user.getUsername()) return false;
        if (startTime != null ? !startTime.equals(resultPK.startTime) : resultPK.startTime != null) return false;
        if (sessionKey != null ? !sessionKey.equals(resultPK.sessionKey) : resultPK.sessionKey != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = user.getUsername() != null ? user.getUsername().hashCode() : 0;
        result = 31 * result + (int) (test.getTestId() ^ (test.getTestId() >>> 32));
        result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
        result = 31 * result + (sessionKey != null ? sessionKey.hashCode() : 0);
        return result;
    }
}
