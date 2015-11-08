package com.mephiboys.satia.kernel.impl.entitiy;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.sql.Date;

public class ResultPK implements Serializable {
    private String username;

    @Column(name = "username")
    @Id
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private long testId;

    @Column(name = "test_id")
    @Id
    public long getTestId() {
        return testId;
    }

    public void setTestId(long testId) {
        this.testId = testId;
    }

    private Date startTime;

    @Column(name = "start_time")
    @Id
    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    private String sessionKey;

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

        if (testId != resultPK.testId) return false;
        if (username != null ? !username.equals(resultPK.username) : resultPK.username != null) return false;
        if (startTime != null ? !startTime.equals(resultPK.startTime) : resultPK.startTime != null) return false;
        if (sessionKey != null ? !sessionKey.equals(resultPK.sessionKey) : resultPK.sessionKey != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = username != null ? username.hashCode() : 0;
        result = 31 * result + (int) (testId ^ (testId >>> 32));
        result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
        result = 31 * result + (sessionKey != null ? sessionKey.hashCode() : 0);
        return result;
    }
}
