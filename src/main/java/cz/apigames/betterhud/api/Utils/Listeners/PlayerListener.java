package cz.apigames.betterhud.api.Utils.Listeners;

import cz.apigames.betterhud.api.BetterHudAPI;
import cz.apigames.betterhud.api.Displays.Display;
import cz.apigames.betterhud.api.Utils.MessageUtils;
import cz.apigames.betterhud.api.Utils.Placeholder;
import cz.apigames.betterhud.api.Utils.ToggleEvent;
import net.md_5.bungee.api.chat.hover.content.Entity;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Level;

public class PlayerListener implements Listener {

    @EventHandler
    public void PlayerLeave(PlayerQuitEvent event) {

        Display.getDisplays(event.getPlayer()).forEach(Display::destroy);

    }

    @EventHandler
    public void PlayerJoin(PlayerJoinEvent event) {

        BetterHudAPI.getLoadedHuds().forEach(hud -> {

            Optional<ToggleEvent> optEvent = hud.getEvents().stream().filter(toggleEvent -> toggleEvent.getEventType().equals(ToggleEvent.EventType.PLAYER_JOIN)).findFirst();
            optEvent.ifPresent(toggleEvent ->
            {
                try {
                    hud.renderFor(event.getPlayer(), toggleEvent.getDisplayType(), toggleEvent.getHideAfter(), false);
                } catch (IllegalStateException e) {
                    Bukkit.getLogger().log(Level.WARNING, "&cFailed to pass event: JOIN ("+event.getPlayer().getName()+")");
                    Bukkit.getLogger().log(Level.WARNING, "&cCan't display this hud, because some other hud is already displayed via this display! There should be only one hud per one join event, check your configuration.");
                } catch (IllegalArgumentException e) {
                    Bukkit.getLogger().log(Level.WARNING, "&cFailed to pass event: JOIN ("+event.getPlayer().getName()+")");
                    Bukkit.getLogger().log(Level.WARNING, "&cCan't display this hud, because display argument is not valid! Please, check your configuration.");
                }

            });

        });

    }

    @EventHandler
    public void PlayerDamageBySelf(EntityDamageEvent event) {

        if(event.getEntity() instanceof HumanEntity) {

            EntityDamageEvent.DamageCause[] invalid = {EntityDamageEvent.DamageCause.ENTITY_ATTACK, EntityDamageEvent.DamageCause.ENTITY_EXPLOSION, EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK};

            if(Arrays.stream(invalid).noneMatch(damageCause -> damageCause.equals(event.getCause()))) {

                BetterHudAPI.getLoadedHuds().forEach(hud -> {

                    if(Display.getDisplays((Player) event.getEntity()).stream().noneMatch(display -> display.getHud().equals(hud))) {

                        Optional<ToggleEvent> optEvent = hud.getEvents().stream().filter(toggleEvent -> toggleEvent.getEventType().equals(ToggleEvent.EventType.DAMAGE_OTHER)).findFirst();
                        optEvent.ifPresent(toggleEvent -> {
                            try {

                                //PLACEHOLDERS
                                BetterHudAPI.clearPlaceholders((Player) event.getEntity());
                                BetterHudAPI.setPlaceholders((Player) event.getEntity(), Arrays.asList(
                                        new Placeholder("{damage}", String.valueOf(event.getDamage())),
                                        new Placeholder("{damage_formatted}", String.valueOf(Math.round(event.getDamage()))),
                                        new Placeholder("{cause}", MessageUtils.enumToName(event.getCause().name()))
                                ));

                                hud.renderFor((Player) event.getEntity(), toggleEvent.getDisplayType(), toggleEvent.getHideAfter(), true);
                            } catch (IllegalArgumentException e) {
                                Bukkit.getLogger().log(Level.WARNING, "&cFailed to pass event: DAMAGE_OTHER ("+event.getEntity().getName()+")");
                                Bukkit.getLogger().log(Level.WARNING, "&cCan't display this hud, because display argument is not valid! Please, check your configuration.");
                            }
                        });

                    }

                });

            }

        }

    }

