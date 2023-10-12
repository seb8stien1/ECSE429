package config;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RandomOrderTestRunner extends BlockJUnit4ClassRunner {
    public RandomOrderTestRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected List<FrameworkMethod> computeTestMethods() {
        List<FrameworkMethod> testMethods = super.computeTestMethods();
        List<FrameworkMethod> randomOrder = new ArrayList<>(testMethods);
        Collections.shuffle(randomOrder);
        return randomOrder;
    }
}
