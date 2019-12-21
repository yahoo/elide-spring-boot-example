package example;

import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
public class InMemoryClassLoader extends ClassLoader {
    private Set<String> classNames = Sets.newHashSet(
            "example/models/ArtifactGroup.class",
            "example/models/ArtifactProduct.class",
            "example/models/ArtifactVersion.class");

    public InMemoryClassLoader(ClassLoader parent, Set<String> classNames) {
        super(parent);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return super.findClass(name);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return super.loadClass(name);
    }

    @Override
    protected URL findResource(String name) {
        if (classNames.contains(name)) {
            try {
                return new URL("file://" + name);
            } catch (MalformedURLException e) {
                throw new IllegalStateException(e);
            }
        }
        return super.findResource(name);
    }
/*
    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        List<URL> urls = new ArrayList<>();
        for (String className : classNames) {
            className = className.replace(".", "/");
            if (className.startsWith(name)) {
                urls.add(findResource(className));
            }
        }
        if (urls.isEmpty()) {
            return super.getResources(name);
        }
        return Collections.enumeration(urls);
    }
 */
}