    @EventHandler
    public void PlayerDamageByOther(EntityDamageByEntityEvent event) {

        if(event.getEntity() instanceof HumanEntity) {

            //BY PLAYER
            if(event.getDamager() instanceof HumanEntity) {

                BetterHudAPI.getLoadedHuds().forEach(hud -> {

                    if(Display.getDisplays((Player) event.getEntity()).stream().noneMatch(display -> display.getHud().equals(hud))) {

                        Optional<ToggleEvent> optEvent = hud.getEvents().stream().filter(toggleEvent -> toggleEvent.getEventType().equals(ToggleEvent.EventType.DAMAGE_BY_PLAYER)).findFirst();
                        optEvent.ifPresent(toggleEvent -> {
                            try {

                                if(event.getDamager() instanceof Projectile) {

                                    Projectile projectile = (Projectile) event.getDamager();

                                    if(projectile.getShooter() != null) {
                                        //PLACEHOLDERS (SHOOTER)
                                        BetterHudAPI.clearPlaceholders((Player) event.getEntity());
                                        BetterHudAPI.setPlaceholders((Player) event.getEntity(), Arrays.asList(
                                                new Placeholder("{damage}", String.valueOf(event.getDamage())),
                                                new Placeholder("{damage_formatted}", String.valueOf(Math.round(event.getDamage()))),
                                                new Placeholder("{attacker}", ((LivingEntity) projectile.getShooter()).getName()),
                                                new Placeholder("{attacker_custom}", ((LivingEntity) projectile.getShooter()).getCustomName() == null ? "Unknown" : ((LivingEntity) projectile.getShooter()).getCustomName())
                                        ));
                                    }

                                } else {

                                    //PLACEHOLDERS
                                    BetterHudAPI.clearPlaceholders((Player) event.getEntity());
                                    BetterHudAPI.setPlaceholders((Player) event.getEntity(), Arrays.asList(
                                            new Placeholder("{damage}", String.valueOf(event.getDamage())),
                                            new Placeholder("{damage_formatted}", String.valueOf(Math.round(event.getDamage()))),
                                            new Placeholder("{attacker}", event.getDamager().getName()),
                                            new Placeholder("{attacker_custom}", ((Player) event.getDamager()).getDisplayName())
                                    ));

                                }

                                hud.renderFor((Player) event.getEntity(), toggleEvent.getDisplayType(), toggleEvent.getHideAfter(), true);
                            } catch (IllegalArgumentException e) {
                                Bukkit.getLogger().log(Level.WARNING, "&cFailed to pass event: DAMAGE_BY_PLAYER ("+event.getEntity().getName()+")");
                                Bukkit.getLogger().log(Level.WARNING, "&cCan't display this hud, because display argument is not valid! Please, check your configuration.");
                            }
                        });

                    }

                });

            }

            //BY ENTITY
            else {

                BetterHudAPI.getLoadedHuds().forEach(hud -> {

                    if(Display.getDisplays((Player) event.getEntity()).stream().noneMatch(display -> display.getHud().equals(hud))) {

                        Optional<ToggleEvent> optEvent = hud.getEvents().stream().filter(toggleEvent -> toggleEvent.getEventType().equals(ToggleEvent.EventType.DAMAGE_BY_ENTITY)).findFirst();
                        optEvent.ifPresent(toggleEvent -> {
                            try {

                                if(event.getDamager() instanceof Projectile) {
                                    Projectile projectile = (Projectile) event.getDamager();

                                    if(projectile.getShooter() != null) {
                                        //PLACEHOLDERS (GET SHOOTER FROM PROJECTILE
                                        BetterHudAPI.clearPlaceholders((Player) event.getEntity());
                                        BetterHudAPI.setPlaceholders((Player) event.getEntity(), Arrays.asList(
                                                new Placeholder("{damage}", String.valueOf(event.getDamage())),
                                                new Placeholder("{damage_formatted}", String.valueOf(Math.round(event.getDamage()))),
                                                new Placeholder("{attacker}", ((LivingEntity) projectile.getShooter()).getName()),
                                                new Placeholder("{attacker_custom}", ((LivingEntity) projectile.getShooter()).getCustomName() == null ? "Unknown" : ((LivingEntity) projectile.getShooter()).getCustomName())
                                        ));
                                    }

                                } else {
                                    //PLACEHOLDERS
                                    BetterHudAPI.clearPlaceholders((Player) event.getEntity());
                                    BetterHudAPI.setPlaceholders((Player) event.getEntity(), Arrays.asList(
                                            new Placeholder("{damage}", String.valueOf(event.getDamage())),
                                            new Placeholder("{damage_formatted}", String.valueOf(Math.round(event.getDamage()))),
                                            new Placeholder("{attacker}", event.getDamager().getName()),
                                            new Placeholder("{attacker_custom}", event.getDamager().getCustomName() == null ? "Unknown" : event.getDamager().getCustomName())
                                    ));
                                }

                                hud.renderFor((Player) event.getEntity(), toggleEvent.getDisplayType(), toggleEvent.getHideAfter(), true);
                            } catch (IllegalArgumentException e) {
                                Bukkit.getLogger().log(Level.WARNING, "&cFailed to pass event: DAMAGE_BY_ENTITY ("+event.getEntity().getName()+")");
                                Bukkit.getLogger().log(Level.WARNING, "&cCan't display this hud, because display argument is not valid! Please, check your configuration.");
                            }
                        });

                    }

                });

            }

        }

    }

    @EventHandler
    public void GameModeChange(PlayerGameModeChangeEvent event) {

        BetterHudAPI.getLoadedHuds().forEach(hud -> {

            Optional<ToggleEvent> optEvent = hud.getEvents().stream().filter(toggleEvent -> toggleEvent.getEventType().equals(ToggleEvent.EventType.GAMEMODE_CHANGE)).findFirst();

            if(optEvent.isPresent()) {

                if(optEvent.get().getOpt_value() != null) {

                    //PLACEHOLDERS
                    BetterHudAPI.clearPlaceholders(event.getPlayer());
                    BetterHudAPI.setPlaceholders(event.getPlayer(), Arrays.asList(
                            new Placeholder("{from}", MessageUtils.enumToName(event.getPlayer().getGameMode().name())),
                            new Placeholder("{to}", MessageUtils.enumToName(event.getNewGameMode().name()))
                    ));

                    //TO
                    if(event.getNewGameMode().name().equalsIgnoreCase(optEvent.get().getOpt_value())) {
                        hud.renderFor(event.getPlayer(), optEvent.get().getDisplayType(), optEvent.get().getHideAfter(), true);
                    }

                    //FROM
                    else if(event.getPlayer().getGameMode().name().equalsIgnoreCase(optEvent.get().getOpt_value())) {
                        hud.hide(event.getPlayer());
                    }
                }

            }

        });


    }

}
