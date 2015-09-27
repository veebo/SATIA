import com.mephiboys.satia.groovy.PageResolver;
import com.mephiboys.satia.view.GroovyFacade;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class GroovyTest {

    private GroovyFacade groovy = GroovyFacade.getInstance();

    @Test
    public void testParseURI(){

        String[] in = { "", "/", "a", "/a"};
        String[] out = { "", "", "a", "a" };

        for (int i = 0; i < in.length; ++i) {
            assertEquals(out[i], groovy.parseURI(in[i]));

        }
    }

    @Test
    public void testMethods() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        GroovyClassLoader groovyLoader = new GroovyClassLoader();
        Class pageResolverClass = groovyLoader.loadClass("com.mephiboys.satia.groovy.PageResolver");
        Set<String> methods = Arrays.asList(pageResolverClass
                .getMethods()).stream().map(Method::getName).collect(Collectors.toSet());
        Set<String> expectedMethods = new HashSet<>();
        expectedMethods.add("defaultPage");
        assertEquals(true, methods.containsAll(expectedMethods));
    }
}
