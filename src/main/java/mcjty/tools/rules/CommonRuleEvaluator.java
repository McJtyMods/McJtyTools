package mcjty.tools.rules;

import com.google.common.base.Optional;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.istack.internal.NotNull;
import mcjty.tools.cache.StructureCache;
import mcjty.tools.typed.AttributeMap;
import mcjty.tools.typed.Key;
import mcjty.tools.varia.Tools;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static mcjty.tools.rules.CommonRuleKeys.*;

public class CommonRuleEvaluator {

    protected final List<BiFunction<Event, IEventQuery, Boolean>> checks = new ArrayList<>();
    private final Logger logger;
    private final IModRuleCompatibilityLayer compatibility;

    public CommonRuleEvaluator(AttributeMap map, Logger logger, IModRuleCompatibilityLayer compatibility) {
        this.logger = logger;
        this.compatibility = compatibility;
        addChecks(map);
    }

    protected void addChecks(AttributeMap map) {
        if (map.has(RANDOM)) {
            addRandomCheck(map);
        }
        if (map.has(MINTIME)) {
            addMinTimeCheck(map);
        }
        if (map.has(MAXTIME)) {
            addMaxTimeCheck(map);
        }

        if (map.has(MINHEIGHT)) {
            addMinHeightCheck(map);
        }
        if (map.has(MAXHEIGHT)) {
            addMaxHeightCheck(map);
        }
        if (map.has(WEATHER)) {
            addWeatherCheck(map);
        }
        if (map.has(TEMPCATEGORY)) {
            addTempCategoryCheck(map);
        }
        if (map.has(DIFFICULTY)) {
            addDifficultyCheck(map);
        }
        if (map.has(DIMENSION)) {
            addDimensionCheck(map);
        }

        if (map.has(MINSPAWNDIST)) {
            addMinSpawnDistCheck(map);
        }
        if (map.has(MAXSPAWNDIST)) {
            addMaxSpawnDistCheck(map);
        }

        if (map.has(MINLIGHT)) {
            addMinLightCheck(map);
        }
        if (map.has(MAXLIGHT)) {
            addMaxLightCheck(map);
        }

        if (map.has(MINDIFFICULTY)) {
            addMinAdditionalDifficultyCheck(map);
        }
        if (map.has(MAXDIFFICULTY)) {
            addMaxAdditionalDifficultyCheck(map);
        }

        if (map.has(SEESKY)) {
            addSeeSkyCheck(map);
        }
        if (map.has(BLOCK)) {
            addBlocksCheck(map);
        }
        if (map.has(BIOME)) {
            addBiomesCheck(map);
        }
        if (map.has(BIOMETYPE)) {
            addBiomeTypesCheck(map);
        }
        if (map.has(HELMET)) {
            addHelmetCheck(map);
        }
        if (map.has(CHESTPLATE)) {
            addChestplateCheck(map);
        }
        if (map.has(LEGGINGS)) {
            addLeggingsCheck(map);
        }
        if (map.has(BOOTS)) {
            addBootsCheck(map);
        }
        if (map.has(HELDITEM)) {
            addHeldItemCheck(map);
        }
        if (map.has(OFFHANDITEM)) {
            addOffHandItemCheck(map);
        }
        if (map.has(BOTHHANDSITEM)) {
            addBothHandsItemCheck(map);
        }

        if (map.has(STRUCTURE)) {
            addStructureCheck(map);
        }

        if (map.has(SUMMER)) {
            if (compatibility.hasSereneSeasons()) {
                addSummerCheck(map);
            } else {
                logger.warn("Serene Seaons is missing: this test cannot work!");
            }
        }
        if (map.has(WINTER)) {
            if (compatibility.hasSereneSeasons()) {
                addWinterCheck(map);
            } else {
                logger.warn("Serene Seaons is missing: this test cannot work!");
            }
        }
        if (map.has(SPRING)) {
            if (compatibility.hasSereneSeasons()) {
                addSpringCheck(map);
            } else {
                logger.warn("Serene Seaons is missing: this test cannot work!");
            }
        }
        if (map.has(AUTUMN)) {
            if (compatibility.hasSereneSeasons()) {
                addAutumnCheck(map);
            } else {
                logger.warn("Serene Seaons is missing: this test cannot work!");
            }
        }
        if (map.has(GAMESTAGE)) {
            if (compatibility.hasGameStages()) {
                addGameStageCheck(map);
            } else {
                logger.warn("Game Stages is missing: the 'gamestage' test cannot work!");
            }
        }
        if (map.has(INCITY)) {
            if (compatibility.hasLostCities()) {
                addInCityCheck(map);
            } else {
                logger.warn("The Lost Cities is missing: the 'incity' test cannot work!");
            }
        }
        if (map.has(INSTREET)) {
            if (compatibility.hasLostCities()) {
                addInStreetCheck(map);
            } else {
                logger.warn("The Lost Cities is missing: the 'instreet' test cannot work!");
            }
        }
        if (map.has(INSPHERE)) {
            if (compatibility.hasLostCities()) {
                addInSphereCheck(map);
            } else {
                logger.warn("The Lost Cities is missing: the 'insphere' test cannot work!");
            }
        }
        if (map.has(INBUILDING)) {
            if (compatibility.hasLostCities()) {
                addInBuildingCheck(map);
            } else {
                logger.warn("The Lost Cities is missing: the 'inbuilding' test cannot work!");
            }
        }

        if (map.has(AMULET)) {
            if (compatibility.hasBaubles()) {
                addBaubleCheck(map, AMULET, compatibility::getAmuletSlots);
            } else {
                logger.warn("Baubles is missing: this test cannot work!");
            }
        }
        if (map.has(RING)) {
            if (compatibility.hasBaubles()) {
                addBaubleCheck(map, RING, compatibility::getRingSlots);
            } else {
                logger.warn("Baubles is missing: this test cannot work!");
            }
        }
        if (map.has(BELT)) {
            if (compatibility.hasBaubles()) {
                addBaubleCheck(map, BELT, compatibility::getBeltSlots);
            } else {
                logger.warn("Baubles is missing: this test cannot work!");
            }
        }
        if (map.has(TRINKET)) {
            if (compatibility.hasBaubles()) {
                addBaubleCheck(map, TRINKET, compatibility::getTrinketSlots);
            } else {
                logger.warn("Baubles is missing: this test cannot work!");
            }
        }
        if (map.has(HEAD)) {
            if (compatibility.hasBaubles()) {
                addBaubleCheck(map, HEAD, compatibility::getHeadSlots);
            } else {
                logger.warn("Baubles is missing: this test cannot work!");
            }
        }
        if (map.has(BODY)) {
            if (compatibility.hasBaubles()) {
                addBaubleCheck(map, BODY, compatibility::getBodySlots);
            } else {
                logger.warn("Baubles is missing: this test cannot work!");
            }
        }
        if (map.has(CHARM)) {
            if (compatibility.hasBaubles()) {
                addBaubleCheck(map, CHARM, compatibility::getCharmSlots);
            } else {
                logger.warn("Baubles is missing: this test cannot work!");
            }
        }
    }

