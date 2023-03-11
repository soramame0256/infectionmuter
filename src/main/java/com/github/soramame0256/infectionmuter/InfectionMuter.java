package com.github.soramame0256.infectionmuter;

import com.github.soramame0256.infectionmuter.util.IDataUtil;
import com.github.soramame0256.infectionmuter.util.JsonUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mod(
        modid = InfectionMuter.MOD_ID,
        name = InfectionMuter.MOD_NAME,
        version = InfectionMuter.VERSION
)
public class InfectionMuter {

    public static final String MOD_ID = "infectionmuter";
    public static final String MOD_NAME = "InfectionMuter";
    public static final String VERSION = "1.0-SNAPSHOT";
    public static final Pattern CHAT_FORMAT = Pattern.compile("(\\[.*])?(?<mcid>.*) : .*");
    public static final String COLOR_CODE = "§[0-9a-fik-or]";
    public static final IDataUtil<JsonElement> settings;

    static {
        try {
            settings = new JsonUtils(MOD_ID + "/settings.json");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This is the instance of your mod as created by Forge. It will never be null.
     */
    @Mod.Instance(MOD_ID)
    public static InfectionMuter INSTANCE;

    /**
     * This is the first initialization event. Register tile entities here.
     * The registry events below will have fired prior to entry to this method.
     */
    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent event) {

    }

    /**
     * This is the second initialization event. Register custom recipes
     */
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        ClientCommandHandler.instance.registerCommand(new MuteCmd());
    }

    /**
     * This is the final initialization event. Register actions from other mods here
     */
    @Mod.EventHandler
    public void postinit(FMLPostInitializationEvent event) {

    }

    @SubscribeEvent
    public void onChatReceive(ClientChatReceivedEvent e){
        String msg = e.getMessage().getFormattedText().replaceAll(COLOR_CODE,"");
        Matcher m = CHAT_FORMAT.matcher(msg);
        if(m.find()){
            String id = m.group("mcid");
            if(settings.getRoot().getAsJsonObject().has("muted") && settings.getRoot().getAsJsonObject().get("muted").getAsJsonArray().contains(new JsonPrimitive(id))){
                System.out.println("muted message by " + id + "("+ msg+ ")");
                e.setCanceled(true);
            }
        }
    }

}
