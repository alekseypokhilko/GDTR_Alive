package org.happysanta.gdtralive.game.levels.mrg;

import org.happysanta.gdtralive.game.Constants;
import org.happysanta.gdtralive.game.levels.TrackParams;
import org.happysanta.gdtralive.game.mod.Mod;
import org.happysanta.gdtralive.game.mod.PackLevel;
import org.happysanta.gdtralive.game.mod.TrackReference;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.Date;
import java.util.UUID;

public class MrgUtils {
    public static Mod convertMrg(String fileName, byte[] bytes) {
        return convertMrg(null, fileName, bytes);
    }

    public static Mod convertMrg(String name, String fileName, byte[] bytes) {
        String packName = name == null || "".equals(name) ? fileName : name;
        String author = "Unknown";


        Mod mod = new Mod();
        mod.setGuid(UUID.randomUUID().toString());
        mod.setName(packName);
        mod.setAuthor(author);
        mod.setDate(Constants.DATE_FORMAT.format(new Date()));

        LevelHeader header = Reader.readHeader(bytes);
        for (int league = 0; league < 3; league++) {
            PackLevel packLevelObj = new PackLevel();

            for (int track = 0; track < header.getPointers()[league].length; track++) {
                try (ByteArrayInputStream in = new ByteArrayInputStream(bytes);
                     DataInputStream dis = new DataInputStream(in)) {
                    for (int i1 = header.getPointers()[league][track]; i1 > 0; i1 -= dis.skipBytes(i1))
                        ;
                    TrackParams level = new TrackParams();
                    level.setLeague(league);
                    level.readTrackData(dis, true); //todo unpack on loading track

                    level.setGuid(UUID.randomUUID().toString());
                    level.setAuthor(author);
                    level.setName(header.getNames()[league][track]);

                    int[][] pointsTrimmed = new int[level.pointsCount][2];
                    System.arraycopy(level.points, 0, pointsTrimmed, 0, level.pointsCount);
                    level.points = pointsTrimmed;

                    TrackReference trackReference = new TrackReference();
                    trackReference.setGuid(level.getGuid());
                    trackReference.setName(level.getName());
                    trackReference.setData(level);

                    packLevelObj.getTracks().add(trackReference);
                } catch (Exception _ex) {
                    throw new RuntimeException(_ex);
                }
            }
            mod.getLevels().add(packLevelObj);
        }
        return mod;
    }
}
