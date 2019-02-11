package mcjty.tools.rules;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import mcjty.tools.typed.AttributeMap;
import mcjty.tools.typed.Key;
import mcjty.tools.varia.Tools;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.Consumer;

import static mcjty.tools.rules.CommonRuleKeys.*;

public class RuleBase<T extends RuleBase.EventGetter> {

    protected final Logger logger;
    protected final List<Consumer<T>> actions = new ArrayList<>();

    public RuleBase(Logger logger) {
        this.logger = logger;
    }

    private static Random rnd = new Random();

    protected List<Pair<Float, ItemStack>> getItemsWeighted(List<String> itemNames) {
        List<Pair<Float, ItemStack>> items = new ArrayList<>();
        for (String json : itemNames) {
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(json);
            if (element.isJsonPrimitive()) {
                String name = element.getAsString();
                Pair<Float, ItemStack> pair = Tools.parseStackWithFactor(name, logger);
                if (pair.getValue().isEmpty()) {
                    logger.log(Level.ERROR, "Unknown item '" + name + "'!");
                } else {
                    items.add(pair);
                }
            } else if (element.isJsonObject()) {
                JsonObject obj = element.getAsJsonObject();
                Pair<Float, ItemStack> pair = Tools.parseStackWithFactor(obj, logger);
                if (pair != null) {
                    items.add(pair);
                }
            } else {
                logger.log(Level.ERROR, "Item description '" + json + "' is not valid!");
            }
        }
        return items;
    }

    protected ItemStack getRandomItem(List<Pair<Float, ItemStack>> items, float total) {
        float r = rnd.nextFloat() * total;
        for (Pair<Float, ItemStack> pair : items) {
            if (r <= pair.getLeft()) {
                return pair.getRight().copy();
            }
            r -= pair.getLeft();
        }
        return ItemStack.EMPTY;
    }

    protected float getTotal(List<Pair<Float, ItemStack>> items) {
        float total = 0.0f;
        for (Pair<Float, ItemStack> pair : items) {
            total += pair.getLeft();
        }
        return total;
    }

    public interface EventGetter {
        EntityLivingBase getEntityLiving();

        EntityPlayer getPlayer();

        World getWorld();

        BlockPos getPosition();
    }

    protected void addActions(AttributeMap map, IModRuleCompatibilityLayer layer) {
        if (map.has(ACTION_HEALTHMULTIPLY) || map.has(ACTION_HEALTHADD)) {
            addHealthAction(map);
        }
        if (map.has(ACTION_SPEEDMULTIPLY) || map.has(ACTION_SPEEDADD)) {
            addSpeedAction(map);
        }
        if (map.has(ACTION_DAMAGEMULTIPLY) || map.has(ACTION_DAMAGEADD)) {
            addDamageAction(map);
        }
        if (map.has(ACTION_SIZEMULTIPLY) || map.has(ACTION_SIZEADD)) {
            addSizeActions(map);
        }
        if (map.has(ACTION_POTION)) {
            addPotionsAction(map);
        }
        if (map.has(ACTION_ANGRY)) {
            addAngryAction(map);
        }
        if (map.has(ACTION_MOBNBT)) {
            addMobNBT(map);
        }
        if (map.has(ACTION_HELDITEM)) {
            addHeldItem(map);
        }
        if (map.has(ACTION_ARMORBOOTS)) {
            addArmorItem(map, ACTION_ARMORBOOTS, EntityEquipmentSlot.FEET);
        }
        if (map.has(ACTION_ARMORLEGS)) {
            addArmorItem(map, ACTION_ARMORLEGS, EntityEquipmentSlot.LEGS);
        }
        if (map.has(ACTION_ARMORHELMET)) {
            addArmorItem(map, ACTION_ARMORHELMET, EntityEquipmentSlot.HEAD);
        }
        if (map.has(ACTION_ARMORCHEST)) {
            addArmorItem(map, ACTION_ARMORCHEST, EntityEquipmentSlot.CHEST);
        }
        if (map.has(ACTION_FIRE)) {
            addFireAction(map);
        }
        if (map.has(ACTION_EXPLOSION)) {
            addExplosionAction(map);
        }
        if (map.has(ACTION_CLEAR)) {
            addClearAction(map);
        }
        if (map.has(ACTION_DAMAGE)) {
            addDoDamageAction(map);
        }
        if (map.has(ACTION_MESSAGE)) {
            addDoMessageAction(map);
        }
        if (map.has(ACTION_GIVE)) {
            addGiveAction(map);
        }
        if (map.has(ACTION_DROP)) {
            addDropAction(map);
        }
        if (map.has(ACTION_SETBLOCK)) {
            addSetBlockAction(map);
        }
        if (map.has(ACTION_SETSTATE)) {
            if (layer.hasEnigmaScript()) {
                addStateAction(map, layer);
            } else {
                logger.warn("EnigmaScript is missing: this action cannot work!");
            }
        }
        if (map.has(ACTION_SETPSTATE)) {
            if (layer.hasEnigmaScript()) {
                addPStateAction(map, layer);
            } else {
                logger.warn("EnigmaScript is missing: this action cannot work!");
            }
        }
    }

