package me.recursiveg;

import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;

@Mod(value = Mc122477Fix.MOD_ID, dist = Dist.CLIENT)
public final class Mc122477Fix {
    public static final String MOD_ID = "mc122477fix";

    public Mc122477Fix() {
    }

    @EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT)
    public static final class ClientEvents {
        private static long framesSinceScreenOpen = Long.MAX_VALUE;

        private ClientEvents() {
        }

        @SubscribeEvent
        public static void onScreenOpen(ScreenEvent.Opening event) {
            if (event.getNewScreen() instanceof ChatScreen
                    || event.getNewScreen() instanceof CreativeModeInventoryScreen) {
                framesSinceScreenOpen = 0;
            }
        }

        @SubscribeEvent
        public static void onKeyPressed(ScreenEvent.KeyPressed.Pre event) {
            if (framesSinceScreenOpen < 2) {
                event.setCanceled(true);
            }
        }

        @SubscribeEvent
        public static void onCharTyped(ScreenEvent.CharacterTyped.Pre event) {
            if (framesSinceScreenOpen < 2) {
                event.setCanceled(true);
            }
        }

        @SubscribeEvent
        public static void onPostRenderFrame(RenderFrameEvent.Post event) {
            if (framesSinceScreenOpen < 2) {
                framesSinceScreenOpen++;
            }
        }
    }
}
