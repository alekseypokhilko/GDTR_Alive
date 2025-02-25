package org.happysanta.gdtralive.game.api.dto;

import java.util.List;
import java.util.Map;

public class GameProperties {
    private Map<String, Object> interfaceTheme;
    private Map<String, Object> gameTheme;
    private List<Map<String, Object>> leagueProperties;
    private List<String> levelNames; //todo move to PackLevel
}