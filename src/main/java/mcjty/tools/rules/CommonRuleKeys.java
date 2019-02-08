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
    Key<String> BIOME = Key.create(Type.STRING, "biome");
    Key<String> BIOMETYPE = Key.create(Type.STRING, "biometype");
    Key<String> STRUCTURE = Key.create(Type.STRING, "structure");
    Key<Integer> DIMENSION = Key.create(Type.INTEGER, "dimension");

    Key<String> HELMET = Key.create(Type.STRING, "helmet");
    Key<String> CHESTPLATE = Key.create(Type.STRING, "chestplate");
    Key<String> LEGGINGS = Key.create(Type.STRING, "leggings");
    Key<String> BOOTS = Key.create(Type.STRING, "boots");
    Key<String> HELDITEM = Key.create(Type.STRING, "helditem");
    Key<String> OFFHANDITEM = Key.create(Type.STRING, "offhanditem");
    Key<String> BOTHHANDSITEM = Key.create(Type.STRING, "bothhandsitem");
}
