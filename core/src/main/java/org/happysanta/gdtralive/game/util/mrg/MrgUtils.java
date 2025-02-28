package org.happysanta.gdtralive.game.util.mrg;

import org.happysanta.gdtralive.game.api.Constants;
import org.happysanta.gdtralive.game.api.model.TrackData;
import org.happysanta.gdtralive.game.api.model.Mod;
import org.happysanta.gdtralive.game.api.dto.LevelPack;
import org.happysanta.gdtralive.game.api.dto.TrackParams;

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
        return mod;
    }
}
