package response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Todo {
    String id;
    String title;
    Boolean doneStatus;
    String description;
    List<Map<String, String>> tasksof;
    List<Map<String, String>> categories;
}
