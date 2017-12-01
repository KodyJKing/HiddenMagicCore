package hiddenmagiccore;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.Arrays;

import static org.objectweb.asm.Opcodes.*;

public class Transformer implements IClassTransformer {

    static boolean obfuscated = false;
    private static Hook hook = new Hook(
            "net/minecraft/block/BlockLeaves/updateTick (Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Ljava/util/Random;)V arr/b (Lamu;Let;Lawt;Ljava/util/Random;)V",
            "hiddenmagiccore/Hooks"
    );

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        obfuscated = !name.equals(transformedName);
        String dotClassPath = hook.classPath.replace('/', '.');
        if (transformedName.equals(dotClassPath)) {
//            System.out.println("Obfuscated: " + String.valueOf(obfuscated));
//            System.out.println("Name: " + String.valueOf(name));
//            System.out.println("Transformed Name: " + String.valueOf(transformedName));
            try {
                ClassNode classNode = new ClassNode();
                ClassReader classReader = new ClassReader(basicClass);
                classReader.accept(classNode, 0);

                findMethod(hook, classNode);

                ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
                classNode.accept(classWriter);
                return classWriter.toByteArray();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return basicClass;
    }

    private static void findMethod(Hook hook, ClassNode classNode) {
        for (MethodNode methodNode : classNode.methods) {
//            System.out.println("Checking method...");
//            System.out.println(methodNode.name + " " + methodNode.desc);
            if (methodNode.name.equals(hook.getMethodName()) && methodNode.desc.equals(hook.getDesciptor()))
                transformMethod(hook, methodNode);
        }
    }

    private static void transformMethod(Hook hook, MethodNode methodNode) {
//        System.out.println("Transforming method.");
//        System.out.println(methodNode.name + " " + methodNode.desc);

        AbstractInsnNode firstNode = methodNode.instructions.getFirst();

        InsnList insnList = new InsnList();
        LabelNode labelNode = new LabelNode();

        insnList.add(labelNode);

        String hookDesc = hook.getHookDescriptor(methodNode);
        loadArguments(insnList, hookDesc);
//        System.out.println("Using method descriptor: " + hookDesc);

        insnList.add(new MethodInsnNode(INVOKESTATIC, hook.hookClassPath, hook.methodName, hookDesc,false));

        methodNode.instructions.insertBefore(firstNode, insnList);
    }

    private static void loadArguments(InsnList insnList, String desc) {
//        System.out.println("Loading arguments.");
        Type[] types = Type.getArgumentTypes(desc);
        int i = 0;
        for (Type type: types) {
            int opcode = type.getOpcode(Opcodes.ILOAD);
            insnList.add(new VarInsnNode(opcode, i++));
        }
    }

    private static boolean isStatic(MethodNode methodNode) {
        for (LocalVariableNode local: methodNode.localVariables) {
            if (local.name.equals("this")) return false;
        }
        return true;
    }

    private static class Hook {

        public String classPath, obfClassPath, methodName, obfMethodName, desciptor, obfDescriptor, hookClassPath;

        public Hook(String srg, String hookClassPath) {
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

//            System.out.println("classPath: " + classPath);
//            System.out.println("obfClassPath: " + obfClassPath);
//            System.out.println("methodName: " + methodName);
//            System.out.println("obfMethodName: " + obfMethodName);
//            System.out.println("desciptor: " + desciptor);
//            System.out.println("obfDescriptor: " + obfDescriptor);
//            System.out.println("hookClassPath: " + hookClassPath);
        }

        private static String[] classAndMethod(String path) {
            ArrayList<String> parts = new ArrayList<String>(Arrays.asList(path.split("/")));
            String method = parts.remove(parts.size() - 1);
            String classPath = String.join("/", parts);
            return new String[]{classPath, method};
        }

        public String getClassPath() {
            return obfuscated ? obfClassPath : classPath;
        }

        public String getMethodName() {
            return obfuscated ? obfMethodName : methodName;
        }

        public String getDesciptor() {
            return obfuscated ? obfDescriptor : desciptor;
        }

        public String getHookDescriptor(MethodNode methodNode) {
            if (isStatic(methodNode)) return methodNode.desc;
            return "(L" + getClassPath() + ";" + methodNode.desc.substring(1);
        }
    }
}
