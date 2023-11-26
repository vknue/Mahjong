package vknue.mahjong.utilities;

import java.lang.reflect.*;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DocumentationUtils {

    private DocumentationUtils(){

    }

    public static final String HTML_OPENING = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Document</title>
                </head>
                <body>
                """;

    public static final String HTML_CLOSING = """
                </body>
                </html>
                """;
    public static final String HEADER_OPENING = "<h5>";
    public static final String HEADER_CLOSING = "</h5>";
    public static final String HORIZONTAL_LINE = "<hr/>";
    public static final String HTML_LINE_SEPERATOR = "<br/>";

    public static String getDocumentationForClass(Class<?> c){
        StringBuilder sb = new StringBuilder();
        sb
                .append(getPackage(c))
                .append(HTML_LINE_SEPERATOR)
                .append(getModifiers(c))
                .append(getName(c))
                .append(getParent(c))
                .append(getInterfaces(c))
                .append(HTML_LINE_SEPERATOR)
                .append(HTML_LINE_SEPERATOR)
                .append(getFields(c))
                .append(HTML_LINE_SEPERATOR)
                .append(HTML_LINE_SEPERATOR)
                .append(getConstructors(c))
                .append(HTML_LINE_SEPERATOR)
                .append(HTML_LINE_SEPERATOR)
                .append(getMethods(c));

        return sb.toString();
    }

    private static String getPackage(Class<?> c){
        StringBuilder sb = new StringBuilder();
        sb.append(c.getPackage())
                .append(HTML_LINE_SEPERATOR)
                .append(HTML_LINE_SEPERATOR);
        return sb.toString();
    }
    private static String getModifiers(Class <?> c){
        return Modifier.toString(c.getModifiers());
    }
    private static String getName(Class<?> c){
        return " " + c.getSimpleName();
    }
    private static String getParent(Class<?> c){
        Class<?> superclass = c.getSuperclass();
        if(superclass == null) {
            return "";
        }
        return " extends " +
                superclass.getSimpleName();
    }
    private static String getInterfaces(Class<?> c) {
        if (c.getInterfaces().length > 0) {
            return " implements " +
                    Stream.of(c.getInterfaces())
                            .map(Class::getSimpleName)
                            .collect(Collectors.joining(", "));
        }
        return "";
    }

    private static String getFields(Class<?> c) {
        Field[] fields = c.getDeclaredFields();
        return HTML_LINE_SEPERATOR +
                HTML_LINE_SEPERATOR +
                Stream.of(fields)
                        .map(Objects::toString)
                        .collect(Collectors.joining(HTML_LINE_SEPERATOR));

    }
    private static String getMethods(Class<?> c) {
        Method[] methods = c.getDeclaredMethods();
        StringBuilder sb = new StringBuilder();
        //iter tab tab
        for (Method method : methods) {
            sb
                    .append(HTML_LINE_SEPERATOR)
                    .append(getAnnotationsForMethod(method))
                    .append(System.lineSeparator())
                    .append(Modifier.toString(method.getModifiers()))
                    .append(" ")
                    .append(method.getReturnType())
                    .append(" ")
                    .append(method.getName())
                    .append(getParametersForMethod(method))
                    .append(getExceptionsForMethod(method));
        }
        return sb.toString();
    }
    private static String getConstructors(Class<?> c){
        Constructor[] constructors = c.getDeclaredConstructors();
        StringBuilder sb = new StringBuilder();
        for (Constructor constructor : constructors) {
            sb
                    .append(HTML_LINE_SEPERATOR)
                    .append(getAnnotationsForMethod(constructor))
                    .append(HTML_LINE_SEPERATOR)
                    .append(Modifier.toString(constructor.getModifiers()))
                    .append(" ")
                    .append(constructor.getName())
                    .append(getParametersForMethod(constructor))
                    .append(getExceptionsForMethod(constructor));
        }
        return sb.toString();
    }
    private static String getParametersForMethod(Executable method){
        return  Stream.of(method.getParameters())
                        .map(Objects::toString)
                        .collect(Collectors.joining(", ", "(", ")"));
    }
    private  static String getExceptionsForMethod(Executable method){
        if (method.getExceptionTypes().length > 0) {
            return Stream.of(method.getExceptionTypes())
                            .map(Class::getSimpleName)
                            .collect(Collectors.joining(", ", " throws ", ""));
        }
        return "";
    }
    private static String getAnnotationsForMethod(Executable method) {
        return Stream.of(method.getAnnotations())
                        .map(Objects::toString)
                        .collect(Collectors.joining(System.lineSeparator()));
    }


}