    private static Random rnd = new Random();

    private void addRandomCheck(AttributeMap map) {
        final float r = map.get(RANDOM);
        checks.add((event,query) -> rnd.nextFloat() < r);
    }

    private void addSeeSkyCheck(AttributeMap map) {
        if (map.get(SEESKY)) {
            checks.add((event,query) -> query.getWorld(event).canBlockSeeSky(query.getPos(event)));
        } else {
            checks.add((event,query) -> !query.getWorld(event).canBlockSeeSky(query.getPos(event)));
        }
    }

    private void addDimensionCheck(AttributeMap map) {
        List<Integer> dimensions = map.getList(DIMENSION);
        if (dimensions.size() == 1) {
            Integer dim = dimensions.get(0);
            checks.add((event,query) -> query.getWorld(event).provider.getDimension() == dim);
        } else {
            Set<Integer> dims = new HashSet<>(dimensions);
            checks.add((event,query) -> dims.contains(query.getWorld(event).provider.getDimension()));
        }
    }

    private void addDifficultyCheck(AttributeMap map) {
        String difficulty = map.get(DIFFICULTY).toLowerCase();
        EnumDifficulty diff = null;
        for (EnumDifficulty d : EnumDifficulty.values()) {
            if (d.getDifficultyResourceKey().endsWith("." + difficulty)) {
                diff = d;
                break;
            }
        }
        if (diff != null) {
            EnumDifficulty finalDiff = diff;
            checks.add((event,query) -> query.getWorld(event).getDifficulty() == finalDiff);
        } else {
            logger.log(Level.ERROR, "Unknown difficulty '" + difficulty + "'! Use one of 'easy', 'normal', 'hard',  or 'peaceful'");
        }
    }

