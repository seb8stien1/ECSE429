package tests;

import config.RandomOrderTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

@RunWith(RandomOrderTestRunner.class)
public class CategoryTest {
    @Test
    public void doNothing() {
        assertTrue(true);
    }

    @Test
    public void testEmpty() throws IOException {}

    @Test
    public void testBlank() throws IOException {}
}
