package com.redlimerl.lazyworlds.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class LazyWorldsConfig {

    public static int REFRESH_INTERVAL = 500;
    public static int SYNC_LOAD_LEVELS = 2;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File configFile = FabricLoader.getInstance().getConfigDir().resolve("lazyworlds.json").toFile();

    public static void init() {

        if (!configFile.exists()) {
            save();
        }

        try {
            JsonObject jsonObject = GSON.fromJson(FileUtils.readFileToString(configFile, StandardCharsets.UTF_8), JsonObject.class);
            REFRESH_INTERVAL = jsonObject.get("refresh_interval").getAsInt();
            SYNC_LOAD_LEVELS = jsonObject.get("first_load_worlds").getAsInt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("refresh_interval", REFRESH_INTERVAL);
        jsonObject.addProperty("first_load_worlds", SYNC_LOAD_LEVELS);
        try {
            FileUtils.writeStringToFile(configFile, GSON.toJson(jsonObject), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
