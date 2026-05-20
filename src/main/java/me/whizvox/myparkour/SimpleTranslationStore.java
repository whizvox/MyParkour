package me.whizvox.myparkour;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.minimessage.translation.MiniMessageTranslator;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Locale;
import java.util.Map;

@NotNullByDefault
public class SimpleTranslationStore extends MiniMessageTranslator {

    private final Key key;
    private final Map<String, String> strings;

    public SimpleTranslationStore() {
        key = Key.key("myparkour:main");
        strings = new Object2ObjectOpenHashMap<>();
    }

    @Override
    protected @Nullable String getMiniMessageString(String key, Locale locale) {
        return strings.get(key);
    }

    @Override
    public Key name() {
        return key;
    }

    public void load(Path path) throws IOException {
        if (Files.exists(path)) {
            try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                JsonObject obj = MyParkour.inst().getGson().fromJson(reader, JsonObject.class);
                strings.clear();
                obj.keySet().forEach(key -> strings.put(key, obj.get(key).getAsString()));
            }
        } else {
            try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.CREATE)) {
                JsonObject obj = new JsonObject();
                Map<String, String> defaultMessages = Messages.getDefaultMessages();
                defaultMessages.keySet().stream().sorted().forEach(key -> obj.addProperty(key, defaultMessages.get(key)));
                MyParkour.inst().getGson().toJson(obj, writer);
                strings.clear();
                strings.putAll(defaultMessages);
            }
        }
    }

}
