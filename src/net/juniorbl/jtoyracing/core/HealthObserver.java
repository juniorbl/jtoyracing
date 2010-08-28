package net.juniorbl.jtoyracing.core;

/**
 * Define methods to the health observers (Observer pattern).
 *
 * @version 1.0 Dec 23, 2007
 * @author Carlos Luz Junior
 */
public interface HealthObserver {

	/**
	 * Notification when the health ends.
	 */
	void healthEnded();
}