    private static Map<String, DamageSource> damageMap = null;

    private static void addSource(DamageSource source) {
        damageMap.put(source.getDamageType(), source);
    }

    private void createDamageMap() {
        if (damageMap == null) {
            damageMap = new HashMap<>();
            addSource(DamageSource.IN_FIRE);
            addSource(DamageSource.LIGHTNING_BOLT);
            addSource(DamageSource.ON_FIRE);
            addSource(DamageSource.LAVA);
            addSource(DamageSource.HOT_FLOOR);
            addSource(DamageSource.IN_WALL);
            addSource(DamageSource.CRAMMING);
            addSource(DamageSource.DROWN);
            addSource(DamageSource.STARVE);
            addSource(DamageSource.CACTUS);
            addSource(DamageSource.FALL);
            addSource(DamageSource.FLY_INTO_WALL);
            addSource(DamageSource.OUT_OF_WORLD);
            addSource(DamageSource.GENERIC);
            addSource(DamageSource.MAGIC);
            addSource(DamageSource.WITHER);
            addSource(DamageSource.ANVIL);
            addSource(DamageSource.FALLING_BLOCK);
            addSource(DamageSource.DRAGON_BREATH);
            addSource(DamageSource.FIREWORKS);
        }
    }

    private void addDoDamageAction(AttributeMap map) {
        String damage = map.get(ACTION_DAMAGE);
        createDamageMap();
        String[] split = StringUtils.split(damage, "=");
        DamageSource source = damageMap.get(split[0]);
        if (source == null) {
            logger.log(Level.ERROR, "Can't find damage source '" + split[0] + "'!");
            return;
        }
        float amount = 1.0f;
        if (split.length > 1) {
            amount = Float.parseFloat(split[1]);
        }

        float finalAmount = amount;
        actions.add(event -> {
            EntityLivingBase living = event.getEntityLiving();
            if (living != null) {
                living.attackEntityFrom(source, finalAmount);
            }
        });
    }

    private void addDoMessageAction(AttributeMap map) {
        String message = map.get(ACTION_MESSAGE);
        actions.add(event -> {
            EntityPlayer player = event.getPlayer();
            if (player == null) {
                player = event.getWorld().getClosestPlayerToEntity(event.getEntityLiving(), 100);
            }
            if (player != null) {
                player.sendStatusMessage(new TextComponentString(message), false);
            }
        });
    }


    private void addGiveAction(AttributeMap map) {
        final List<Pair<Float, ItemStack>> items = getItemsWeighted(map.getList(ACTION_GIVE));
        if (items.isEmpty()) {
            return;
        }
        if (items.size() == 1) {
            ItemStack item = items.get(0).getRight();
            actions.add(event -> {
                EntityPlayer player = event.getPlayer();
                if (player != null) {
                    if (!player.inventory.addItemStackToInventory(item.copy())) {
                        player.entityDropItem(item.copy(), 1.05f);
                    }
                }
            });
        } else {
            final float total = getTotal(items);
            actions.add(event -> {
                EntityPlayer player = event.getPlayer();
                if (player != null) {
                    ItemStack item = getRandomItem(items, total);
                    if (!player.inventory.addItemStackToInventory(item.copy())) {
                        player.entityDropItem(item.copy(), 1.05f);
                    }
                }
            });
        }
    }

