package com.redlimerl.lazyworlds.mixin;

import com.mojang.datafixers.DataFixer;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelSummary;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.io.File;
import java.util.function.BiFunction;

@Mixin(LevelStorage.class)
public interface AccessorLevelStorage {

    @Invoker("readLevelProperties")
    <T> T invokeReadLevelProperties(File file, BiFunction<File, DataFixer, T> biFunction);

    @Invoker("method_29014")
    BiFunction<File, DataFixer, LevelSummary> getLevelSummary(File file, boolean bool);

}
