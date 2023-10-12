package response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Project {
    String id;
    String title;
    String description;
    Boolean completed;
    Boolean active;
    List<Map<String, String>> tasks;
    List<Map<String, String>> categories;
}
