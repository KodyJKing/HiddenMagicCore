package hiddenmagiccore;

import java.util.ArrayList;
import java.util.HashMap;

public class HookRegistry {

    static HashMap<String, ArrayList<Hook>> hooks;

    static void registerHook(String srg, String hookClass, String hookMethodName, boolean insertBefore) {
        Hook hook = new Hook(srg, hookClass, hookMethodName, insertBefore);
        if (!hooks.containsKey(hook.classPath)) hooks.put(hook.classPath, new ArrayList<>());
        ArrayList<Hook> classHooks = hooks.get(hook.classPath);
        classHooks.add(hook);
        System.out.println("Registered hook:");
        hook.print();
    }

    static boolean hasHooks(String className) {
        return hooks.containsKey(className);
    }

    static ArrayList<Hook> getHooks(String className) {
        if (!hasHooks(className))
            return null;
        return hooks.get(className);
    }

    static {
        hooks = new HashMap<>();

        registerHook(
        "net/minecraft/block/BlockLeaves/updateTick (Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Ljava/util/Random;)V arr/b (Lamu;Let;Lawt;Ljava/util/Random;)V",
        "hiddenmagic/CoreHooks","leafTick",true
        );

        registerHook(
        "net/minecraft/world/chunk/Chunk/setBlockState (Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)Lnet/minecraft/block/state/IBlockState; axw/a (Let;Lawt;)Lawt;",
        "hiddenmagic/CoreHooks", "setBlockState", true
        );
    }

}
