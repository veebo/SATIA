package com.mephiboys.satia.kernel.impl.entitiy;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "tests")
public class Test {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "test_id")
    private Long testId;

    @Basic
    @Column(name = "title")
    private String title;

    @Basic
    @Column(name = "description")
    private String description;

    @Basic
    @Column(name = "created_when")
    private Timestamp createdWhen;

    @ManyToOne
    @JoinColumn(name = "username", referencedColumnName = "username", nullable = false)
    private User user;

    @ManyToOne(cascade = {CascadeType.REFRESH})
    @JoinColumn(name = "gen_id", referencedColumnName = "gen_id", nullable = false)
    private Generator generator;

    @ManyToMany(cascade = {CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(
            name="test_tasks",
            joinColumns={@JoinColumn(name="test_id", referencedColumnName="test_id")},
            inverseJoinColumns={@JoinColumn(name="task_id", referencedColumnName="task_id")}
    )
    private List<Task> tasks;

    @ManyToOne
    @JoinColumn(name = "source_lang", referencedColumnName = "lang", nullable = false)
    private Lang sourceLang;

    @ManyToOne
    @JoinColumn(name = "target_lang", referencedColumnName = "lang", nullable = false)
    private Lang targetLang;

    public Long getTestId() {
        return testId;
    }

    public void setTestId(Long testId) {
        this.testId = testId;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public Timestamp getCreatedWhen() {
        return createdWhen;
    }

    public void setCreatedWhen(Timestamp createdWhen) {
        this.createdWhen = createdWhen;
    }


    public Lang getSourceLang() {
        return sourceLang;
    }

    public void setSourceLang(Lang sourceLang) {
        this.sourceLang = sourceLang;
    }

    public Lang getTargetLang() {
        return targetLang;
    }

    public void setTargetLang(Lang targetLang) {
        this.targetLang = targetLang;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public Generator getGenerator() {
        return generator;
    }

    public void setGenerator(Generator generator) {
        this.generator = generator;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Test test = (Test) o;

        if (testId != test.testId) return false;
        if (title != null ? !title.equals(test.title) : test.title != null) return false;
        if (description != null ? !description.equals(test.description) : test.description != null) return false;
        if (createdWhen != null ? !createdWhen.equals(test.createdWhen) : test.createdWhen != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (testId ^ (testId >>> 32));
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (createdWhen != null ? createdWhen.hashCode() : 0);
        return result;
    }

}
