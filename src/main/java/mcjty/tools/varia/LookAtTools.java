package mcjty.tools.varia;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class LookAtTools {

    public static RayTraceResult getMovingObjectPositionFromPlayer(World worldIn, EntityPlayer playerIn, boolean useLiquids) {
        float pitch = playerIn.rotationPitch;
        float yaw = playerIn.rotationYaw;
        Vec3d vec3 = getPlayerEyes(playerIn);
        float f2 = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
        float f3 = MathHelper.sin(-yaw * 0.017453292F - (float)Math.PI);
        float f4 = -MathHelper.cos(-pitch * 0.017453292F);
        float f5 = MathHelper.sin(-pitch * 0.017453292F);
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double reach = 5.0D;
        if (playerIn instanceof net.minecraft.entity.player.EntityPlayerMP) {
            reach = ((EntityPlayerMP)playerIn).interactionManager.getBlockReachDistance();
        }
        Vec3d vec31 = vec3.addVector(f6 * reach, f5 * reach, f7 * reach);
        return worldIn.rayTraceBlocks(vec3, vec31, useLiquids, !useLiquids, false);
    }

    private static Vec3d getPlayerEyes(EntityPlayer playerIn) {
        double x = playerIn.posX;
        double y = playerIn.posY + playerIn.getEyeHeight();
        double z = playerIn.posZ;
        return new Vec3d(x, y, z);
    }
}
