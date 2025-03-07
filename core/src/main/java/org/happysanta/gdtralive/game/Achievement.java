package org.happysanta.gdtralive.game;

import org.happysanta.gdtralive.game.util.Fmt;
import org.happysanta.gdtralive.game.api.S;

import java.util.HashMap;
import java.util.Map;

public class Achievement {
    public static final Map<Achievement.Type, Achievement> achievements = new HashMap<>();

    static {
        achievements.put(Type.TRIAL_MASTER, new Achievement(Type.TRIAL_MASTER.id, 0, 0));
        achievements.put(Type.BACK_TO_SCHOOL, new Achievement(Type.BACK_TO_SCHOOL.id, 0, 0));
        achievements.put(Type.GAMBLER, new Achievement(Type.GAMBLER.id, 0, 0));
        achievements.put(Type.SERIES_LOVER, new Achievement(Type.SERIES_LOVER.id, 0, 0));
        achievements.put(Type.ESTHETE, new Achievement(Type.ESTHETE.id, 0, 0));
        achievements.put(Type.DEVELOPER, new Achievement(Type.DEVELOPER.id, 0, 0));
    }

    private final String id;
    private long count;
    private int level;

    public Achievement(String id, long count, int level) {
        this.id = id;
        this.count = count;
        this.level = level;
    }

    public S getName() {
        return Type.fromId(id).names[level];
    }

    public S getDescription() {
        return Type.fromId(id).description;
    }

    public String getProgressFormatted() {
        Type type = Type.fromId(id);
        if (this.level + 1 < type.levels.length) {
            long levelCount = type.levels[this.level + 1];
            return Fmt.slash(count, levelCount);
        } else {
            long levelCount = type.levels[type.levels.length - 1];
            return Fmt.slash(count, levelCount);
        }
    }

    public long getCount() {
        return count;
    }

    public int getLevel() {
        return level;
    }

    public boolean isCompleted() {
        return true;
    }

    public void increment() {
        //todo toast
        int newLevel = Type.fromId(id).getLevel(count + 1);
        if (newLevel > level) {
            level = newLevel;
        }
        this.count = count + 1;
    }

    public enum Type {
        TRIAL_MASTER("trial_master",
                new long[]{0, 10, 100, 1000},
                new S[]{
                        S.TRIAL_MASTER,
                        S.TRIAL_MASTER,
                        S.TRIAL_MASTER,
                        S.TRIAL_MASTER
                },
                S.TRIAL_MASTER_DESC
        ),

        GAMBLER("gambler",
                new long[]{0, 10, 100, 1000},
                new S[]{
                        S.GAMBLER,
                        S.GAMBLER,
                        S.GAMBLER,
                        S.GAMBLER
                },
                S.GAMBLER_DESC
        ),

        SERIES_LOVER("series_lover",
                new long[]{0, 10, 100, 1000},
                new S[]{
                        S.SERIES_LOVER,
                        S.SERIES_LOVER,
                        S.SERIES_LOVER,
                        S.SERIES_LOVER
                },
                S.SERIES_LOVER_DESC
        ),

        ESTHETE("esthete",
                new long[]{0, 1, 10, 100},
                new S[]{
                        S.ESTHETE,
                        S.ESTHETE,
                        S.ESTHETE,
                        S.ESTHETE
                },
                S.ESTHETE_DESC
        ),

        DEVELOPER("developer",
                new long[]{0, 1, 10, 100},
                new S[]{
                        S.DEVELOPER,
                        S.DEVELOPER,
                        S.DEVELOPER,
                        S.DEVELOPER
                },
                S.DEVELOPER_DESC
        ),

        BACK_TO_SCHOOL("back_to_school",
                new long[]{0, 10, 100, 1000},
                new S[]{
                        S.BACK_TO_SCHOOL,
                        S.BACK_TO_SCHOOL,
                        S.BACK_TO_SCHOOL,
                        S.BACK_TO_SCHOOL
                },
                S.BACK_TO_SCHOOL_DESC
        );

        public final String id;
        public final long[] levels;
        public final S[] names;
        public final S description;

        Type(String id, long[] levels, S[] names, S description) {
            this.id = id;
            this.levels = levels;
            this.names = names;
            this.description = description;
        }

        public static Type fromId(String id) {
            for (Type type : Type.values()) {
                if (type.id.equals(id)) {
                    return type;
                }
            }
            return null;
        }

        public int getLevel(long count) {
            for (int i = levels.length - 1; i >= 0; i--) {
                if (count >= levels[i]) {
                    return i;
                }
            }
            return 0;
        }
    }
}
