package example;

import com.google.common.collect.Sets;
import org.mdkt.compiler.InMemoryJavaCompiler;

import java.util.Map;

public class EntityCompiler {

    public static String [] classNames = {
            "example.models.ArtifactGroup",
            "example.models.ArtifactProduct",
            "example.models.ArtifactVersion"
    };

    private InMemoryJavaCompiler compiler = InMemoryJavaCompiler.newInstance()
            .useParentClassLoader(new InMemoryClassLoader(ClassLoader.getSystemClassLoader(), Sets.newHashSet(classNames)));

    private Map<String, Class<?>> compiledObjects;

    public String getGroup() {
        return "package example.models;\n" +
                "\n" +
                "import com.yahoo.elide.annotation.Include;\n" +
                "\n" +
                "import javax.persistence.Entity;\n" +
                "import javax.persistence.Id;\n" +
                "import javax.persistence.OneToMany;\n" +
                "import java.util.ArrayList;\n" +
                "import java.util.List;\n" +
                "\n" +
                "@Include(rootLevel = true, type = \"group\")\n" +
                "@Entity\n" +
                "public class ArtifactGroup {\n" +
                "    @Id\n" +
                "    private String name = \"\";\n" +
                "\n" +
                "    private String commonName = \"\";\n" +
                "\n" +
                "    private String description = \"\";\n" +
                "\n" +
                "    @OneToMany(mappedBy = \"group\")\n" +
                "    private List<ArtifactProduct> products = new ArrayList<>();\n" +
                "}";
    }

    public String getArtifactProduct() {
        return "package example.models;\n" +
                "\n" +
                "import com.yahoo.elide.annotation.Include;\n" +
                "\n" +
                "import javax.persistence.Entity;\n" +
                "import javax.persistence.Id;\n" +
                "import javax.persistence.ManyToOne;\n" +
                "import javax.persistence.OneToMany;\n" +
                "import java.util.ArrayList;\n" +
                "import java.util.List;\n" +
                "\n" +
                "@Include(type = \"product\")\n" +
                "@Entity\n" +
                "public class ArtifactProduct {\n" +
                "    @Id\n" +
                "    private String name = \"\";\n" +
                "\n" +
                "    private String commonName = \"\";\n" +
                "\n" +
                "    private String description = \"\";\n" +
                "\n" +
                "    @ManyToOne\n" +
                "    private ArtifactGroup group = null;\n" +
                "\n" +
                "    @OneToMany(mappedBy = \"artifact\")\n" +
                "    private List<ArtifactVersion> versions = new ArrayList<>();\n" +
                "}";
    }

    public String getArtifactVersion() {
        return "package example.models;\n" +
                "\n" +
                "import com.yahoo.elide.annotation.Include;\n" +
                "\n" +
                "import javax.persistence.Entity;\n" +
                "import javax.persistence.Id;\n" +
                "import javax.persistence.ManyToOne;\n" +
                "import java.util.Date;\n" +
                "\n" +
                "@Include(type = \"version\")\n" +
                "@Entity\n" +
                "public class ArtifactVersion {\n" +
                "    @Id\n" +
                "    private String name = \"\";\n" +
                "\n" +
                "    private Date createdAt = new Date();\n" +
                "\n" +
                "    @ManyToOne\n" +
                "    private ArtifactProduct artifact;\n" +
                "}";
    }


    public void compile() throws Exception {
        compiler.addSource("example.models.ArtifactGroup", getGroup());
        compiler.addSource("example.models.ArtifactProduct", getArtifactProduct());
        compiler.addSource("example.models.ArtifactVersion", getArtifactVersion());
        compiledObjects = compiler.compileAll();
    }

    public ClassLoader getClassLoader() {
        return compiler.getClassloader();
    }

    public Class<?> getCompiled(String name) {
        return compiledObjects.get(name);
    }
}