    private void addWeatherCheck(AttributeMap map) {
        String weather = map.get(WEATHER);
        boolean raining = weather.toLowerCase().startsWith("rain");
        boolean thunder = weather.toLowerCase().startsWith("thunder");
        if (raining) {
            checks.add((event,query) -> query.getWorld(event).isRaining());
        } else if (thunder) {
            checks.add((event,query) -> query.getWorld(event).isThundering());
        } else {
            logger.log(Level.ERROR, "Unknown weather '" + weather + "'! Use 'rain' or 'thunder'");
        }
    }

    private void addTempCategoryCheck(AttributeMap map) {
        String tempcategory = map.get(TEMPCATEGORY).toLowerCase();
        Biome.TempCategory cat = null;
        if ("cold".equals(tempcategory)) {
            cat = Biome.TempCategory.COLD;
        } else if ("medium".equals(tempcategory)) {
            cat = Biome.TempCategory.MEDIUM;
        } else if ("warm".equals(tempcategory)) {
            cat = Biome.TempCategory.WARM;
        } else if ("ocean".equals(tempcategory)) {
            cat = Biome.TempCategory.OCEAN;
        } else {
            logger.log(Level.ERROR, "Unknown tempcategory '" + tempcategory + "'! Use one of 'cold', 'medium', 'warm',  or 'ocean'");
            return;
        }

        Biome.TempCategory finalCat = cat;
        checks.add((event,query) -> {
            Biome biome = query.getWorld(event).getBiome(query.getPos(event));
            return biome.getTempCategory() == finalCat;
        });
    }

    private void addStructureCheck(AttributeMap map) {
        String structure = map.get(STRUCTURE);
        checks.add((event,query) -> StructureCache.CACHE.isInStructure(query.getWorld(event), structure, query.getPos(event)));
    }

    private void addBiomesCheck(AttributeMap map) {
        List<String> biomes = map.getList(BIOME);
        if (biomes.size() == 1) {
            String biomename = biomes.get(0);
            checks.add((event,query) -> {
                Biome biome = query.getWorld(event).getBiome(query.getPos(event));
                return biomename.equals(compatibility.getBiomeName(biome));
            });
        } else {
            Set<String> biomenames = new HashSet<>(biomes);
            checks.add((event,query) -> {
                Biome biome = query.getWorld(event).getBiome(query.getPos(event));
                return biomenames.contains(compatibility.getBiomeName(biome));
            });
        }
    }

    private void addBiomeTypesCheck(AttributeMap map) {
        List<String> biomeTypes = map.getList(BIOMETYPE);
        if (biomeTypes.size() == 1) {
            String biometype = biomeTypes.get(0);
            BiomeDictionary.Type type = BiomeDictionary.Type.getType(biometype);
            checks.add((event,query) -> {
                Biome biome = query.getWorld(event).getBiome(query.getPos(event));
                return BiomeDictionary.getTypes(biome).contains(type);
            });
        } else {
            Set<BiomeDictionary.Type> types = new HashSet<>();
            for (String s : biomeTypes) {
                types.add(BiomeDictionary.Type.getType(s));
            }

            checks.add((event,query) -> {
                Biome biome = query.getWorld(event).getBiome(query.getPos(event));
                return BiomeDictionary.getTypes(biome).stream().anyMatch(s -> types.contains(s));
            });
        }
    }

