package us.talabrek.ultimateskyblock.island;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import us.talabrek.ultimateskyblock.Settings;
import us.talabrek.ultimateskyblock.handler.WorldEditHandler;
import us.talabrek.ultimateskyblock.player.PlayerPerk;
import us.talabrek.ultimateskyblock.uSkyBlock;
import us.talabrek.ultimateskyblock.util.FileUtil;
import us.talabrek.ultimateskyblock.util.ItemStackUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * The factory for creating islands (actual blocks).
 */
@SuppressWarnings("deprecation")
public class IslandGenerator {
    private static final String CN = IslandGenerator.class.getName();
    private static final Logger log = Logger.getLogger(IslandGenerator.class.getName());
    private final FileConfiguration config;
    private final File[] schemFiles;

    public IslandGenerator(File dataFolder, FileConfiguration config) {
        this.config = config;
        File directorySchematics = new File(dataFolder + File.separator + "schematics");
        if (!directorySchematics.exists()) {
            directorySchematics.mkdir();
        }
        this.schemFiles = directorySchematics.listFiles();
        if (this.schemFiles == null) {
            System.out.print("[uSkyBlock] No schematic file loaded.");
        } else {
            System.out.print("[uSkyBlock] " + this.schemFiles.length + " schematics loaded.");
        }
    }

    public List<String> getSchemeNames() {
        List<String> names = new ArrayList<>();
        for (File f : schemFiles) {
            names.add(FileUtil.getBasename(f));
        }
        return names;
    }


    public String createIsland(uSkyBlock plugin, final PlayerPerk playerPerk, final Location next, Runnable onCompletion) {
        log.entering(CN, "createIsland", new Object[]{plugin, playerPerk.getPlayerInfo().getPlayerName(), next});
        log.fine("creating island for " + playerPerk.getPlayerInfo().getPlayerName() + " at " + next);
        boolean hasIslandNow = false;
        String cSchem = "default";
        if (schemFiles.length > 0 && Bukkit.getServer().getPluginManager().isPluginEnabled("WorldEdit")) {
            File permFile = null;
            File defaultFile = null;
            for (File schemFile : schemFiles) {
                // First run-through - try to set the island the player has permission for.
                cSchem = FileUtil.getBasename(schemFile);
                if (cSchem.equalsIgnoreCase(Settings.island_schematicName)) {
                    defaultFile = schemFile;
                }
                if (permFile == null && playerPerk.getPerk().getSchematics().contains(cSchem)) {
                    permFile = schemFile;
                }
            }
            if (permFile != null) {
                defaultFile = permFile;
            }
            if (defaultFile != null && WorldEditHandler.loadIslandSchematic(uSkyBlock.skyBlockWorld, defaultFile, next)) {
                cSchem = FileUtil.getBasename(defaultFile);
                hasIslandNow = true;
                log.fine("chose schematic " + defaultFile);
            }
        }
        if (!hasIslandNow) {
            if (!Settings.island_useOldIslands) {
                log.fine("generating a uSkyBlock default island");
                generateIslandBlocks(next.getBlockX(), next.getBlockZ(), playerPerk, uSkyBlock.skyBlockWorld);
                cSchem = "default";
            } else {
                log.fine("generating a skySMP island");
                oldGenerateIslandBlocks(next.getBlockX(), next.getBlockZ(), playerPerk, uSkyBlock.skyBlockWorld);
                cSchem = "skySMP";
            }
        }
        next.setY((double) Settings.island_height);
        log.exiting(CN, "createIsland");
        return cSchem;
    }

    public void generateIslandBlocks(final int x, final int z, final PlayerPerk playerPerk, final World world) {
        final int y = Settings.island_height;
        final Block blockToChange = world.getBlockAt(x, y, z);
        blockToChange.setTypeIdAndData(7, (byte) 0, false);
        this.islandLayer1(x, z, world);
        this.islandLayer2(x, z, world);
        this.islandLayer3(x, z, world);
        this.islandLayer4(x, z, world);
        this.islandExtras(x, z, playerPerk, world);
    }

