package hr.abysalto.hiring.mid.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
public class BaseModel {

    @Id
    protected Long id;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;
}
