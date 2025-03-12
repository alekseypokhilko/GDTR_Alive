package org.happysanta.gdtralive.game.util.mrg;

import org.happysanta.gdtralive.game.AbstractFileStorage;
import org.happysanta.gdtralive.game.api.Constants;
import org.happysanta.gdtralive.game.api.GDFile;
import org.happysanta.gdtralive.game.api.dto.LevelPack;
import org.happysanta.gdtralive.game.api.dto.TrackParams;
import org.happysanta.gdtralive.game.api.model.Mod;
import org.happysanta.gdtralive.game.api.model.TrackData;
import org.happysanta.gdtralive.game.engine.TrackPhysic;
import org.happysanta.gdtralive.game.util.Fmt;
import org.happysanta.gdtralive.game.util.Utils;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class MrgUtils {

    static String BASE_PATH = "/Users/semen/Desktop/gg/gd/mrg";
    static String MULTIGOD = "10_391_Gravity Defied MultiGOD [323.263.452]Sewer MC.mrg";

    public static void main(String[] args) {

        String[] modNmaes = new String[]{
                "GDTR Alive",
                "GDTR Original",
                "GDTR ULTIMATE α",
                "GDTR ULTIMATE β",
                "GDTR ULTIMATE γ",
                "GDTR ULTIMATE Δ",
                "GDTR ULTIMATE λ",
                "GDTR ULTIMATE μ",
                "GDTR ULTIMATE Ξ",
                "GDTR ULTIMATE π",
                "GDTR ULTIMATE Ω"
        };

        File appFolder = new File("/Users/semen/Documents/projects/MY/GDTR_Alive/app/src/main/assets");
        Map<GDFile, File> folders = new HashMap<>();
        folders.put(GDFile.MOD, new File("/Users/semen/Documents/projects/MY/GDTR_Alive/app/src/main/assets/mods"));
        AbstractFileStorage fs = new AbstractFileStorage(appFolder, folders) {
            @Override
            protected String[] listAssets(GDFile fileType) throws IOException {
                return null;
            }

            @Override
            protected InputStream fromAssets(String folder, String name) throws IOException {
                return null;
            }
        };
        for (String modPath : modNmaes) {
            Mod mod = fs.readMod(modPath);
            fs.save(mod, GDFile.MOD, modPath);
        }

        if (true) {
            return;
        }
        Mod multigod = null;
        Map<String, TrackData> trackToTrackGuid = new HashMap<>();
        for (File file : Objects.requireNonNull(new File(BASE_PATH).listFiles())) {
            try {
                String name = file.getName(); //464_509_Levels by Neker [15.15.20]Neker.mrg
                String modName = Utils.fixFileName(
                        name.substring(name.indexOf("_") + 1, name.indexOf("]") + 1).trim()
                                .substring(name.indexOf("_") + 1)
                );
                String author = Utils.fixFileName(name.substring(name.indexOf("]") + 1, name.lastIndexOf(".")));
                Mod mod = MrgUtils.convertMrg(name, Utils.readAllBytes(new FileInputStream(file)), false);
                mod.setName(modName);
                mod.setAuthor(author);

                if (MULTIGOD.equals(name)) {
                    multigod = mod;
                }

                for (LevelPack level : mod.getLevels()) {
                    for (TrackParams track : level.getTracks()) {
                        int[][] points = track.getData().getPoints();
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int[] point : points) {
                            stringBuilder.append(Arrays.toString(point));
                        }
                        String trackId = stringBuilder.toString();
                        TrackData inMap = trackToTrackGuid.get(trackId);
                        if (inMap != null) {
                            track.getData().setGuid(inMap.getGuid());
                        } else {
                            try {
                                new TrackPhysic().load(track.getData()); //assert track is valid
                                if (track.getData().getStartPointIndex() < track.getData().getFinishPointIndex()) {
                                    trackToTrackGuid.put(trackId, track.getData());
                                }
                            } catch (Exception e) {

                            }
                        }
                    }
                }

                if (false) {
                    write(mod);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        List<LevelPack> levels = new ArrayList<>();
        levels.add(new LevelPack());
        levels.add(new LevelPack());
        levels.add(new LevelPack());
        levels.get(0).setTracks(new ArrayList<>());
        levels.get(1).setTracks(new ArrayList<>());
        levels.get(2).setTracks(new ArrayList<>());
        for (TrackData track : trackToTrackGuid.values()) {
            TrackParams trackParams = new TrackParams();
            trackParams.setData(track);
            int league = track.getLeague();
            levels.get(league).getTracks().add(trackParams);
        }

        //Α α, Β β, Γ γ, Δ δ, Ε ε, Ζ ζ, Η η, Θ θ, Ι ι, Κ κ, Λ λ, Μ μ, Ν ν, Ξ ξ, Ο ο, Π π, Ρ ρ, Σ σ ς, Τ τ, Υ υ, Φ φ, Χ χ, Ψ ψ, Ω ω.
        String[] ch = new String[]{"α", "β", "Δ", "Σ", "Ω", "λ", "μ", "π", "Ξ", "γ", "Θ", "τ", "φ", "Ψ", "σ"};
        int index = 0;
        for (int i = 0; i < 20; i++) {
            Mod mod = new Mod();
            mod.setName("GDTR ULTIMATE " + ch[i]);
            mod.setGuid(UUID.randomUUID().toString());
            mod.setAuthor("GDTR Alive");
            mod.setDate(new Date().toString());

            List<LevelPack> levelsBounded = new ArrayList<>();
            levelsBounded.add(new LevelPack());
            levelsBounded.add(new LevelPack());
            levelsBounded.add(new LevelPack());
            levelsBounded.get(0).setTracks(new ArrayList<>());
            levelsBounded.get(1).setTracks(new ArrayList<>());
            levelsBounded.get(2).setTracks(new ArrayList<>());

            for (int counter = 0; counter < 1500; counter++) {
                try {
                    levelsBounded.get(0).getTracks().add(levels.get(0).getTracks().get(index));
                } catch (Exception e) {
                }
                try {
                    levelsBounded.get(1).getTracks().add(levels.get(1).getTracks().get(index));
                } catch (Exception e) {
                }
                try {
                    levelsBounded.get(2).getTracks().add(levels.get(2).getTracks().get(index));
                } catch (Exception e) {
                }
                index++;
            }
            if (levelsBounded.get(0).getTracks().isEmpty()) {
                levelsBounded.get(0).getTracks().add(multigod.getLevels().get(0).getTracks().get(0));
            }
            if (levelsBounded.get(1).getTracks().isEmpty()) {
                levelsBounded.get(1).getTracks().add(multigod.getLevels().get(1).getTracks().get(1));
            }
            if (levelsBounded.get(2).getTracks().isEmpty()) {
                levelsBounded.get(2).getTracks().add(multigod.getLevels().get(2).getTracks().get(2));
            }

            mod.setLevels(levelsBounded);

//            write(mod.pack());
        }
    }

    private static void write(Mod mod) {
        File output = new File(Fmt.slash(BASE_PATH, "packed"), GDFile.MOD.addExtension(mod.getName()));
        if (output != null) {
            try (PrintStream out = new PrintStream(output)) {
                String content = Utils.toJson(mod);
                out.print(content);
                out.flush();
            } catch (Exception e) {
            }
        }
    }

    public static Mod convertMrg(String fileName, byte[] bytes, boolean packed) {
        String packName = null == null || "".equals(null) ? fileName : null;
        String author = "Unknown";


        Mod mod = new Mod();
        mod.setGuid(UUID.randomUUID().toString());
        mod.setName(packName);
        mod.setAuthor(author);
        mod.setDate(Constants.DATE_FORMAT.format(new Date()));

        LevelHeader header = Reader.readHeader(bytes);
        for (int league = 0; league < 3; league++) {
            LevelPack levelPackObj = new LevelPack();

            for (int track = 0; track < header.getPointers()[league].length; track++) {
                try (ByteArrayInputStream in = new ByteArrayInputStream(bytes);
                     DataInputStream dis = new DataInputStream(in)) {
                    for (int i1 = header.getPointers()[league][track]; i1 > 0; i1 -= dis.skipBytes(i1))
                        ;
                    TrackData trackData = new TrackData();
                    trackData.setLeague(league);
                    trackData.readTrackData(dis, true); //todo unpack on loading track

                    trackData.setGuid(UUID.randomUUID().toString());
                    trackData.setAuthor(author);
                    trackData.setName(header.getNames()[league][track]);

                    int[][] pointsTrimmed = new int[trackData.pointsCount][2];
                    System.arraycopy(trackData.points, 0, pointsTrimmed, 0, trackData.pointsCount);
                    trackData.points = pointsTrimmed;

                    TrackParams trackParams = new TrackParams();
                    trackParams.setData(trackData);

                    levelPackObj.getTracks().add(trackParams);
                } catch (Exception _ex) {
                    throw new RuntimeException(_ex);
                }
            }
            mod.getLevels().add(levelPackObj);
        }

        return packed ? mod.pack() : mod;
    }

}
