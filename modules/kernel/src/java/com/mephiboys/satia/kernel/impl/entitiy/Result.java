package com.mephiboys.satia.kernel.impl.entitiy;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "results")
@IdClass(ResultPK.class)
public class Result {
    private Date startTime;
    private String sessionKey;
    private Double value;
    private Test test;
    private User user;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Result result = (Result) o;

        if (startTime != null ? !startTime.equals(result.startTime) : result.startTime != null) return false;
        if (sessionKey != null ? !sessionKey.equals(result.sessionKey) : result.sessionKey != null) return false;
        if (value != null ? !value.equals(result.value) : result.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
        result = 31 * result + (sessionKey != null ? sessionKey.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

}
