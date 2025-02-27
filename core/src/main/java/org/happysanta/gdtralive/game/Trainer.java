package org.happysanta.gdtralive.game;

import org.happysanta.gdtralive.game.engine.Engine;
import org.happysanta.gdtralive.game.api.external.GdStr;
import org.happysanta.gdtralive.game.api.model.FullEngineState;
import org.happysanta.gdtralive.game.api.Strings;

import java.util.ArrayList;
import java.util.List;

public class Trainer {
    private final Engine engine;
    private final GdView view;
    private final GdStr str;

    private List<FullEngineState> states = new ArrayList<>();
    private boolean trainingMode = false; //todo
    private int attempt = 1;

    public Trainer(Engine engine, GdView view, GdStr str) {
        this.str = str;
        this.engine = engine;
        this.view = view;
    }

    public void setSavepoint() {
        if (!trainingMode) {
            trainingMode = true;
        }
        states.add(engine.getFullState());
        engine.setRespawn(engine.getState());
        view.showInfoMessage(str.s(Strings.ATTEMPT) + attempt, 1000);
    }

    public void onCrash(Runnable execution) {
        if (trainingMode) {
            engine.setState(states.get(states.size() - 1));
            attempt++;
            view.showInfoMessage(str.s(Strings.ATTEMPT) + " " + attempt, 1000);
            Achievement.achievements.get(Achievement.Type.BACK_TO_SCHOOL).increment();
        } else {
            execution.run();
        }
    }

    public void stop() {
        trainingMode = false; //todo
        attempt = 1;
        engine.setRespawn(null);
        states = new ArrayList<>();
    }

    public void prepare() {
        states = new ArrayList<>();
        states.add(engine.getFullState());
        engine.setRespawn(null);
    }

    public boolean isTrainingMode() {
        return trainingMode;
    }
}
