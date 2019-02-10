package mcjty.tools.rules;

import mcjty.tools.typed.Key;
import mcjty.tools.typed.Type;

public interface CommonRuleKeys {

    // Inputs
    Key<Integer> MINTIME = Key.create(Type.INTEGER, "mintime");
    Key<Integer> MAXTIME = Key.create(Type.INTEGER, "maxtime");
    Key<Integer> MINLIGHT = Key.create(Type.INTEGER, "minlight");
    Key<Integer> MAXLIGHT = Key.create(Type.INTEGER, "maxlight");
    Key<Integer> MINHEIGHT = Key.create(Type.INTEGER, "minheight");
    Key<Integer> MAXHEIGHT = Key.create(Type.INTEGER, "maxheight");
    Key<Float> MINDIFFICULTY = Key.create(Type.FLOAT, "mindifficulty");
    Key<Float> MAXDIFFICULTY = Key.create(Type.FLOAT, "maxdifficulty");
    Key<Float> MINSPAWNDIST = Key.create(Type.FLOAT, "minspawndist");
    Key<Float> MAXSPAWNDIST = Key.create(Type.FLOAT, "maxspawndist");
    Key<Float> RANDOM = Key.create(Type.FLOAT, "random");
    Key<Boolean> SEESKY = Key.create(Type.BOOLEAN, "seesky");
    Key<String> WEATHER = Key.create(Type.STRING, "weather");
    Key<String> TEMPCATEGORY = Key.create(Type.STRING, "tempcategory");
    Key<String> DIFFICULTY = Key.create(Type.STRING, "difficulty");
    Key<String> BLOCK = Key.create(Type.STRING, "block");
    Key<String> BLOCKUP = Key.create(Type.STRING, "blockup");
    Key<String> BIOME = Key.create(Type.STRING, "biome");
    Key<String> BIOMETYPE = Key.create(Type.STRING, "biometype");
    Key<String> STRUCTURE = Key.create(Type.STRING, "structure");
    Key<Integer> DIMENSION = Key.create(Type.INTEGER, "dimension");

    Key<String> HELMET = Key.create(Type.JSON, "helmet");
    Key<String> CHESTPLATE = Key.create(Type.JSON, "chestplate");
    Key<String> LEGGINGS = Key.create(Type.JSON, "leggings");
    Key<String> BOOTS = Key.create(Type.JSON, "boots");
    Key<String> HELDITEM = Key.create(Type.JSON, "helditem");
    Key<String> OFFHANDITEM = Key.create(Type.JSON, "offhanditem");
    Key<String> BOTHHANDSITEM = Key.create(Type.JSON, "bothhandsitem");

    Key<Boolean> INCITY = Key.create(Type.BOOLEAN, "incity");
    Key<Boolean> INBUILDING = Key.create(Type.BOOLEAN, "inbuilding");
    Key<Boolean> INSTREET = Key.create(Type.BOOLEAN, "instreet");
    Key<Boolean> INSPHERE = Key.create(Type.BOOLEAN, "insphere");

    Key<String> GAMESTAGE = Key.create(Type.STRING, "gamestage");

    Key<Boolean> SUMMER = Key.create(Type.BOOLEAN, "summer");
    Key<Boolean> WINTER = Key.create(Type.BOOLEAN, "winter");
    Key<Boolean> SPRING = Key.create(Type.BOOLEAN, "spring");
    Key<Boolean> AUTUMN = Key.create(Type.BOOLEAN, "autumn");

    Key<String> AMULET = Key.create(Type.JSON, "amulet");
    Key<String> RING = Key.create(Type.JSON, "ring");
    Key<String> BELT = Key.create(Type.JSON, "belt");
    Key<String> TRINKET = Key.create(Type.JSON, "trinket");
    Key<String> HEAD = Key.create(Type.JSON, "head");
    Key<String> BODY = Key.create(Type.JSON, "body");
    Key<String> CHARM = Key.create(Type.JSON, "charm");

    // Outputs

    Key<String> ACTION_RESULT = Key.create(Type.STRING, "result");

    Key<Float> ACTION_HEALTHMULTIPLY = Key.create(Type.FLOAT, "healthmultiply");
    Key<Float> ACTION_HEALTHADD = Key.create(Type.FLOAT, "healthadd");
    Key<Float> ACTION_SPEEDMULTIPLY = Key.create(Type.FLOAT, "speedmultiply");
    Key<Float> ACTION_SPEEDADD = Key.create(Type.FLOAT, "speedadd");
    Key<Float> ACTION_DAMAGEMULTIPLY = Key.create(Type.FLOAT, "damagemultiply");
    Key<Float> ACTION_DAMAGEADD = Key.create(Type.FLOAT, "damageadd");
    Key<Float> ACTION_SIZEMULTIPLY = Key.create(Type.FLOAT, "sizemultiply");
    Key<Float> ACTION_SIZEADD = Key.create(Type.FLOAT, "sizeadd");

    Key<String> ACTION_POTION = Key.create(Type.STRING, "potion");
    Key<String> ACTION_HELDITEM = Key.create(Type.STRING, "helditem");
    Key<String> ACTION_ARMORCHEST = Key.create(Type.STRING, "armorchest");
    Key<String> ACTION_ARMORHELMET = Key.create(Type.STRING, "armorhelmet");
    Key<String> ACTION_ARMORLEGS = Key.create(Type.STRING, "armorlegs");
    Key<String> ACTION_ARMORBOOTS = Key.create(Type.STRING, "armorboots");
    Key<String> ACTION_MOBNBT = Key.create(Type.JSON, "nbt");
    Key<Boolean> ACTION_ANGRY = Key.create(Type.BOOLEAN, "angry");
    Key<String> ACTION_MESSAGE = Key.create(Type.STRING, "message");
    Key<String> ACTION_GIVE = Key.create(Type.STRING, "give");

    Key<String> ACTION_EXPLOSION = Key.create(Type.STRING, "explosion");
    Key<Integer> ACTION_FIRE = Key.create(Type.INTEGER, "fire");
    Key<Boolean> ACTION_CLEAR = Key.create(Type.BOOLEAN, "clear");
    Key<String> ACTION_DAMAGE = Key.create(Type.STRING, "damage");
}
