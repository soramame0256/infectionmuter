package com.github.soramame0256.infectionmuter;

import com.github.soramame0256.infectionmuter.util.JsonUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import org.apache.commons.lang3.ArrayUtils;
import scala.util.parsing.json.JSON;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MuteCmd extends CommandBase {
    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public String getName() {
        return "mute";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/mute <mcid>";
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        List<String> ret = new ArrayList<>();
        String name;
        if(args.length == 0){
            name = "";
        }else{
            name = args[0];
        }
        if(Minecraft.getMinecraft().getConnection() != null) {
            for (NetworkPlayerInfo i : Minecraft.getMinecraft().getConnection().getPlayerInfoMap()) {
                if(i.getGameProfile().getName()!=null &&i.getGameProfile().getName().startsWith(name)) ret.add(i.getGameProfile().getName());
            }
        }
        return ret;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        JsonUtils ju = (JsonUtils) InfectionMuter.settings;
        if(!ju.getRoot().getAsJsonObject().has("muted")){
            ju.getRoot().getAsJsonObject().add("muted", new JsonArray());
        }
        JsonArray ja = ju.getRoot().getAsJsonObject().get("muted").getAsJsonArray();
        if (args.length == 1){
            JsonPrimitive jp = new JsonPrimitive(args[0]);
            if(!ja.contains(jp)){
                ja.add(args[0]);
                sender.sendMessage(new TextComponentString("§8[InfectionMuter]§7" + args[0] + "をミュートしました。"));
            }else{
                ja.remove(jp);
                sender.sendMessage(new TextComponentString("§8[InfectionMuter]§7" + args[0] + "のミュートを解除しました。"));
            }
            try {
                ju.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else{
            sender.sendMessage(new TextComponentString("§8---------[ミュート済みプレイヤーリスト]---------"));
            for(JsonElement j : ja) {
                sender.sendMessage(new TextComponentString("- §7" + j.getAsString()));
            }
        }
    }
}
