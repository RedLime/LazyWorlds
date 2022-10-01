package com.redlimerl.lazyworlds;

import com.google.common.collect.Lists;
import com.redlimerl.lazyworlds.mixin.AccessorLevelStorage;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelStorageException;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraft.world.level.storage.SessionLock;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Environment(EnvType.CLIENT)
public class LazyWorlds implements ClientModInitializer {

    private static ExecutorService LOAD_EXECUTOR = null;
    private static final CopyOnWriteArrayList<LevelSummary> LEVEL_SUMMARIES = new CopyOnWriteArrayList<>();
    private static int LATEST_WORLD_SIZE = 0;
    private static volatile int INIT_FIRST = 0;

    @Override
    public void onInitializeClient() {
        LOAD_EXECUTOR = Executors.newSingleThreadExecutor();
    }

    public static List<LevelSummary> getLevelList() throws LevelStorageException {
        LevelStorage levelStorage = MinecraftClient.getInstance().getLevelStorage();
        Path saveDirectory = levelStorage.getSavesDirectory();
        if (!Files.isDirectory(saveDirectory)) {
            throw new LevelStorageException(new TranslatableText("selectWorld.load_folder_access").getString());
        }
        String[] levelArray = saveDirectory.toFile().list();

        if (LOAD_EXECUTOR != null && levelArray != null) {
            int levelSize = levelArray.length;
            if (levelSize == 0) return Lists.newArrayList();

            if (levelSize != LATEST_WORLD_SIZE) {
                LEVEL_SUMMARIES.clear();

                File[] worlds = saveDirectory.toFile().listFiles();
                if (worlds == null) return Lists.newArrayList();
                Arrays.sort(worlds, (a, b) -> (int) (b.lastModified() - a.lastModified()));

                INIT_FIRST = 0;

                LOAD_EXECUTOR.submit(() -> {
                    AccessorLevelStorage accessorLevelStorage = (AccessorLevelStorage) levelStorage;

                    for (File worldDirectory : worlds) {
                        if (!worldDirectory.isDirectory()) continue;

                        boolean sessionLocked;
                        try {
                            sessionLocked = SessionLock.isLocked(worldDirectory.toPath());
                        }
                        catch (Exception exception) {
                            continue;
                        }

                        LevelSummary levelSummary = accessorLevelStorage.invokeReadLevelProperties(worldDirectory, accessorLevelStorage.getLevelSummary(worldDirectory, sessionLocked));
                        if (levelSummary == null) continue;
                        LEVEL_SUMMARIES.add(levelSummary);
                        //noinspection NonAtomicOperationOnVolatileField
                        INIT_FIRST++;
                    }
                    INIT_FIRST = 999;
                });
                //noinspection StatementWithEmptyBody
                while (INIT_FIRST < 2) {
                    // Empty
                }
            }
            LATEST_WORLD_SIZE = levelSize;

            return LEVEL_SUMMARIES;
        }

        return Lists.newArrayList();
    }

}
