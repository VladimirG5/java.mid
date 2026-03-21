package hr.abysalto.hiring.mid.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

@Table("USERS")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseModel{

    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
}