    private static final int[] EMPTYINTS = new int[0];

    private <T extends Comparable<T>> IBlockState set(IBlockState state, IProperty<T> property, String value) {
        Optional<T> optionalValue = property.parseValue(value);
        if (optionalValue.isPresent()) {
            return state.withProperty(property, optionalValue.get());
        } else {
            return state;
        }
    }

    @NotNull
    private BiFunction<Event, IEventQuery, BlockPos> parseOffset(String json) {
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(json);
        JsonObject obj = element.getAsJsonObject();
        if (obj.has("offset")) {
            JsonObject offset = obj.getAsJsonObject("offset");
            int offsetX = offset.get("x").getAsInt();
            int offsetY = offset.get("y").getAsInt();
            int offsetZ = offset.get("z").getAsInt();
            return (event, query) -> query.getValidBlockPos(event).add(offsetX, offsetY, offsetZ);
        }
        return null;
    }

    @Nullable
    private Predicate<IBlockState> parseBlock(String json) {
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(json);
        if (element.isJsonPrimitive()) {
            String blockname = element.getAsString();
            if (blockname.startsWith("ore:")) {
                int oreId = OreDictionary.getOreID(blockname.substring(4));
                return state -> isMatchingOreDict(oreId, state.getBlock());
            } else {
                Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockname));
                if (block == null) {
                    logger.log(Level.ERROR, "Block '" + blockname + "' is not valid!");
                    return null;
                }
                return state -> state.getBlock() == block;
            }
        } else if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();
            if (obj.has("ore")) {
                int oreId = OreDictionary.getOreID(obj.get("ore").getAsString());
                return state -> isMatchingOreDict(oreId, state.getBlock());
            } else if (obj.has("block")) {
                String blockname = obj.get("block").getAsString();
                Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockname));
                if (block == null) {
                    logger.log(Level.ERROR, "Block '" + blockname + "' is not valid!");
                    return null;
                }
                if (obj.has("properties")) {
                    IBlockState blockState = block.getDefaultState();
                    JsonArray propArray = obj.get("properties").getAsJsonArray();
                    for (JsonElement el : propArray) {
                        JsonObject propObj = el.getAsJsonObject();
                        String name = propObj.get("name").getAsString();
                        String value = propObj.get("value").getAsString();
                        for (IProperty<?> key : blockState.getPropertyKeys()) {
                            if (name.equals(key.getName())) {
                                blockState = set(blockState, key, value);
                            }
                        }
                    }
                    IBlockState finalBlockState = blockState;
                    return state -> state == finalBlockState;
                } else {
                    return state -> state.getBlock() == block;
                }
            }
        } else {
            logger.log(Level.ERROR, "Block description '" + json + "' is not valid!");
        }
        return null;
    }

    private boolean isMatchingOreDict(int oreId, Block block) {
        ItemStack stack = new ItemStack(block);
        int[] oreIDs = stack.isEmpty() ? EMPTYINTS : OreDictionary.getOreIDs(stack);
        return isMatchingOreId(oreIDs, oreId);
    }

    private void addBlocksCheck(AttributeMap map) {
        BiFunction<Event, IEventQuery, BlockPos> posFunction;
        if (map.has(BLOCKOFFSET)) {
            String json = map.get(BLOCKOFFSET);
            posFunction = parseOffset(json);
        } else {
            posFunction = (event, query) -> query.getValidBlockPos(event);
        }

        List<String> blocks = map.getList(BLOCK);
        if (blocks.size() == 1) {
            String json = blocks.get(0);
            Predicate<IBlockState> blockMatcher = parseBlock(json);
            if (blockMatcher != null) {
                checks.add((event, query) -> {
                    BlockPos pos = posFunction.apply(event, query);
                    IBlockState state = query.getWorld(event).getBlockState(pos);
                    return blockMatcher.test(state);
                });
            }
        } else {
            List<Predicate<IBlockState>> blockMatchers = new ArrayList<>();
            for (String block : blocks) {
                Predicate<IBlockState> blockMatcher = parseBlock(block);
                if (blockMatcher == null) {
                    return;
                }
                blockMatchers.add(blockMatcher);
            }

            checks.add((event,query) -> {
                BlockPos pos = posFunction.apply(event, query);
                IBlockState state = query.getWorld(event).getBlockState(pos);
                for (Predicate<IBlockState> matcher : blockMatchers) {
                    if (matcher.test(state)) {
                        return true;
                    }
                }
                return false;
            });
        }
    }

    private boolean isMatchingOreId(int[] oreIDs, int oreId) {
        if (oreIDs.length > 0) {
            for (int id : oreIDs) {
                if (id == oreId) {
                    return true;
                }
            }
        }
        return false;
    }


    private void addMinTimeCheck(AttributeMap map) {
        final int mintime = map.get(MINTIME);
        checks.add((event,query) -> {
            int time = (int) query.getWorld(event).getWorldTime();
            return time >= mintime;
        });
    }

    private void addMaxTimeCheck(AttributeMap map) {
        final int maxtime = map.get(MAXTIME);
        checks.add((event,query) -> {
            int time = (int) query.getWorld(event).getWorldTime();
            return time <= maxtime;
        });
    }

    private void addMinSpawnDistCheck(AttributeMap map) {
        final Float d = map.get(MINSPAWNDIST) * map.get(MINSPAWNDIST);
        checks.add((event,query) -> {
            BlockPos pos = query.getPos(event);
            double sqdist = pos.distanceSq(query.getWorld(event).getSpawnPoint());
            return sqdist >= d;
        });
    }

    private void addMaxSpawnDistCheck(AttributeMap map) {
        final Float d = map.get(MAXSPAWNDIST) * map.get(MAXSPAWNDIST);
        checks.add((event,query) -> {
            BlockPos pos = query.getPos(event);
            double sqdist = pos.distanceSq(query.getWorld(event).getSpawnPoint());
            return sqdist <= d;
        });
    }


    private void addMinLightCheck(AttributeMap map) {
        final int minlight = map.get(MINLIGHT);
        checks.add((event,query) -> {
            BlockPos pos = query.getPos(event);
            return query.getWorld(event).getLight(pos, true) >= minlight;
        });
    }

    private void addMaxLightCheck(AttributeMap map) {
        final int maxlight = map.get(MAXLIGHT);
        checks.add((event,query) -> {
            BlockPos pos = query.getPos(event);
            return query.getWorld(event).getLight(pos, true) <= maxlight;
        });
    }

    private void addMinAdditionalDifficultyCheck(AttributeMap map) {
        final Float mindifficulty = map.get(MINDIFFICULTY);
        checks.add((event,query) -> query.getWorld(event).getDifficultyForLocation(query.getPos(event)).getAdditionalDifficulty() >= mindifficulty);
    }

    private void addMaxAdditionalDifficultyCheck(AttributeMap map) {
        final Float maxdifficulty = map.get(MAXDIFFICULTY);
        checks.add((event,query) -> query.getWorld(event).getDifficultyForLocation(query.getPos(event)).getAdditionalDifficulty() <= maxdifficulty);
    }

    private void addMaxHeightCheck(AttributeMap map) {
        final int maxheight = map.get(MAXHEIGHT);
        checks.add((event,query) -> query.getY(event) <= maxheight);
    }

    private void addMinHeightCheck(AttributeMap map) {
        final int minheight = map.get(MINHEIGHT);
        checks.add((event,query) -> query.getY(event) >= minheight);
    }


    public boolean match(Event event, IEventQuery query) {
        for (BiFunction<Event, IEventQuery, Boolean> rule : checks) {
            if (!rule.apply(event, query)) {
                return false;
            }
        }
        return true;
    }

    private Predicate<Integer> getExpression(String expression) {
        try {
            if (expression.startsWith(">=")) {
                int amount = Integer.parseInt(expression.substring(2));
                return i -> i >= amount;
            }
            if (expression.startsWith(">")) {
                int amount = Integer.parseInt(expression.substring(1));
                return i -> i > amount;
            }
            if (expression.startsWith("<=")) {
                int amount = Integer.parseInt(expression.substring(2));
                return i -> i <= amount;
            }
            if (expression.startsWith("<")) {
                int amount = Integer.parseInt(expression.substring(1));
                return i -> i < amount;
            }
            if (expression.startsWith("=")) {
                int amount = Integer.parseInt(expression.substring(1));
                return i -> i == amount;
            }
            if (expression.startsWith("!=") || expression.startsWith("<>")) {
                int amount = Integer.parseInt(expression.substring(2));
                return i -> i != amount;
            }

            if (expression.contains("-")) {
                String[] split = StringUtils.split(expression, "-");
                int amount1 = Integer.parseInt(split[0]);
                int amount2 = Integer.parseInt(split[1]);
                return i -> i >= amount1 && i <= amount2;
            }

            int amount = Integer.parseInt(expression);
            return i -> i == amount;
        } catch (NumberFormatException e) {
            logger.log(Level.ERROR, "Bad expression '" + expression + "'!");
            return null;
        }
    }

    private Predicate<Integer> getExpression(JsonElement element) {
        if (element.isJsonPrimitive()) {
            if (element.getAsJsonPrimitive().isNumber()) {
                int amount = element.getAsInt();
                return i -> i == amount;
            } else {
                return getExpression(element.getAsString());
            }
        } else {
            logger.log(Level.ERROR, "Bad expression!");
            return null;
        }
    }

    private Predicate<ItemStack> getMatcher(String name) {
        ItemStack stack = Tools.parseStack(name, logger);
        if (!stack.isEmpty()) {
            // Stack matching
            if (name.contains("/") && name.contains("@")) {
                return s -> ItemStack.areItemsEqual(s, stack) && ItemStack.areItemStackTagsEqual(s, stack);
            } else if (name.contains("/")) {
                return s -> ItemStack.areItemsEqualIgnoreDurability(s, stack) && ItemStack.areItemStackTagsEqual(s, stack);
            } else if (name.contains("@")) {
                return s -> ItemStack.areItemsEqual(s, stack);
            } else {
                return s -> s.getItem() == stack.getItem();
            }
        }
        return null;
    }

    private Predicate<ItemStack> getMatcher(JsonObject obj) {
        String name = obj.get("item").getAsString();
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(name));
        if (item == null) {
            logger.log(Level.ERROR, "Unknown item '" + name + "'!");
            return null;
        }
        if (obj.has("damage") && obj.has("nbt")) {
            Predicate<Integer> damage = getExpression(obj.get("damage"));
            if (damage == null) {
                return null;
            }
            List<Predicate<NBTTagCompound>> nbtMatchers = getNbtMatchers(obj);
            if (nbtMatchers == null) return null;
            return s -> s.getItem() == item && damage.test(s.getItemDamage()) && nbtMatchers.stream().allMatch(p -> p.test(s.getTagCompound()));
        } else if (obj.has("damage")) {
            Predicate<Integer> damage = getExpression(obj.get("damage"));
            if (damage == null) {
                return null;
            }
            return s -> s.getItem() == item && damage.test(s.getItemDamage());
        } else if (obj.has("nbt")) {
            List<Predicate<NBTTagCompound>> nbtMatchers = getNbtMatchers(obj);
            if (nbtMatchers == null) return null;
            return s -> s.getItem() == item && nbtMatchers.stream().allMatch(p -> p.test(s.getTagCompound()));
        } else {
            return s -> s.getItem() == item;
        }
    }

    private List<Predicate<NBTTagCompound>> getNbtMatchers(JsonObject obj) {
        JsonArray nbtArray = obj.getAsJsonArray("nbt");
        return getNbtMatchers(nbtArray);
    }

    private List<Predicate<NBTTagCompound>> getNbtMatchers(JsonArray nbtArray) {
        List<Predicate<NBTTagCompound>> nbtMatchers = new ArrayList<>();
        for (JsonElement element : nbtArray) {
            JsonObject o = element.getAsJsonObject();
            String tag = o.get("tag").getAsString();
            if (o.has("contains")) {
                List<Predicate<NBTTagCompound>> subMatchers = getNbtMatchers(o.getAsJsonArray("contains"));
                nbtMatchers.add(tagCompound -> {
                    if (tagCompound != null) {
                        NBTTagList list = tagCompound.getTagList(tag, Constants.NBT.TAG_COMPOUND);
                        for (NBTBase base : list) {
                            for (Predicate<NBTTagCompound> matcher : subMatchers) {
                                if (matcher.test((NBTTagCompound) base)) {
                                    return true;
                                }
                            }
                        }
                    }
                    return false;
                });
            } else {
                Predicate<Integer> nbt = getExpression(o.get("value"));
                if (nbt == null) {
                    return null;
                }
                nbtMatchers.add(tagCompound -> nbt.test(tagCompound.getInteger(tag)));
            }

        }
        return nbtMatchers;
    }


    protected List<Predicate<ItemStack>> getItems(List<String> itemNames) {
        List<Predicate<ItemStack>> items = new ArrayList<>();
        for (String json : itemNames) {
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(json);
            if (element.isJsonPrimitive()) {
                String name = element.getAsString();
                Predicate<ItemStack> matcher = getMatcher(name);
                if (matcher != null) {
                    items.add(matcher);
                }
            } else if (element.isJsonObject()) {
                JsonObject obj = element.getAsJsonObject();
                Predicate<ItemStack> matcher = getMatcher(obj);
                if (matcher != null) {
                    items.add(matcher);
                }
            } else {
                logger.log(Level.ERROR, "Item description '" + json + "' is not valid!");
            }
        }
        return items;
    }

    public void addHelmetCheck(AttributeMap map) {
        List<Predicate<ItemStack>> items = getItems(map.getList(HELMET));
        addArmorCheck(items, EntityEquipmentSlot.HEAD);
    }

    public void addChestplateCheck(AttributeMap map) {
        List<Predicate<ItemStack>> items = getItems(map.getList(CHESTPLATE));
        addArmorCheck(items, EntityEquipmentSlot.CHEST);
    }

    public void addLeggingsCheck(AttributeMap map) {
        List<Predicate<ItemStack>> items = getItems(map.getList(LEGGINGS));
        addArmorCheck(items, EntityEquipmentSlot.LEGS);
    }

    public void addBootsCheck(AttributeMap map) {
        List<Predicate<ItemStack>> items = getItems(map.getList(BOOTS));
        addArmorCheck(items, EntityEquipmentSlot.FEET);
    }

    private void addArmorCheck(List<Predicate<ItemStack>> items, EntityEquipmentSlot slot) {
        checks.add((event,query) -> {
            EntityPlayer player = query.getPlayer(event);
            if (player != null) {
                ItemStack armorItem = player.getItemStackFromSlot(slot);
                if (!armorItem.isEmpty()) {
                    for (Predicate<ItemStack> item : items) {
                        if (item.test(armorItem)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        });
    }

    public void addHeldItemCheck(AttributeMap map) {
        List<Predicate<ItemStack>> items = getItems(map.getList(HELDITEM));
        checks.add((event,query) -> {
            EntityPlayer player = query.getPlayer(event);
            if (player != null) {
                ItemStack mainhand = player.getHeldItemMainhand();
                if (!mainhand.isEmpty()) {
                    for (Predicate<ItemStack> item : items) {
                        if (item.test(mainhand)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        });
    }

    public void addOffHandItemCheck(AttributeMap map) {
        List<Predicate<ItemStack>> items = getItems(map.getList(OFFHANDITEM));
        checks.add((event,query) -> {
            EntityPlayer player = query.getPlayer(event);
            if (player != null) {
                ItemStack offhand = player.getHeldItemOffhand();
                if (!offhand.isEmpty()) {
                    for (Predicate<ItemStack> item : items) {
                        if (item.test(offhand)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        });
    }

    public void addBothHandsItemCheck(AttributeMap map) {
        List<Predicate<ItemStack>> items = getItems(map.getList(BOTHHANDSITEM));
        checks.add((event,query) -> {
            EntityPlayer player = query.getPlayer(event);
            if (player != null) {
                ItemStack offhand = player.getHeldItemOffhand();
                if (!offhand.isEmpty()) {
                    for (Predicate<ItemStack> item : items) {
                        if (item.test(offhand)) {
                            return true;
                        }
                    }
                }
                ItemStack mainhand = player.getHeldItemMainhand();
                if (!mainhand.isEmpty()) {
                    for (Predicate<ItemStack> item : items) {
                        if (item.test(mainhand)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        });
    }


    private void addSummerCheck(AttributeMap map) {
        Boolean s = map.get(SUMMER);
        checks.add((event, query) -> s == compatibility.isSummer(query.getWorld(event)));
    }

    private void addWinterCheck(AttributeMap map) {
        Boolean s = map.get(WINTER);
        checks.add((event, query) -> s == compatibility.isWinter(query.getWorld(event)));
    }

    private void addSpringCheck(AttributeMap map) {
        Boolean s = map.get(SPRING);
        checks.add((event, query) -> s == compatibility.isSpring(query.getWorld(event)));
    }

    private void addAutumnCheck(AttributeMap map) {
        Boolean s = map.get(AUTUMN);
        checks.add((event, query) -> s == compatibility.isAutumn(query.getWorld(event)));
    }

    private void addGameStageCheck(AttributeMap map) {
        String stage = map.get(GAMESTAGE);
        checks.add((event, query) -> compatibility.hasGameStage(query.getPlayer(event), stage));
    }

    private void addInCityCheck(AttributeMap map) {
        if (map.get(INCITY)) {
            checks.add((event,query) -> compatibility.isCity(query, event));
        } else {
            checks.add((event,query) -> !compatibility.isCity(query, event));
        }
    }

    private void addInStreetCheck(AttributeMap map) {
        if (map.get(INSTREET)) {
            checks.add((event,query) -> compatibility.isStreet(query, event));
        } else {
            checks.add((event,query) -> !compatibility.isStreet(query, event));
        }
    }

    private void addInSphereCheck(AttributeMap map) {
        if (map.get(INSPHERE)) {
            checks.add((event,query) -> compatibility.inSphere(query, event));
        } else {
            checks.add((event,query) -> !compatibility.inSphere(query, event));
        }
    }

    private void addInBuildingCheck(AttributeMap map) {
        if (map.get(INBUILDING)) {
            checks.add((event,query) -> compatibility.isBuilding(query, event));
        } else {
            checks.add((event,query) -> !compatibility.isBuilding(query, event));
        }
    }

    public void addBaubleCheck(AttributeMap map, Key<String> key, Supplier<int[]> slotSupplier) {
        List<Predicate<ItemStack>> items = getItems(map.getList(key));
        checks.add((event,query) -> {
            EntityPlayer player = query.getPlayer(event);
            if (player != null) {
                for (int slot : slotSupplier.get()) {
                    ItemStack stack = compatibility.getBaubleStack(player, slot);
                    if (!stack.isEmpty()) {
                        for (Predicate<ItemStack> item : items) {
                            if (item.test(stack)) {
                                return true;
                            }
                        }
                    }
                }
            }
            return false;
        });
    }
}
