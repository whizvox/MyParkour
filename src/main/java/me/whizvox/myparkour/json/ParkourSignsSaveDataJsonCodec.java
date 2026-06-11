package me.whizvox.myparkour.json;

import com.google.gson.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.whizvox.myparkour.sign.ParkourSign;
import me.whizvox.myparkour.sign.ParkourSigns;
import me.whizvox.myparkour.util.BlockLocation;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;

public class ParkourSignsSaveDataJsonCodec implements JsonCodec<ParkourSigns.SaveData> {

    private ParkourSignsSaveDataJsonCodec() {}

    @Override
    public ParkourSigns.SaveData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonArray signsArr = json.getAsJsonArray();
        Map<BlockLocation, ParkourSign> signs = new Object2ObjectOpenHashMap<>();
        signsArr.forEach(signElem -> {
            JsonObject signObj = signElem.getAsJsonObject();
            BlockLocation location = context.deserialize(signObj.get("location"), BlockLocation.class);
            ParkourSign sign = context.deserialize(signObj.get("sign"), ParkourSign.class);
            signs.put(location, sign);
        });
        return new ParkourSigns.SaveData(Collections.unmodifiableMap(signs));
    }

    @Override
    public JsonElement serialize(ParkourSigns.SaveData src, Type typeOfSrc, JsonSerializationContext context) {
        JsonArray signsArr = new JsonArray();
        src.signs().forEach((location, parkourSign) -> {
            JsonObject signObj = new JsonObject();
            signObj.add("location", context.serialize(location));
            signObj.add("sign", context.serialize(parkourSign));
            signsArr.add(signObj);
        });
        return signsArr;
    }

    public static final ParkourSignsSaveDataJsonCodec INSTANCE = new ParkourSignsSaveDataJsonCodec();

}
