package com.redlimerl.lazyworlds.mixin;

import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SelectWorldScreen.class)
public interface AccessorSelectWorldScreen {

    @Accessor("searchBox")
    TextFieldWidget getSearchBoxWidget();

}
