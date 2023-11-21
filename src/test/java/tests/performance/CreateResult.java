package tests.performance;

import lombok.Getter;
import lombok.AllArgsConstructor;
import java.util.List;

@AllArgsConstructor
@Getter
public class CreateResult {
    private final long timeTaken;
    private final List<String> createdIds;
}

