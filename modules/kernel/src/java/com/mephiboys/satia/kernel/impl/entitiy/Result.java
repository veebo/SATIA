package com.mephiboys.satia.kernel.impl.entitiy;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "results")
public class Result {

    @EmbeddedId
    private ResultPK id;
    private Double value;

    public ResultPK getId() {
        return id;
    }

    public void setId(ResultPK id) {
        this.id = id;
    }

//    public Test getTest() {
//        return id.getTest();
//    }
//
//    public void setTest(Test test) {
//        this.id.setTest(test);
//    }
//
//    public User getUser() {
//        return this.id.getUser();
//    }
//
//    public void setUser(User user) {
//        this.id.setUser(user);
//    }
//
//    public Date getStartTime() {
//        return this.id.getStartTime();
//    }
//
//    public void setStartTime(Date startTime) {
//        this.id.setStartTime(startTime);
//    }
//
//    public String getSessionKey() {
//        return this.id.getSessionKey();
//    }
//
//    public void setSessionKey(String sessionKey) {
//        this.id.setSessionKey(sessionKey);
//    }

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

}