    public void oldGenerateIslandBlocks(final int x, final int z, final PlayerPerk playerPerk, final World world) {
        final int y = Settings.island_height;
        for (int x_operate = x; x_operate < x + 3; ++x_operate) {
            for (int y_operate = y; y_operate < y + 3; ++y_operate) {
                for (int z_operate = z; z_operate < z + 6; ++z_operate) {
                    final Block blockToChange = world.getBlockAt(x_operate, y_operate, z_operate);
                    blockToChange.setTypeIdAndData(2, (byte) 0, false);
                }
            }
        }
        for (int x_operate = x + 3; x_operate < x + 6; ++x_operate) {
            for (int y_operate = y; y_operate < y + 3; ++y_operate) {
                for (int z_operate = z + 3; z_operate < z + 6; ++z_operate) {
                    final Block blockToChange = world.getBlockAt(x_operate, y_operate, z_operate);
                    blockToChange.setTypeIdAndData(2, (byte) 0, false);
                }
            }
        }
        for (int x_operate = x + 3; x_operate < x + 7; ++x_operate) {
            for (int y_operate = y + 7; y_operate < y + 10; ++y_operate) {
                for (int z_operate = z + 3; z_operate < z + 7; ++z_operate) {
                    final Block blockToChange = world.getBlockAt(x_operate, y_operate, z_operate);
                    blockToChange.setTypeIdAndData(18, (byte) 0, false);
                }
            }
        }
        for (int y_operate2 = y + 3; y_operate2 < y + 9; ++y_operate2) {
            final Block blockToChange2 = world.getBlockAt(x + 5, y_operate2, z + 5);
            blockToChange2.setTypeIdAndData(17, (byte) 0, false);
        }
        Block blockToChange3 = world.getBlockAt(x + 1, y + 3, z + 1);
        blockToChange3.setTypeIdAndData(54, (byte) 0, false);
        final Chest chest = (Chest) blockToChange3.getState();
        blockToChange3 = world.getBlockAt(x, y, z);
        blockToChange3.setTypeIdAndData(7, (byte) 0, false);
        blockToChange3 = world.getBlockAt(x + 2, y + 1, z + 1);
        blockToChange3.setTypeIdAndData(12, (byte) 0, false);
        blockToChange3 = world.getBlockAt(x + 2, y + 1, z + 2);
        blockToChange3.setTypeIdAndData(12, (byte) 0, false);
        blockToChange3 = world.getBlockAt(x + 2, y + 1, z + 3);
        blockToChange3.setTypeIdAndData(12, (byte) 0, false);
    }

    private void islandLayer1(final int x, final int z, final World world) {
        int y = Settings.island_height + 4;
        for (int x_operate = x - 3; x_operate <= x + 3; ++x_operate) {
            for (int z_operate = z - 3; z_operate <= z + 3; ++z_operate) {
                final Block blockToChange = world.getBlockAt(x_operate, y, z_operate);
                blockToChange.setTypeIdAndData(2, (byte) 0, false);
            }
        }
        Block blockToChange2 = world.getBlockAt(x - 3, y, z + 3);
        blockToChange2.setTypeIdAndData(0, (byte) 0, false);
        blockToChange2 = world.getBlockAt(x - 3, y, z - 3);
        blockToChange2.setTypeIdAndData(0, (byte) 0, false);
        blockToChange2 = world.getBlockAt(x + 3, y, z - 3);
        blockToChange2.setTypeIdAndData(0, (byte) 0, false);
        blockToChange2 = world.getBlockAt(x + 3, y, z + 3);
        blockToChange2.setTypeIdAndData(0, (byte) 0, false);
    }

