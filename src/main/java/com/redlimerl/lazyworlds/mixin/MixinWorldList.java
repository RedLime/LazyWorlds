package com.redlimerl.lazyworlds.mixin;

import com.redlimerl.lazyworlds.LazyWorlds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.screen.world.WorldListWidget;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelStorageException;
import net.minecraft.world.level.storage.LevelSummary;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Supplier;

@Mixin(WorldListWidget.class)
public abstract class MixinWorldList extends AlwaysSelectedEntryListWidget<WorldListWidget.Entry>  {

    @Shadow public abstract void filter(Supplier<String> supplier, boolean load);

    @Shadow @Final private SelectWorldScreen parent;
    private int levelSize = 0;
    private long latestRefresh = System.currentTimeMillis();
    private boolean refreshDone = false;

    public MixinWorldList(MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
        super(minecraftClient, i, j, k, l, m);
    }

    @Redirect(method = "filter", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/LevelStorage;getLevelList()Ljava/util/List;"))
    public List<LevelSummary> redirectList(LevelStorage instance) throws LevelStorageException {
        List<LevelSummary> levelSummaries = LazyWorlds.getLevelList();
        refreshDone = levelSummaries.size() == levelSize;
        levelSize = levelSummaries.size();
        return levelSummaries;
    }


    @Inject(method = "getScrollbarPositionX", at = @At("RETURN"))
    public void refreshLevelListAsync(CallbackInfoReturnable<Boolean> cir) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - latestRefresh > 500 && !refreshDone) {
            double scrollAmount = this.getScrollAmount();
            this.filter(() -> ((AccessorSelectWorldScreen) this.parent).getSearchBoxWidget().getText(), true);
            this.setScrollAmount(scrollAmount);
            latestRefresh = currentTime;
        }
    }
}
