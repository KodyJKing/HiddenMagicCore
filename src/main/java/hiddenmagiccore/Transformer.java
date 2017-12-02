package hiddenmagiccore;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.Type;

import java.util.List;

import org.objectweb.asm.Opcodes;
import static org.objectweb.asm.Opcodes.*;

public class Transformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        boolean obfuscated = !name.equals(transformedName);
        String classPath = transformedName.replace('.', '/');
        if (HookRegistry.hasHooks(classPath)) {

//            System.out.println("Obfuscated: " + String.valueOf(obfuscated));
//            System.out.println("Name: " + String.valueOf(name));
//            System.out.println("Transformed Name: " + String.valueOf(transformedName));

            try {
                ClassNode classNode = new ClassNode();
                ClassReader classReader = new ClassReader(basicClass);
                classReader.accept(classNode, 0);

                findMethods(classPath, classNode, obfuscated);

                ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
                classNode.accept(classWriter);
                return classWriter.toByteArray();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return basicClass;
    }

    private static void findMethods(String classPath, ClassNode classNode, boolean obfuscated) {
        List<Hook> hooks = HookRegistry.getHooks(classPath);
        for (Hook hook: hooks) {
            for (MethodNode methodNode : classNode.methods) {

//            System.out.println("Checking method...");
//            System.out.println(methodNode.name + " " + methodNode.desc);

                if (methodNode.name.equals(hook.getMethodName(obfuscated)) && methodNode.desc.equals(hook.getDesciptor(obfuscated)))
                    transformMethod(hook, methodNode, obfuscated);
            }
        }
    }

    private static void transformMethod(Hook hook, MethodNode methodNode, boolean obfuscated) {

//        System.out.println("Transforming method.");
//        System.out.println(methodNode.name + " " + methodNode.desc);

        InsnList insnList = new InsnList();
        LabelNode labelNode = new LabelNode();

        insnList.add(labelNode);

        String hookDesc = hook.getHookDescriptor(methodNode.desc, isStatic(methodNode), obfuscated);
        loadArguments(insnList, hookDesc);

//        System.out.println("Using method descriptor: " + hookDesc);

        insnList.add(new MethodInsnNode(INVOKESTATIC, hook.hookClassPath, hook.hookMethodName, hookDesc,false));

        if (hook.insertBefore) {
            AbstractInsnNode firstNode = methodNode.instructions.getFirst();
            methodNode.instructions.insertBefore(firstNode, insnList);
        } else {
            AbstractInsnNode insnNode = methodNode.instructions.getLast();
            for (int i = methodNode.instructions.size() - 1; i >= 0; i--) {
                insnNode = methodNode.instructions.get(i);
                if (insnNode.getOpcode() == RETURN) break;
            }
            methodNode.instructions.insertBefore(insnNode, insnList);
        }
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

}
