package com.mods.omnigears.client;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class OmniSound extends AbstractTickableSoundInstance {
    public static final Map<Integer, OmniSound> PLAYING_FOR = Collections.synchronizedMap(new HashMap<>());
    private final Player player;

    public OmniSound(Player player) {
        super(OmniSounds.JETPACK_SOUND.get(), SoundSource.PLAYERS, player.getRandom());
        this.player = player;
        this.looping = true;
        PLAYING_FOR.put(player.getId(), this);
    }

    public static boolean playing(int entityId) {
        return PLAYING_FOR.containsKey(entityId) && PLAYING_FOR.get(entityId) != null && !PLAYING_FOR.get(entityId).isStopped();
    }

    @Override
    public void tick() {
        var pos = this.player.position();

        this.x = (float) pos.x();
        this.y = (float) pos.y() - 10;
        this.z = (float) pos.z();

        if (!JetpackClientHandler.isFlying(this.player)) {
            synchronized (PLAYING_FOR) {
                PLAYING_FOR.remove(this.player.getId());
                this.stop();
            }
        }
    }
}
