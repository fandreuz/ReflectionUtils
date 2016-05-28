/* Copyright Francesco Andreuzzi

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */

package ohi.andre.reflectionutils;

import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author francescoandreuzzi
 */

public class ReflectionUtils {
    
    private static final String DOT = ".";
    private static final String SPACE = " ";
    private static final String TAB = "\t";
    private static final String OPEN_LIST = "(";
    private static final String CLOSE_LIST = ")";
    private static final String EQUALS = "=";
    private static final String NEWLINE = "\n";
    
    private static final String START_LABEL = "--- start";
    private static final String END_LABEL = "--- end";
    private static final String NULL_LABEL = "null";
    
    public static void setAllFieldsTo(Object value, Object parentClass, PrintWriter writer) {
        List<Field> fields = Arrays.asList(parentClass.getClass().getDeclaredFields());
        Class<?> toSetClass = value.getClass();

        for (int count = 0; count < fields.size(); count++) {
            
            Field field = fields.get(count);
            if (value.getClass().isAssignableFrom(toSetClass)) {
                field.setAccessible(true);
                try {
                    field.set(parentClass, value);
                    writer.write(field.getName() + EQUALS + value.toString());
                } catch (IllegalAccessException e) {}
            }
        }
    }

    public static void printStackTrace(StackTraceElement[] elements, PrintWriter writer) {
        writer.write(START_LABEL);

        String last = null;
        boolean lastWasTabbed = false;
        for (StackTraceElement element : elements) {
            if (last == null) {
                writer.write(element.toString());
                last = element.toString();
                continue;
            }

            String current = element.toString();

            int currentFirstPoint = current.indexOf(DOT);
            String after = current.substring(currentFirstPoint + 1);
            int currentSecondPoint = after.indexOf(DOT);
            if (currentSecondPoint == -1)
                currentSecondPoint = currentFirstPoint;

            int lastFirstPoint = last.indexOf(DOT);
            after = last.substring(lastFirstPoint + 1);
            int lastSecondPoint = after.indexOf(DOT);
            if (lastSecondPoint == -1)
                lastSecondPoint = lastFirstPoint;

            if (current.substring(0, currentSecondPoint).equals(last.substring(0, lastSecondPoint))) {
                if (lastWasTabbed) {
                    writer.write(TAB + current);
                } else {
                    writer.write(current);
                }
            } else {
                if (lastWasTabbed) {
                    writer.write(current);
                    lastWasTabbed = false;
                } else {
                    writer.write(TAB + current);
                    lastWasTabbed = true;
                }
            }

            last = current;
        }

        writer.write(END_LABEL);
        writer.write(NEWLINE);
    }

    public static void printDeclaredMethods(Class<?> c, PrintWriter writer) {
        printDeclaredMethods(c, null, writer);
    }

    public static void printDeclaredMethods(Class<?> c, Class<?> returnType, PrintWriter writer) {
        writer.write(START_LABEL);

        for (Method method : c.getDeclaredMethods()) {
            if (returnType == null || returnType.equals(method.getReturnType())) {
                writer.write(method.getReturnType() + SPACE + method.getName() + OPEN_LIST + Arrays.toString(method.getParameterTypes()) + CLOSE_LIST);
            }
        }

        writer.write(END_LABEL);
        writer.write(NEWLINE);
    }

    public static void printDeclaredFields(Object o, PrintWriter writer) {
        printDeclaredFields(o, null, writer);
    }

    public static void printDeclaredFields(Object o, Class<?> type, PrintWriter writer) {
        Class<?> c = o.getClass();
        writer.write(START_LABEL);

        for (Field field : c.getDeclaredFields()) {
            field.setAccessible(true);

            if (type == null || type.equals(field.getType())) {
                try {
                    writer.write(field.getType().getName() + SPACE + field.getName() + EQUALS + field.get(o).toString());
                } 
                catch (IllegalAccessException e) {} 
                catch (NullPointerException e) {
                    writer.write(field.getType().getName() + SPACE + field.getName() + EQUALS + NULL_LABEL);
                }
            }
        }

        writer.write(END_LABEL);
        writer.write(NEWLINE);
    }
    
    public static boolean containsInt(int[] array, int i) {
        if (array == null) {
            return false;
        }

        for (int n : array) {
            if (n == i) {
                return true;
            }
        }
        return false;
    }

    public static Field getFieldOfType(Field[] fields, String name, Class<?> type) {
        Field r = null;
        for (Field f : fields) {
            if (f.getType().equals(type) && (name == null || f.getName().equals(name))) {
                r = f;
            }
        }
        if (r != null) {
            r.setAccessible(true);
        }
        return r;
    }

    public static Field getFieldOfType(Field[] fields, Class<?> type) {
        return getFieldOfType(fields, null, type);
    }

    public static Method getMethod(Class<?> clazz, Class<?> returnType, Class<?>[] args) {
        Method[] methods = clazz.getDeclaredMethods();

        MainLoop:
        for (Method method : methods) {
            if (method.getReturnType().equals(returnType)) {

                Class<?>[] args2 = method.getParameterTypes();
                if (args2.length != args.length) {
                    continue;
                }

                for (int count = 0; count < args.length; count++) {
                    if (!(args[count].equals(args2[count]))) {
                        continue MainLoop;
                    }
                }

                return method;
            }
        }

        return null;
    }
}
