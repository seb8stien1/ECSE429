package response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Category {
    String id;
    String title;
    String description;
    List<Map<String, String>> todos;
    List<Map<String, String>> projects;
}