    private void addStateAction(AttributeMap map, IModRuleCompatibilityLayer layer) {
        String s = map.get(ACTION_SETSTATE);
        String[] split = StringUtils.split(s, '=');
        String state;
        String value;
        try {
            state = split[0];
            value = split[1];
        } catch (Exception e) {
            logger.log(Level.ERROR, "Bad state=value specifier '" + s + "'!");
            return;
        }
        String finalState = state;
        String finalValue = value;
        actions.add(event -> layer.setState(event.getWorld(), finalState, finalValue));
    }

    private void addPStateAction(AttributeMap map, IModRuleCompatibilityLayer layer) {
        String s = map.get(ACTION_SETPSTATE);
        String[] split = StringUtils.split(s, '=');
        String state;
        String value;
        try {
            state = split[0];
            value = split[1];
        } catch (Exception e) {
            logger.log(Level.ERROR, "Bad state=value specifier '" + s + "'!");
            return;
        }
        String finalState = state;
        String finalValue = value;
        actions.add(event -> layer.setPlayerState(event.getPlayer(), finalState, finalValue));
    }

    private void addSetBlockAction(AttributeMap map) {
        String json = map.get(ACTION_SETBLOCK);
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(json);
        if (element.isJsonPrimitive()) {
            String blockname = element.getAsString();
            Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockname));
            if (block == null) {
                logger.log(Level.ERROR, "Block '" + blockname + "' is not valid!");
                return;
            }
            IBlockState state = block.getDefaultState();
            actions.add(event -> event.getWorld().setBlockState(event.getPosition(), state, 3));
        } else {
            JsonObject obj = element.getAsJsonObject();
            if (!obj.has("block")) {
                logger.log(Level.ERROR, "Block is not valid!");
                return;
            }

            String blockname = obj.get("block").getAsString();
            Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockname));
            if (block == null) {
                logger.log(Level.ERROR, "Block '" + blockname + "' is not valid!");
                return;
            }
            IBlockState state = block.getDefaultState();
            if (obj.has("properties")) {
                JsonArray propArray = obj.get("properties").getAsJsonArray();
                for (JsonElement el : propArray) {
                    JsonObject propObj = el.getAsJsonObject();
                    String name = propObj.get("name").getAsString();
                    String value = propObj.get("value").getAsString();
                    for (IProperty<?> key : state.getPropertyKeys()) {
                        if (name.equals(key.getName())) {
                            state = CommonRuleEvaluator.set(state, key, value);
                        }
                    }
                }
            }
            IBlockState finalState = state;
            actions.add(event -> event.getWorld().setBlockState(event.getPosition(), finalState, 3));

        }
    }

    private void addDropAction(AttributeMap map) {
        final List<Pair<Float, ItemStack>> items = getItemsWeighted(map.getList(ACTION_DROP));
        if (items.isEmpty()) {
            return;
        }
        if (items.size() == 1) {
            ItemStack item = items.get(0).getRight();
            actions.add(event -> {
                BlockPos pos = event.getPosition();
                EntityItem entityItem = new EntityItem(event.getWorld(), pos.getX(), pos.getY(), pos.getZ(), item.copy());
                event.getWorld().spawnEntity(entityItem);
            });
        } else {
            final float total = getTotal(items);
            actions.add(event -> {
                BlockPos pos = event.getPosition();
                ItemStack item = getRandomItem(items, total);
                EntityItem entityItem = new EntityItem(event.getWorld(), pos.getX(), pos.getY(), pos.getZ(), item.copy());
                event.getWorld().spawnEntity(entityItem);
            });
        }
    }


    private void addClearAction(AttributeMap map) {
        Boolean clear = map.get(ACTION_CLEAR);
        if (clear) {
            actions.add(event -> {
                EntityLivingBase living = event.getEntityLiving();
                if (living != null) {
                    living.clearActivePotions();
                }
            });
        }
    }

    private void addFireAction(AttributeMap map) {
        Integer fireAction = map.get(ACTION_FIRE);
        actions.add(event -> {
            EntityLivingBase living = event.getEntityLiving();
            if (living != null) {
                living.attackEntityFrom(DamageSource.ON_FIRE, 0.1f);
                living.setFire(fireAction);
            }
        });
    }

    private void addExplosionAction(AttributeMap map) {
        String fireAction = map.get(ACTION_EXPLOSION);
        String[] split = StringUtils.split(fireAction, ",");
        float strength = 1.0f;
        boolean flaming = false;
        boolean smoking = false;
        try {
            strength = Float.parseFloat(split[0]);
            flaming = "1".equalsIgnoreCase(split[1]) || "true".equals(split[1].toLowerCase()) || "yes".equals(split[1].toLowerCase());
            smoking = "1".equalsIgnoreCase(split[2]) || "true".equals(split[2].toLowerCase()) || "yes".equals(split[2].toLowerCase());
        } catch (Exception ignore) {
        }

        float finalStrength = strength;
        boolean finalFlaming = flaming;
        boolean finalSmoking = smoking;
        actions.add(event -> {
            BlockPos pos = event.getPosition();
            if (pos != null) {
                event.getWorld().newExplosion(null, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, finalStrength, finalFlaming, finalSmoking);
            }
        });
    }


    private void addPotionsAction(AttributeMap map) {
        List<PotionEffect> effects = new ArrayList<>();
        for (String p : map.getList(ACTION_POTION)) {
            String[] splitted = StringUtils.split(p, ',');
            if (splitted == null || splitted.length != 3) {
                logger.log(Level.ERROR, "Bad potion specifier '" + p + "'! Use <potion>,<duration>,<amplifier>");
                continue;
            }
            Potion potion = ForgeRegistries.POTIONS.getValue(new ResourceLocation(splitted[0]));
            if (potion == null) {
                logger.log(Level.ERROR, "Can't find potion '" + p + "'!");
                continue;
            }
            int duration = 0;
            int amplifier = 0;
            try {
                duration = Integer.parseInt(splitted[1]);
                amplifier = Integer.parseInt(splitted[2]);
            } catch (NumberFormatException e) {
                logger.log(Level.ERROR, "Bad duration or amplifier integer for '" + p + "'!");
                continue;
            }
            effects.add(new PotionEffect(potion, duration, amplifier));
        }
        if (!effects.isEmpty()) {
            actions.add(event -> {
                EntityLivingBase living = event.getEntityLiving();
                if (living != null) {
                    for (PotionEffect effect : effects) {
                        PotionEffect neweffect = new PotionEffect(effect.getPotion(), effect.getDuration(), effect.getAmplifier());
                        living.addPotionEffect(neweffect);
                    }
                }
            });
        }
    }


    private void addHealthAction(AttributeMap map) {
        float m = map.has(ACTION_HEALTHMULTIPLY) ? map.get(ACTION_HEALTHMULTIPLY) : 1;
        float a = map.has(ACTION_HEALTHADD) ? map.get(ACTION_HEALTHADD) : 0;
        actions.add(event -> {
            EntityLivingBase entityLiving = event.getEntityLiving();
            if (entityLiving != null) {
                IAttributeInstance entityAttribute = entityLiving.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
                if (entityAttribute != null) {
                    double newMax = entityAttribute.getBaseValue() * m + a;
                    entityAttribute.setBaseValue(newMax);
                    entityLiving.setHealth((float) newMax);
                }
            }
        });
    }

    private void addSpeedAction(AttributeMap map) {
        float m = map.has(ACTION_SPEEDMULTIPLY) ? map.get(ACTION_SPEEDMULTIPLY) : 1;
        float a = map.has(ACTION_SPEEDADD) ? map.get(ACTION_SPEEDADD) : 0;
        actions.add(event -> {
            EntityLivingBase entityLiving = event.getEntityLiving();
            if (entityLiving != null) {
                IAttributeInstance entityAttribute = entityLiving.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
                if (entityAttribute != null) {
                    double newMax = entityAttribute.getBaseValue() * m + a;
                    entityAttribute.setBaseValue(newMax);
                }
            }
        });
    }

    private void addSizeActions(AttributeMap map) {
        logger.log(Level.WARN, "Mob resizing not implemented yet!");
        float m = map.has(ACTION_SIZEMULTIPLY) ? map.get(ACTION_SIZEMULTIPLY) : 1;
        float a = map.has(ACTION_SIZEADD) ? map.get(ACTION_SIZEADD) : 0;
        actions.add(event -> {
            EntityLivingBase entityLiving = event.getEntityLiving();
            if (entityLiving != null) {
                // Not implemented yet
//                entityLiving.setSize(entityLiving.width * m + a, entityLiving.height * m + a);
            }
        });
    }

    private void addDamageAction(AttributeMap map) {
        float m = map.has(ACTION_DAMAGEMULTIPLY) ? map.get(ACTION_DAMAGEMULTIPLY) : 1;
        float a = map.has(ACTION_DAMAGEADD) ? map.get(ACTION_DAMAGEADD) : 0;
        actions.add(event -> {
            EntityLivingBase entityLiving = event.getEntityLiving();
            if (entityLiving != null) {
                IAttributeInstance entityAttribute = entityLiving.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
                if (entityAttribute != null) {
                    double newMax = entityAttribute.getBaseValue() * m + a;
                    entityAttribute.setBaseValue(newMax);
                }
            }
        });
    }

    private void addArmorItem(AttributeMap map, Key<String> itemKey, EntityEquipmentSlot slot) {
        final List<Pair<Float, ItemStack>> items = getItemsWeighted(map.getList(itemKey));
        if (items.isEmpty()) {
            return;
        }
        if (items.size() == 1) {
            ItemStack item = items.get(0).getRight();
            actions.add(event -> {
                EntityLivingBase entityLiving = event.getEntityLiving();
                if (entityLiving != null) {
                    entityLiving.setItemStackToSlot(slot, item.copy());
                }
            });
        } else {
            final float total = getTotal(items);
            actions.add(event -> {
                EntityLivingBase entityLiving = event.getEntityLiving();
                if (entityLiving != null) {
                    entityLiving.setItemStackToSlot(slot, getRandomItem(items, total));
                }
            });
        }
    }

    private void addHeldItem(AttributeMap map) {
        final List<Pair<Float, ItemStack>> items = getItemsWeighted(map.getList(ACTION_HELDITEM));
        if (items.isEmpty()) {
            return;
        }
        if (items.size() == 1) {
            ItemStack item = items.get(0).getRight();
            actions.add(event -> {
                EntityLivingBase entityLiving = event.getEntityLiving();
                if (entityLiving != null) {
                    if (entityLiving instanceof EntityEnderman) {
                        if (item.getItem() instanceof ItemBlock) {
                            ItemBlock b = (ItemBlock) item.getItem();
                            ((EntityEnderman) entityLiving).setHeldBlockState(b.getBlock().getStateFromMeta(b.getMetadata(item.getItemDamage())));
                        }
                    } else {
                        entityLiving.setHeldItem(EnumHand.MAIN_HAND, item.copy());
                    }
                }
            });
        } else {
            final float total = getTotal(items);
            actions.add(event -> {
                EntityLivingBase entityLiving = event.getEntityLiving();
                if (entityLiving != null) {
                    ItemStack item = getRandomItem(items, total);
                    if (entityLiving instanceof EntityEnderman) {
                        if (item.getItem() instanceof ItemBlock) {
                            ItemBlock b = (ItemBlock) item.getItem();
                            ((EntityEnderman) entityLiving).setHeldBlockState(b.getBlock().getStateFromMeta(b.getMetadata(item.getItemDamage())));
                        }
                    } else {
                        entityLiving.setHeldItem(EnumHand.MAIN_HAND, item.copy());
                    }
                }
            });
        }
    }

    private void addMobNBT(AttributeMap map) {
        String mobnbt = map.get(ACTION_MOBNBT);
        if (mobnbt != null) {
            NBTTagCompound tagCompound;
            try {
                tagCompound = JsonToNBT.getTagFromJson(mobnbt);
            } catch (NBTException e) {
                logger.log(Level.ERROR, "Bad NBT for mob!");
                return;
            }
            actions.add(event -> {
                EntityLivingBase entityLiving = event.getEntityLiving();
                entityLiving.readEntityFromNBT(tagCompound);
            });
        }
    }

    private void addAngryAction(AttributeMap map) {
        if (map.get(ACTION_ANGRY)) {
            actions.add(event -> {
                EntityLivingBase entityLiving = event.getEntityLiving();
                if (entityLiving instanceof EntityPigZombie) {
                    EntityPigZombie pigZombie = (EntityPigZombie) entityLiving;
                    EntityPlayer player = event.getWorld().getClosestPlayerToEntity(entityLiving, 50);
                    if (player != null) {
                        pigZombie.setRevengeTarget(player);
                    }
                } else if (entityLiving instanceof EntityLiving) {
                    EntityPlayer player = event.getWorld().getClosestPlayerToEntity(entityLiving, 50);
                    if (player != null) {
                        ((EntityLiving) entityLiving).setAttackTarget(player);
                    }
                }
            });
        }
    }


}
