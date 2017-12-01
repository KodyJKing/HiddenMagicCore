package hiddenmagiccore;

import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class Hooks {

    public static void updateTick(BlockLeaves thisIn, World worldIn, BlockPos pos, IBlockState state, Random rand) {
        System.out.println("HOOKED!!!");
    }

}
