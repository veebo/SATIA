package com.mephiboys.satia.kernel.impl.entitiy.test;

import javax.persistence.*;
import java.sql.Date;

/**
 * Created by vibo0315 on 05.11.2015.
 */
@Entity
@Table(name = "results", schema = "public", catalog = "quartz")
@IdClass(ResultPK.class)
public class Result {
    private String username;
    private long testId;
    private Date startTime;
    private String sessionKey;
    private Double value;
    private Test testsByTestId;
    private User usersByUsername;

    @Id
    @Column(name = "username")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Id
    @Column(name = "test_id")
    public long getTestId() {
        return testId;
    }

    public void setTestId(long testId) {
        this.testId = testId;
    }

    @Id
    @Column(name = "start_time")
    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    @Id
    @Column(name = "session_key")
    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    @Basic
    @Column(name = "value")
    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Result result = (Result) o;

        if (testId != result.testId) return false;
        if (username != null ? !username.equals(result.username) : result.username != null) return false;
        if (startTime != null ? !startTime.equals(result.startTime) : result.startTime != null) return false;
        if (sessionKey != null ? !sessionKey.equals(result.sessionKey) : result.sessionKey != null) return false;
        if (value != null ? !value.equals(result.value) : result.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = username != null ? username.hashCode() : 0;
        result = 31 * result + (int) (testId ^ (testId >>> 32));
        result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
        result = 31 * result + (sessionKey != null ? sessionKey.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @ManyToOne
    @JoinColumn(name = "test_id", referencedColumnName = "test_id", nullable = false)
    public Test getTestsByTestId() {
        return testsByTestId;
    }

    public void setTestsByTestId(Test testsByTestId) {
        this.testsByTestId = testsByTestId;
    }

    @ManyToOne
    @JoinColumn(name = "username", referencedColumnName = "username", nullable = false)
    public User getUsersByUsername() {
        return usersByUsername;
    }

    public void setUsersByUsername(User usersByUsername) {
        this.usersByUsername = usersByUsername;
    }
}
