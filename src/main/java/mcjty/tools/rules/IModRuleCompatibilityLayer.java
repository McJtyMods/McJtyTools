package mcjty.tools.rules;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IModRuleCompatibilityLayer {

    // --------------------
    // Baubles
    // --------------------
    boolean hasBaubles();

    int[] getAmuletSlots();

    int[] getBeltSlots();

    int[] getBodySlots();

    int[] getCharmSlots();

    int[] getHeadSlots();

    int[] getRingSlots();

    int[] getTrinketSlots();

    ItemStack getBaubleStack(EntityPlayer player, int slot);


    // --------------------
    // Game Stages
    // --------------------

    boolean hasGameStages();

    boolean hasGameStage(EntityPlayer player, String stage);

    // --------------------
    // Lost Cities
    // --------------------

    boolean hasLostCities();

    <T> boolean isCity(IEventQuery<T> query, T event);

    <T> boolean isStreet(IEventQuery<T> query, T event);

    <T> boolean inSphere(IEventQuery<T> query, T event);

    <T> boolean isBuilding(IEventQuery<T> query, T event);

    // --------------------
    // Serene Seasons
    // --------------------

    boolean hasSereneSeasons();

    boolean isSpring(World world);

    boolean isSummer(World world);

    boolean isWinter(World world);

    boolean isAutumn(World world);
}
