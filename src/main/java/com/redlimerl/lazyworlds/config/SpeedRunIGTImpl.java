package com.redlimerl.lazyworlds.config;

import com.google.common.collect.Lists;
import com.redlimerl.speedrunigt.api.OptionButtonFactory;
import com.redlimerl.speedrunigt.api.SpeedRunIGTApi;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.LiteralText;

import java.util.ArrayList;
import java.util.Collection;

public class SpeedRunIGTImpl implements SpeedRunIGTApi {

    @Override
    public Collection<OptionButtonFactory> createOptionButtons() {
        ArrayList<OptionButtonFactory> optionButtonFactories = Lists.newArrayList();

        optionButtonFactories.add(
                screen -> new OptionButtonFactory.Builder()
                        .setButtonWidget(new SliderWidget(0, 0, 150, 20, new LiteralText("First Load Worlds : " + LazyWorldsConfig.SYNC_LOAD_LEVELS), (LazyWorldsConfig.SYNC_LOAD_LEVELS - 1) / 9.0) {
                            @Override
                            protected void updateMessage() {
                                setMessage(new LiteralText("First Load Worlds : " + LazyWorldsConfig.SYNC_LOAD_LEVELS));
                            }

                            @Override
                            protected void applyValue() {
                                LazyWorldsConfig.SYNC_LOAD_LEVELS = (int) (this.value * 9) + 1;
                            }

                            @Override
                            public void onRelease(double mouseX, double mouseY) {
                                super.onRelease(mouseX, mouseY);
                                LazyWorldsConfig.save();
                            }
                        })
                        .setCategory("LazyWorlds")
        );

        optionButtonFactories.add(
                screen -> new OptionButtonFactory.Builder()
                        .setButtonWidget(new SliderWidget(0, 0, 150, 20, new LiteralText("Refresh Interval : " + LazyWorldsConfig.REFRESH_INTERVAL + "ms"), (LazyWorldsConfig.REFRESH_INTERVAL - 100) / 4900.0) {
                            @Override
                            protected void updateMessage() {
                                setMessage(new LiteralText("Refresh Interval : " + LazyWorldsConfig.REFRESH_INTERVAL + "ms"));
                            }

                            @Override
                            protected void applyValue() {
                                LazyWorldsConfig.REFRESH_INTERVAL = ((int) (this.value * 4900) / 50) * 50 + 100;
                            }

                            @Override
                            public void onRelease(double mouseX, double mouseY) {
                                super.onRelease(mouseX, mouseY);
                                LazyWorldsConfig.save();
                            }
                        })
                        .setCategory("LazyWorlds")
        );

        return optionButtonFactories;
    }
}
