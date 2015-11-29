package com.mephiboys.satia.kernel.impl.entitiy;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "results")
public class Result {

    @EmbeddedId
    private ResultPK id;

    private Double value;

    private String fullname;

    @ManyToOne
    @JoinColumn(name = "username", referencedColumnName = "username")
    private User user;

    @ManyToOne
    @MapsId("testId")
    @JoinColumn(name = "test_id", referencedColumnName = "test_id", nullable = false)
    private Test test;

    {
        id = new ResultPK();
    }

    public ResultPK getId() {
        return id;
    }

    public void setId(ResultPK id) {
        this.id = id;
    }

    public Timestamp getStartTime() {
        return id.getStartTime();
    }

    public void setStartTime(Timestamp startTime) {
        id.setStartTime(startTime);
    }

    public String getSessionKey() {
        return id.getSessionKey();
    }

    public void setSessionKey(String sessionKey) {
        id.setSessionKey(sessionKey);
    }

    @Basic
    @Column(name = "value")
    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Test getTest() {
        return test;
    }

    public void setTest(Test testsByTestId) {
        this.test = testsByTestId;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Result result = (Result) o;

        if (id != null ? !id.equals(result.id) : result.id != null) return false;
        if (value != null ? !value.equals(result.value) : result.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Basic
    @Column(name = "fullname")
    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
}
