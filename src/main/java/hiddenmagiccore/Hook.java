package hiddenmagiccore;

import java.util.ArrayList;
import java.util.Arrays;

class Hook {

    public String classPath, obfClassPath, methodName, obfMethodName, desciptor, obfDescriptor, hookClassPath, hookMethodName;
    public boolean insertBefore;

    public Hook(String srg, String hookClassPath, String hookMethodName, boolean insertBefore) {
        String[] parts = srg.split(" ");

        String[] classMethod = classAndMethod(parts[0]);
        this.classPath = classMethod[0];
        this.methodName = classMethod[1];

        this.desciptor = parts[1];

        String[] obfClassMethod = classAndMethod(parts[2]);
        this.obfClassPath = obfClassMethod[0];
        this.obfMethodName = obfClassMethod[1];

        this.obfDescriptor = parts[3];

        this.hookClassPath = hookClassPath;
        this.hookMethodName = hookMethodName;

        this.insertBefore = insertBefore;
    }

    public Hook(String srg, String hookClassPath, String hookMethodName) {
        this(srg, hookClassPath, hookMethodName,true);
    }


    public void print() {
        System.out.println("classPath: " + classPath);
//        System.out.println("obfClassPath: " + obfClassPath);
        System.out.println("methodName: " + methodName);
//        System.out.println("obfMethodName: " + obfMethodName);
//        System.out.println("desciptor: " + desciptor);
//        System.out.println("obfDescriptor: " + obfDescriptor);
        System.out.println("hookClassPath: " + hookClassPath);
        System.out.println("hookMethodName: " + hookMethodName);
    }

    private static String[] classAndMethod(String path) {
        ArrayList<String> parts = new ArrayList<String>(Arrays.asList(path.split("/")));
        String method = parts.remove(parts.size() - 1);
        String classPath = String.join("/", parts);
        return new String[]{classPath, method};
    }

    public String getClassPath(boolean obfuscated) {
        return obfuscated ? obfClassPath : classPath;
    }

    public String getMethodName(boolean obfuscated) {
        return obfuscated ? obfMethodName : methodName;
    }

    public String getDesciptor(boolean obfuscated) {
        return obfuscated ? obfDescriptor : desciptor;
    }

    // TODO: Make this work on strongly typed objects rather than strings.
    public String getHookDescriptor(String desc, boolean isStatic, boolean obfuscated) {
        // Make it a static function.
        String staticDesc;
        if (isStatic)
            staticDesc = desc;
        else
            staticDesc = "(L" + getClassPath(obfuscated) + ";" + desc.substring(1);
        // Make it a void function.
        return staticDesc.substring(0, staticDesc.indexOf(")")) + ")V";
    }
}
