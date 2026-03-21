package hr.abysalto.hiring.mid.model;

import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

public class BaseModel {

    @Id
    protected Long id;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;
}
