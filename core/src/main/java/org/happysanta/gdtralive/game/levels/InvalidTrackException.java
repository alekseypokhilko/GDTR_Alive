package org.happysanta.gdtralive.game.levels;

public class InvalidTrackException extends Exception {

	public InvalidTrackException(Exception e) {
		super(e);
	}

	public InvalidTrackException(String message) {
		super(message);
	}
}
