package it.uniroma3.siw.taskmanager.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;


@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;
    
    @Column
    private String description;
    
    @Column(nullable = false)
    private boolean completed;

    @Column(updatable = false, nullable = false)
    private LocalDateTime creationTimestamp;

   
    @Column(nullable = false)
    private LocalDateTime lastUpdateTimestamp;

    public Task() {}

    public Task(String name,
                String description,
                boolean completed) {
        this.name = name;
        this.description = description;
        this.completed = completed;
    }

    @PrePersist
    protected void onPersist() {
        this.creationTimestamp = LocalDateTime.now();
        this.lastUpdateTimestamp = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.lastUpdateTimestamp = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public LocalDateTime getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(LocalDateTime creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public LocalDateTime getLastUpdateTimestamp() {
        return lastUpdateTimestamp;
    }

    public void setLastUpdateTimestamp(LocalDateTime lastUpdateTimestamp) {
        this.lastUpdateTimestamp = lastUpdateTimestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return completed == task.completed &&
               Objects.equals(name, task.name) &&
                Objects.equals(creationTimestamp, task.creationTimestamp) &&
                Objects.equals(lastUpdateTimestamp, task.lastUpdateTimestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, completed, creationTimestamp, lastUpdateTimestamp);
    }
}
