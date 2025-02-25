package org.happysanta.gdtralive.game.api.exception;

public class InvalidTrackException extends Exception {

	public InvalidTrackException(Exception e) {
		super(e);
	}

	public InvalidTrackException(String message) {
		super(message);
	}
}
