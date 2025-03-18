package org.happysanta.gdtralive.game;

import org.happysanta.gdtralive.game.api.model.FullEngineState;
import org.happysanta.gdtralive.game.engine.Engine;

import java.util.ArrayList;
import java.util.List;

public class Trainer {
    private final Game game;
    private final Engine engine;

    private List<FullEngineState> states = new ArrayList<>();
    private boolean trainingMode = false; //todo

    public Trainer(Engine engine, Game game) {
        this.game = game;
        this.engine = engine;
    }

    public void setSavepoint() {
        if (!trainingMode) {
            trainingMode = true;
        }
        states.add(engine.getFullState());
        engine.setRespawn(engine.getState());
    }

    public void onCrash(Runnable execution) {
        if (trainingMode) {
            engine.setState(states.get(states.size() - 1));
            game.attemptCount++;
            //Achievement.achievements.get(Achievement.Type.BACK_TO_SCHOOL).increment();
        } else {
            execution.run();
        }
    }

    public void stop() {
        trainingMode = false; //todo
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
