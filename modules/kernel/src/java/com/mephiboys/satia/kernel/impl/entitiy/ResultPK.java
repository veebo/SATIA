package com.mephiboys.satia.kernel.impl.entitiy;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Embeddable
public class ResultPK implements Serializable {

    private long testId;

    @Basic
    @Column(name = "start_time")
    private Timestamp startTime;

    @Basic
    @Column(name = "session_key")
    private String sessionKey;

    @Basic
    @Column(name = "test_id")
    public long getTestId() {
        return testId;
    }

    public void setTestId(long testId) {
        this.testId = testId;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

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
        if (startTime != null ? !startTime.equals(resultPK.startTime) : resultPK.startTime != null) return false;
        if (sessionKey != null ? !sessionKey.equals(resultPK.sessionKey) : resultPK.sessionKey != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (testId ^ (testId >>> 32));
        result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
        result = 31 * result + (sessionKey != null ? sessionKey.hashCode() : 0);
        return result;
    }
}
