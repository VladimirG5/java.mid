package hr.abysalto.hiring.mid.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
public class BaseModel {

    @Id
    protected Long id;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;
}
