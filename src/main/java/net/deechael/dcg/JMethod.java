package net.deechael.dcg;

import net.deechael.dcg.body.Operation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JMethod extends JExecutableParametered {

    private final Class<?> returnType;
    private final Level level;
    private final JClass parent;
    private final String methodName;

    JMethod(Level level, JClass clazz, String methodName) {
        this(void.class, level, clazz, methodName);
    }

    JMethod(Class<?> returnType, Level level, JClass clazz, String methodName) {
        this.returnType = returnType;
        this.level = level;
        this.parent = clazz;
        this.methodName = methodName;
    }

    @Override
    public String getString() {
        StringBuilder base = new StringBuilder();
        base.append(this.annotationString())
                .append(level.getString())
                .append(" ")
                .append(returnType.getName())
                .append(" ")
                .append(methodName)
                .append("(");
        List<Map.Entry<String, Class<?>>> entries = new ArrayList<>(this.getParameters().entrySet());
        for (int i = 0; i < entries.size(); i++) {
            Map.Entry<String, Class<?>> entry = entries.get(i);
            base.append(entry.getValue().getName()).append(" ").append(entry.getKey());
            if (i != entries.size() - 1) {
                base.append(", ");
            }
        }
        base.append(") ");
        List<Class<?>> throwables = getThrowings();
        if (throwables.size() > 0) {
            base.append("throws ");
            for (int i = 0; i < throwables.size(); i++) {
                base.append(throwables.get(i).getName());
                if (i != throwables.size() - 1) {
                    base.append(", ");
                }
            }
            base.append(" ");
        }
        base.append("{\n");
        for (Operation operation : this.getOperations()) {
            base.append(operation.getString()).append("\n");
        }
        base.append("}").append("\n");
        return base.toString();
    }

    @Override
    public void addAnnotation(Class<?> annotation, Map<String, JStringVar> values) {
        if (!annotation.isAnnotation()) throw new RuntimeException("The class is not an annotation!");
        Target target = annotation.getAnnotation(Target.class);
        if (target != null) {
            boolean hasConstructor = false;
            for (ElementType elementType : target.value()) {
                if (elementType == ElementType.METHOD) {
                    hasConstructor = true;
                    break;
                }
            }
            if (!hasConstructor) throw new RuntimeException("This annotation is not for method!");
        }
        super.addAnnotation(annotation, values);
    }

}
