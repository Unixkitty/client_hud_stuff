package com.unixkitty.client_hud_stuff;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ClientHUDStuff.MODID)
public class ClientHUDStuff
{
    // The MODID value here should match an entry in the META-INF/mods.toml file
    public static final String MODID = "client_hud_stuff";
    public static final String MODNAME = "Client HUD Stuff";

    public static final Logger LOG = LogManager.getLogger(MODNAME);

    public ClientHUDStuff()
    {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);

        LOG.warn("Testing testerino!");
    }
}
