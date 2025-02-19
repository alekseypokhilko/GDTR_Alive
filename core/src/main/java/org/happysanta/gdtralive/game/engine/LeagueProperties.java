package org.happysanta.gdtralive.game.engine;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LeagueProperties {
    public static String name = "name";//;
    public static String m_gI = "m_gI";// = 45875;
    public static String m_fI = "m_fI";// = 13107;
    public static String m_eI = "m_eI";// = 39321;
    public static String m_yI = "m_yI";// = 0x140000;  //крепкость?
    public static String m_xI = "m_xI";// = 0x40000; //rotation?
    public static String m_jI = "m_jI";// = 6553;
    public static String m_aeI = "m_aeI";//;
    public static String m_adI = "m_adI";//;
    public static String m_qI = "m_qI";//; //рассыпается
    public static String m_PI = "m_PI";//;
    public static String m_QI = "m_QI";//;
    public static String acceleration_m_charI = "acceleration_m_charI";//;
    public static String m_abI = "m_abI";//;
    public static String m_WI = "m_WI";//;
    public static String rotationForce = "rotationForce";//;
    public static String rotation1 = "rotation1";//;

    private final Map<String, Object> props = new LinkedHashMap<>();

    public LeagueProperties() {
        initDefaults();
    }

    private void initDefaults() {
        //100 by default //todo unmodified params
        props.put(name, "100cc");
        props.put(m_gI, 45875);
        props.put(m_fI, 13107);
        props.put(m_eI, 39321);
        props.put(m_yI, 0x140000);
        props.put(m_xI, 0x40000);
        props.put(m_jI, 6553);
        props.put(m_aeI, 19660);
        props.put(m_adI, 19660);
        props.put(m_PI, 0x110000);
        props.put(m_QI, 0x3200000);
        props.put(acceleration_m_charI, 0x320000);
        props.put(m_abI, 327);
        props.put(m_WI, 0);
        props.put(rotationForce, 32768);
        props.put(m_qI, 0x12c0000);
        props.put(rotation1, 0x50000);
    }

    public void setProp(String key, Object prop) {
        props.put(key, prop);
    }

    public String getName() {
        return (String) props.get(name);
    }

    public int getM_gI() {
        return (int) props.get(m_gI);
    }

    public int getM_fI() {
        return (int) props.get(m_fI);
    }

    public int getM_eI() {
        return (int) props.get(m_eI);
    }

    public int getM_yI() {
        return (int) props.get(m_yI);
    }

    public int getM_xI() {
        return (int) props.get(m_xI);
    }

    public int getM_jI() {
        return (int) props.get(m_jI);
    }

    public int getM_aeI() {
        return (int) props.get(m_aeI);
    }

    public int getM_adI() {
        return (int) props.get(m_adI);
    }

    public int getM_qI() {
        return (int) props.get(m_qI);
    }

    public int getM_PI() {
        return (int) props.get(m_PI);
    }

    public int getM_QI() {
        return (int) props.get(m_QI);
    }

    public int getAcceleration_m_charI() {
        return (int) props.get(acceleration_m_charI);
    }

    public int getM_abI() {
        return (int) props.get(m_abI);
    }

    public int getM_WI() {
        return (int) props.get(m_WI);
    }

    public int getRotationForce() {
        return (int) props.get(rotationForce);
    }

    public int getRotation1() {
        return (int) props.get(rotation1);
    }

    public static List<LeagueProperties> getDefaultLeagueProperties() {
        LeagueProperties league100cc = new LeagueProperties();
        LeagueProperties league175cc = new LeagueProperties();
        league175cc.setProp(name, "175cc");
        league175cc.setProp(m_aeI, 32768);
        league175cc.setProp(m_adI, 32768);
        league175cc.setProp(m_PI, 0x110000);
        league175cc.setProp(m_QI, 0x3e80000);
        league175cc.setProp(acceleration_m_charI, 0x320000);
        league175cc.setProp(m_abI, 6553);
        league175cc.setProp(m_WI, 26214);
        league175cc.setProp(rotationForce, 26214);
        league175cc.setProp(m_qI, 0x12c0000);
        league175cc.setProp(rotation1, 0x50000);

        LeagueProperties league220cc = new LeagueProperties();
        league220cc.setProp(name, "220cc");
        league220cc.setProp(m_aeI, 32768);
        league220cc.setProp(m_adI, 32768);
        league220cc.setProp(m_PI, 0x140000);
        league220cc.setProp(m_QI, 0x47e0000);
        league220cc.setProp(acceleration_m_charI, 0x350000);
        league220cc.setProp(m_abI, 6553);
        league220cc.setProp(m_WI, 26214);
        league220cc.setProp(rotationForce, 39321);
        league220cc.setProp(m_qI, 0x14a0000);
        league220cc.setProp(rotation1, 0x50000);

        LeagueProperties league325cc = new LeagueProperties();
        league325cc.setProp(name, "325cc");
        league325cc.setProp(m_aeI, 32768);
        league325cc.setProp(m_adI, 32768);
        league325cc.setProp(m_PI, 0x160000);
        league325cc.setProp(m_QI, 0x4b00000);
        league325cc.setProp(acceleration_m_charI, 0x360000);
        league325cc.setProp(m_abI, 6553);
        league325cc.setProp(m_WI, 26214);
        league325cc.setProp(rotationForce, 0x10000);
        league325cc.setProp(m_qI, 0x14a0000);
        league325cc.setProp(rotation1, 0x140000);

        List<LeagueProperties> leagueProperties = new ArrayList<>();
        leagueProperties.add(league100cc);
        leagueProperties.add(league175cc);
        leagueProperties.add(league220cc);
        leagueProperties.add(league325cc);
        return leagueProperties;
    }
}
