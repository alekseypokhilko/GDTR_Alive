package org.happysanta.gdtralive.android;

import org.happysanta.gdtralive.R;
import org.happysanta.gdtralive.game.api.external.GdStr;
import org.happysanta.gdtralive.game.api.S;

import java.util.HashMap;
import java.util.Map;

public class AStr implements GdStr {

    private final Map<S, Integer> strings = new HashMap<>();

    public AStr() {
        strings.put(S.CRASHED, R.string.crashed);
        strings.put(S.FINISHED, R.string.finished1);
        strings.put(S.WHEELIE, R.string.wheelie);
        strings.put(S.ATTEMPT, R.string.attempt);
        strings.put(S.TRIAL_MASTER, R.string.trial_master);
        strings.put(S.TRIAL_MASTER_DESC, R.string.trial_master_description);
        strings.put(S.GAMBLER, R.string.gambler);
        strings.put(S.GAMBLER_DESC, R.string.gambler_description);
        strings.put(S.ESTHETE, R.string.esthete);
        strings.put(S.ESTHETE_DESC, R.string.esthete_description);
        strings.put(S.SERIES_LOVER, R.string.developer);
        strings.put(S.SERIES_LOVER_DESC, R.string.developer);
        strings.put(S.DEVELOPER, R.string.developer);
        strings.put(S.DEVELOPER_DESC, R.string.developer_description);
        strings.put(S.BACK_TO_SCHOOL, R.string.back_to_school);
        strings.put(S.BACK_TO_SCHOOL_DESC, R.string.back_to_school_description);
        strings.put(S.main, R.string.main);
        strings.put(S.play, R.string.play);
        strings.put(S.workshop, R.string.workshop);
        strings.put(S.daily_challenge, R.string.daily_challenge);
        strings.put(S.replay, R.string.replay);
        strings.put(S.options, R.string.options);
        strings.put(S.back, R.string.back);
        strings.put(S.track_editor, R.string.track_editor);
        strings.put(S.track_options, R.string.track_options);
        strings.put(S.save, R.string.save);
        strings.put(S.value, R.string.value);
        strings.put(S.exit_editor, R.string.exit_editor);
        strings.put(S.guid, R.string.guid);
        strings.put(S.author, R.string.author);
        strings.put(S.name, R.string.name);
        strings.put(S.league_properties, R.string.league_properties);
        strings.put(S.league, R.string.league);
        strings.put(S.track_properties, R.string.track_properties);
        strings.put(S.interface_properties, R.string.interface_properties);
        strings.put(S.themes, R.string.themes);
        strings.put(S.import_theme, R.string.import_theme);
        strings.put(S.theme, R.string.theme);
        strings.put(S.description, R.string.description);
        strings.put(S.date, R.string.date);
        strings.put(S.install, R.string.install);
        strings.put(S.copy, R.string.copy);
        strings.put(S.share, R.string.share);
        strings.put(S.edit, R.string.edit);
        strings.put(S.rename, R.string.rename);
        strings.put(S.delete, R.string.delete);
        strings.put(S.mod, R.string.mod);
        strings.put(S.tracks, R.string.tracks);
        strings.put(S.mod_packs, R.string.mod_packs);
        strings.put(S.import_mod, R.string.import_mod);
        strings.put(S.import_mrg, R.string.import_mrg);
        strings.put(S.campaign_select, R.string.campaign_select);
        strings.put(S.campaign, R.string.campaign);
        strings.put(S.random_track, R.string.random_track);
        strings.put(S.achievements, R.string.achievements);
        strings.put(S.recordings, R.string.recordings);
        strings.put(S.import_record, R.string.import_record);
        strings.put(S.import_track, R.string.import_track);
        strings.put(S.record_option, R.string.record_option);
        strings.put(S.time, R.string.time);
        strings.put(S.create_new_track, R.string.create_new_track);
        strings.put(S.finished, R.string.finished);
        strings.put(S.restart, R.string.restart);
        strings.put(S.coming_soon, R.string.coming_soon);
        strings.put(S.ingame, R.string.ingame);
        strings.put(S.training_mode, R.string.training_mode);
        strings.put(S.competition, R.string.competition);
        strings.put(S.profile, R.string.profile);
        strings.put(S.help, R.string.help);
        strings.put(S.about, R.string.about);
        strings.put(S.scale, R.string.scale);
        strings.put(S.recording_enabled, R.string.recording_enabled);
        strings.put(S.ghost_enabled, R.string.ghost_enabled);
        strings.put(S.perspective, R.string.perspective);
        strings.put(S.shadows, R.string.shadows);
        strings.put(S.driver_sprite, R.string.driver_sprite);
        strings.put(S.bike_sprite, R.string.bike_sprite);
        strings.put(S.input, R.string.input);
        strings.put(S.active_camera, R.string.active_camera);
        strings.put(S.vibrate_on_touch, R.string.vibrate_on_touch);
        strings.put(S.show_keyboard, R.string.show_keyboard);
        strings.put(S.confirm_clear, R.string.confirm_clear);
        strings.put(S.erase_text1, R.string.erase_text1);
        strings.put(S.erase_text2, R.string.erase_text2);
        strings.put(S.cleared, R.string.cleared);
        strings.put(S.cleared_text, R.string.cleared_text);
        strings.put(S.confirm_reset, R.string.confirm_reset);
        strings.put(S.reset_text1, R.string.reset_text1);
        strings.put(S.reset_text2, R.string.reset_text2);
        strings.put(S.reset, R.string.reset);
        strings.put(S.reset_text, R.string.reset_text);
        strings.put(S.full_reset, R.string.full_reset);
        strings.put(S.clear_highscore, R.string.clear_highscore);
        strings.put(S.objective, R.string.objective);
        strings.put(S.objective_text, R.string.objective_text);
        strings.put(S.keys, R.string.keys);
        strings.put(S.keyset_text, R.string.keyset_text);
        strings.put(S.unlocking, R.string.unlocking);
        strings.put(S.unlocking_text, R.string.unlocking_text);
        strings.put(S.highscores, R.string.highscores);
        strings.put(S.highscore_text, R.string.highscore_text);
        strings.put(S.options_text, R.string.options_text);
        strings.put(S.about_text, R.string.about_text);
        strings.put(S.saved, R.string.saved);
        strings.put(S.tracks_completed_tpl, R.string.tracks_completed_tpl);
        strings.put(S.level_completed_text, R.string.level_completed_text);
        strings.put(S.congratulations, R.string.congratulations);
        strings.put(S.league_unlocked, R.string.league_unlocked);
        strings.put(S.league_unlocked_text, R.string.league_unlocked_text);
        strings.put(S.next, R.string.next);
        strings.put(S.no_highscores, R.string.no_highscores);
        strings.put(S.start, R.string.start);
        strings.put(S.complete_to_unlock, R.string.complete_to_unlock);
        strings.put(S.unlock_all, R.string.unlock_all);
        strings.put(S.skip_track, R.string.skip_track);
        strings.put(S.skip_track_text, R.string.skip_track_text);
        strings.put(S.god_mode, R.string.god_mode);
        strings.put(S.unlock_all_text, R.string.unlock_all_text);
        strings.put(S.level, R.string.level);
        strings.put(S.track, R.string.track);
        strings.put(S.finished_places, R.array.finished_places);
        strings.put(S.keyset, R.array.keyset);
        strings.put(S.on_off, R.array.on_off);

        strings.put(S.backWheelDotColor, R.string.backWheelDotColor);
        strings.put(S.frontWheelDotColor, R.string.frontWheelDotColor);
        strings.put(S.backWheelsColor, R.string.backWheelsColor);
        strings.put(S.backWheelsSpokeColor, R.string.backWheelsSpokeColor);
        strings.put(S.frontWheelsColor, R.string.frontWheelsColor);
        strings.put(S.frontWheelsSpokeColor, R.string.frontWheelsSpokeColor);
        strings.put(S.forkColor, R.string.forkColor);
        strings.put(S.drawWheelLines, R.string.drawWheelLines);
        strings.put(S.drawWheelSprite, R.string.drawWheelSprite);
        strings.put(S.bikeLinesColor, R.string.bikeLinesColor);
        strings.put(S.bikeColor, R.string.bikeColor);
        strings.put(S.bikerHeadColor, R.string.bikerHeadColor);
        strings.put(S.bikerLegColor, R.string.bikerLegColor);
        strings.put(S.bikerBodyColor, R.string.bikerBodyColor);
        strings.put(S.steeringColor, R.string.steeringColor);
        strings.put(S.infoMessageColor, R.string.infoMessageColor);
        strings.put(S.progressBackgroundColor, R.string.progressBackgroundColor);
        strings.put(S.progressColor, R.string.progressColor);
        strings.put(S.splashColor, R.string.splashColor);
        strings.put(S.lockSkinIndex, R.string.lockSkinIndex);
        strings.put(S.menuBackgroundColor, R.string.menuBackgroundColor);
        strings.put(S.keyboardTextColor, R.string.keyboardTextColor);
        strings.put(S.keyboardBackgroundColor, R.string.keyboardBackgroundColor);
        strings.put(S.menuTitleTextColor, R.string.menuTitleTextColor);
        strings.put(S.menuTitleBackgroundColor, R.string.menuTitleBackgroundColor);
        strings.put(S.frameBackgroundColor, R.string.frameBackgroundColor);
        strings.put(S.mainMenuBackgroundColor, R.string.mainMenuBackgroundColor);
        strings.put(S.textColor, R.string.textColor);
        strings.put(S.gameBackgroundColor, R.string.gameBackgroundColor);
        strings.put(S.trackLineColor, R.string.trackLineColor);
        strings.put(S.perspectiveColor, R.string.perspectiveColor);
        strings.put(S.startFlagColor, R.string.startFlagColor);
        strings.put(S.finishFlagColor, R.string.finishFlagColor);
    }

    public String s(Integer r) {
        return GDActivity.shared.getString(r);
    }

    public String s(S key) {
        return s(strings.get(key));
    }

    public String[] getStringArray(int r) {
        return GDActivity.shared.getResources().getStringArray(r);
    }

    public String[] getStringArray(S r) {
        return GDActivity.shared.getResources().getStringArray(strings.get(r));
    }
}