    private void islandLayer2(final int x, final int z, final World world) {
        int y = Settings.island_height + 3;
        for (int x_operate = x - 2; x_operate <= x + 2; ++x_operate) {
            for (int z_operate = z - 2; z_operate <= z + 2; ++z_operate) {
                final Block blockToChange = world.getBlockAt(x_operate, y, z_operate);
                blockToChange.setTypeIdAndData(3, (byte) 0, false);
            }
        }
        Block blockToChange2 = world.getBlockAt(x - 3, y, z);
        blockToChange2.setTypeIdAndData(3, (byte) 0, false);
        blockToChange2 = world.getBlockAt(x + 3, y, z);
        blockToChange2.setTypeIdAndData(3, (byte) 0, false);
        blockToChange2 = world.getBlockAt(x, y, z - 3);
        blockToChange2.setTypeIdAndData(3, (byte) 0, false);
        blockToChange2 = world.getBlockAt(x, y, z + 3);
        blockToChange2.setTypeIdAndData(3, (byte) 0, false);
        blockToChange2 = world.getBlockAt(x, y, z);
        blockToChange2.setTypeIdAndData(12, (byte) 0, false);
    }

    private void islandLayer3(final int x, final int z, final World world) {
        int y = Settings.island_height + 2;
        for (int x_operate = x - 1; x_operate <= x + 1; ++x_operate) {
            for (int z_operate = z - 1; z_operate <= z + 1; ++z_operate) {
                final Block blockToChange = world.getBlockAt(x_operate, y, z_operate);
                blockToChange.setTypeIdAndData(3, (byte) 0, false);
            }
        }
        Block blockToChange2 = world.getBlockAt(x - 2, y, z);
        blockToChange2.setTypeIdAndData(3, (byte) 0, false);
        blockToChange2 = world.getBlockAt(x + 2, y, z);
        blockToChange2.setTypeIdAndData(3, (byte) 0, false);
        blockToChange2 = world.getBlockAt(x, y, z - 2);
        blockToChange2.setTypeIdAndData(3, (byte) 0, false);
        blockToChange2 = world.getBlockAt(x, y, z + 2);
        blockToChange2.setTypeIdAndData(3, (byte) 0, false);
        blockToChange2 = world.getBlockAt(x, y, z);
        blockToChange2.setTypeIdAndData(12, (byte) 0, false);
    }

    private void islandLayer4(final int x, final int z, final World world) {
        int y = Settings.island_height + 1;
        Block blockToChange = world.getBlockAt(x - 1, y, z);
        blockToChange.setTypeIdAndData(3, (byte) 0, false);
        blockToChange = world.getBlockAt(x + 1, y, z);
        blockToChange.setTypeIdAndData(3, (byte) 0, false);
        blockToChange = world.getBlockAt(x, y, z - 1);
        blockToChange.setTypeIdAndData(3, (byte) 0, false);
        blockToChange = world.getBlockAt(x, y, z + 1);
        blockToChange.setTypeIdAndData(3, (byte) 0, false);
        blockToChange = world.getBlockAt(x, y, z);
        blockToChange.setTypeIdAndData(12, (byte) 0, false);
    }

