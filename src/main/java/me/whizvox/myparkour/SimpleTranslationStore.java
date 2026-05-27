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

    public String getPlainString(String key) {
        if (strings.containsKey(key)) {
            return strings.get(key);
        }
        return key;
    }

    @Override
    public Key name() {
        return key;
    }

    private void save(Path path) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.CREATE)) {
            JsonObject obj = new JsonObject();
            strings.keySet().stream().sorted().forEach(key -> obj.addProperty(key, strings.get(key)));
            MyParkour.inst().getGson().toJson(obj, writer);
        }
    }

    public void load(Path path) throws IOException {
        strings.clear();
        Map<String, String> defaultMessages = Messages.getDefaultMessages();
        if (Files.exists(path)) {
            try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                JsonObject obj = MyParkour.inst().getGson().fromJson(reader, JsonObject.class);
                strings.clear();
                obj.keySet().forEach(key -> strings.put(key, obj.get(key).getAsString()));
            }
            boolean missingAnyKeys = false;
            for (String key : defaultMessages.keySet()) {
                if (!strings.containsKey(key)) {
                    strings.put(key, defaultMessages.get(key));
                    missingAnyKeys = true;
                }
            }
            if (missingAnyKeys) {
                save(path);
            }
        } else {
            strings.putAll(defaultMessages);
            save(path);
        }
    }

}
