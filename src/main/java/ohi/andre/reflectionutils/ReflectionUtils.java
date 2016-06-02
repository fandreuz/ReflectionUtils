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
import java.util.ArrayList;
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
    
    public static List<String> setAllFieldsTo(Object value, Object parentClass) {
        List<String> settedFields = new ArrayList<>();
        
        List<Field> fields = Arrays.asList(parentClass.getClass().getDeclaredFields());
        Class<?> toSetClass = value.getClass();

        for (int count = 0; count < fields.size(); count++) {
            
            Field field = fields.get(count);
            if (value.getClass().isAssignableFrom(toSetClass)) {
                field.setAccessible(true);
                try {
                    field.set(parentClass, value);
                    settedFields.add(field.getName() + EQUALS + value.toString());
                } catch (IllegalAccessException e) {}
            }
        }
        
        return settedFields;
    }
    
    public static void setAllFieldsTo(Object value, Object parentClass, PrintWriter writer) {
        if(writer == null) {
            return;
        }
        
        List<String> settedFields = setAllFieldsTo(value, parentClass);
        
        for(String s : settedFields) {
            writer.write(s);
        }
    }

    public static List<String> printStackTrace(StackTraceElement[] elements) {
        List<String> stackTrace = new ArrayList<>();

        String last = null;
        boolean lastWasTabbed = false;
        for (StackTraceElement element : elements) {
            if (last == null) {
                stackTrace.add(element.toString());
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
                    stackTrace.add(TAB + current);
                } else {
                    stackTrace.add(current);
                }
            } else {
                if (lastWasTabbed) {
                    stackTrace.add(current);
                    lastWasTabbed = false;
                } else {
                    stackTrace.add(TAB + current);
                    lastWasTabbed = true;
                }
            }

            last = current;
        }
        
        return stackTrace;
    }
    
    public static void printStackTrace(StackTraceElement[] elements, PrintWriter writer) {
        if(writer == null) {
            return;
        }
        
        List<String> stackTrace = printStackTrace(elements);
        
        writer.write(START_LABEL);
        for(String s : stackTrace) {
            writer.write(s);
        }
        writer.write(END_LABEL);
        writer.write(NEWLINE);
    }

    public static List<String> printDeclaredMethods(Class<?> parentClass) {
        return printDeclaredMethods(parentClass, (Class<?>) null);
    }

    public static List<String> printDeclaredMethods(Class<?> parentClass, Class<?> returnType) {
        List<String> methods = new ArrayList<>();

        for (Method method : parentClass.getDeclaredMethods()) {
            if (returnType == null || returnType.equals(method.getReturnType())) {
                methods.add(method.getReturnType() + SPACE + method.getName() + OPEN_LIST + Arrays.toString(method.getParameterTypes()) + CLOSE_LIST);
            }
        }
        
        return methods;
    }
    
    public static void printDeclaredMethods(Class<?> parentClass, PrintWriter writer) {
        printDeclaredMethods(parentClass, null, writer);
    }

    public static void printDeclaredMethods(Class<?> parentClass, Class<?> returnType, PrintWriter writer) {
        if(writer == null) {
            return;
        }
        
        List<String> methods = printDeclaredMethods(parentClass, returnType);
        
        writer.write(START_LABEL);
        for(String s : methods) {
            writer.write(s);
        }
        writer.write(END_LABEL);
        writer.write(NEWLINE);
    }

    public static List<String> printDeclaredFields(Object parentClass) {
        return printDeclaredFields(parentClass, (Class<?>) null);
    }

    public static List<String> printDeclaredFields(Object parentClass, Class<?> type) {
        List<String> fields = new ArrayList<>();
        
        Class<?> c = parentClass.getClass();

        for (Field field : c.getDeclaredFields()) {
            field.setAccessible(true);

            if (type == null || type.equals(field.getType())) {
                try {
                    fields.add(field.getType().getName() + SPACE + field.getName() + EQUALS + 
                            field.get(parentClass).toString());
                } 
                catch (IllegalAccessException e) {} 
                catch (NullPointerException e) {
                    fields.add(field.getType().getName() + SPACE + field.getName() + EQUALS + NULL_LABEL);
                }
            }
        }
        
        return fields;
    }
    
    public static void printDeclaredFields(Object parentClass, PrintWriter writer) {
        printDeclaredFields(parentClass, (Class<?>) null, writer);
    }
    
    public static void printDeclaredFields(Object parentClass, Class<?> type, PrintWriter writer) {
        if(writer == null) {
            return;
        }
        
        List<String> fields = printDeclaredFields(parentClass, type);
        
        writer.write(START_LABEL);
        for(String s : fields) {
            writer.write(s);
        }
        writer.write(END_LABEL);
        writer.write(NEWLINE);
    }
    
    public static Field getField(Class<?> parentClass, String name, String type) {
        for (Field f : parentClass.getDeclaredFields()) {
            if(name != null && !name.equals(f.getName())) {
                continue;
            }
            
            if (type != null && !f.getType().getName().equals(type)) {
                f.setAccessible(true);
                return f;
            }
        }
        
        return null;
    }

    public static Field getField(Class<?> parentClass, Class<?> type) {
        return getField(parentClass, null, type.getName());
    }
    
    public static Field getField(Class<?> parentClass, String name) {
        return getField(parentClass, name, null);
    }

    public static Method getMethod(Class<?> clazz, String name, String returnType, String[] args) {
        Method[] methods = clazz.getDeclaredMethods();

        MainLoop:
        for (Method method : methods) {
               
            if (name != null && !name.equals(method.getName())) {
                continue;
            }
            
            if(returnType != null && !returnType.equals(method.getReturnType().getName())) {
                continue;
            }

            Class<?>[] args2 = method.getParameterTypes();
            if (args2.length != args.length) {
                continue;
            }

            for (int count = 0; count < args.length; count++) {
                if (!(args[count].equals(args2[count].getName()))) {
                    continue MainLoop;
                }
            }

            return method;
        }

        return null;
    }
    
    public static Method getMethod(Class<?> clazz, String name) {
        return getMethod(clazz, name, null, null);
    }
    
    public static Method getMethod(Class<?> clazz, Class<?> returnType) {
        return getMethod(clazz, null, returnType.getName(), null);
    }
    
    public static Method getMethod(Class<?> clazz, Class<?>[] args) {
        String[] params = new String[args.length];
        for(int count = 0; count < params.length; count++) {
            params[count] = args[count].getName();
        }
        return getMethod(clazz, null, null, params);
    }
    
    public static <T> boolean arrayContains(final T[] array, final T v) {
        if (v == null) {
            for (final T e : array) {
                if (e == null) {
                    return true;
                }
            }
        } else {
            for (final T e : array) {
                if (e == v || v.equals(e)) {
                    return true;
                }
            }
        }

        return false;
    }

}