    private void islandExtras(final int x, final int z, final PlayerPerk playerPerk, final World world) {
        int y = Settings.island_height;
        Block blockToChange = world.getBlockAt(x, y + 5, z);
        blockToChange.setTypeIdAndData(17, (byte) 0, false);
        blockToChange = world.getBlockAt(x, y + 6, z);
        blockToChange.setTypeIdAndData(17, (byte) 0, false);
        blockToChange = world.getBlockAt(x, y + 7, z);
        blockToChange.setTypeIdAndData(17, (byte) 0, false);
        y = Settings.island_height + 8;
        for (int x_operate = x - 2; x_operate <= x + 2; ++x_operate) {
            for (int z_operate = z - 2; z_operate <= z + 2; ++z_operate) {
                blockToChange = world.getBlockAt(x_operate, y, z_operate);
                blockToChange.setTypeIdAndData(18, (byte) 0, false);
            }
        }
        blockToChange = world.getBlockAt(x + 2, y, z + 2);
        blockToChange.setTypeIdAndData(0, (byte) 0, false);
        blockToChange = world.getBlockAt(x + 2, y, z - 2);
        blockToChange.setTypeIdAndData(0, (byte) 0, false);
        blockToChange = world.getBlockAt(x - 2, y, z + 2);
        blockToChange.setTypeIdAndData(0, (byte) 0, false);
        blockToChange = world.getBlockAt(x - 2, y, z - 2);
        blockToChange.setTypeIdAndData(0, (byte) 0, false);
        blockToChange = world.getBlockAt(x, y, z);
        blockToChange.setTypeIdAndData(17, (byte) 0, false);
        y = Settings.island_height + 9;
        for (int x_operate = x - 1; x_operate <= x + 1; ++x_operate) {
            for (int z_operate = z - 1; z_operate <= z + 1; ++z_operate) {
                blockToChange = world.getBlockAt(x_operate, y, z_operate);
                blockToChange.setTypeIdAndData(18, (byte) 0, false);
            }
        }
        blockToChange = world.getBlockAt(x - 2, y, z);
        blockToChange.setTypeIdAndData(18, (byte) 0, false);
        blockToChange = world.getBlockAt(x + 2, y, z);
        blockToChange.setTypeIdAndData(18, (byte) 0, false);
        blockToChange = world.getBlockAt(x, y, z - 2);
        blockToChange.setTypeIdAndData(18, (byte) 0, false);
        blockToChange = world.getBlockAt(x, y, z + 2);
        blockToChange.setTypeIdAndData(18, (byte) 0, false);
        blockToChange = world.getBlockAt(x, y, z);
        blockToChange.setTypeIdAndData(17, (byte) 0, false);
        y = Settings.island_height + 10;
        blockToChange = world.getBlockAt(x - 1, y, z);
        blockToChange.setTypeIdAndData(18, (byte) 0, false);
        blockToChange = world.getBlockAt(x + 1, y, z);
        blockToChange.setTypeIdAndData(18, (byte) 0, false);
        blockToChange = world.getBlockAt(x, y, z - 1);
        blockToChange.setTypeIdAndData(18, (byte) 0, false);
        blockToChange = world.getBlockAt(x, y, z + 1);
        blockToChange.setTypeIdAndData(18, (byte) 0, false);
        blockToChange = world.getBlockAt(x, y, z);
        blockToChange.setTypeIdAndData(17, (byte) 0, false);
        blockToChange = world.getBlockAt(x, y + 1, z);
        blockToChange.setTypeIdAndData(18, (byte) 0, false);
        blockToChange = world.getBlockAt(x, Settings.island_height + 5, z + 1);
        blockToChange.setTypeIdAndData(54, (byte) 3, false);
    }

    public void setChest(final Location loc, final PlayerPerk playerPerk) {
        World world = loc.getWorld();
        world.loadChunk(loc.getBlockX() >> 4, loc.getBlockZ() >> 4, false);
        for (int dx = 1; dx <= 30; dx++) {
            for (int dy = 1; dy <= 30; dy++) {
                for (int dz = 1; dz <= 30; dz++) {
                    int x = ((dx % 2) == 0) ? dx / 2 : -dx / 2;
                    int y = ((dy % 2) == 0) ? dy / 2 : -dy / 2;
                    int z = ((dz % 2) == 0) ? dz / 2 : -dz / 2;
                    if (world.getBlockAt(loc.getBlockX() + x, loc.getBlockY() + y, loc.getBlockZ() + z).getTypeId() == 54) {
                        final Block blockToChange = world.getBlockAt(loc.getBlockX() + x, loc.getBlockY() + y, loc.getBlockZ() + z);
                        final Chest chest = (Chest) blockToChange.getState();
                        final Inventory inventory = chest.getInventory();
                        inventory.clear();
                        inventory.setContents(Settings.island_chestItems);
                        if (Settings.island_addExtraItems) {
                            inventory.addItem(ItemStackUtil.createItemArray(playerPerk.getPerk().getExtraItems()));
                        }
                    }
                }
            }
        }
    }
}
