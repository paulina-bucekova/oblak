package org.oblak.server.util;

/**
 * Timer that works in milliseconds.
 */
public class MsTimer {
	private long duration;
	private long startTimestamp;

	/**
	 * Constructor
	 * @param duration Duration of the timer.
	 */
	public MsTimer(long duration){
		this.duration = duration;
		this.startTimestamp = 0;
	}
	
	/**
	 * Starts the timer.
	 */
	public void start()	{
		startTimestamp = System.nanoTime();
	}

	/**
	 * Stops the timer.
	 */
	public void reset(){
		startTimestamp = 0;
	}
	
	/**
	 * @return Whether the timer has been started.
	 */
	public boolean hasStarted(){
		return startTimestamp != 0;
	}
	
	/**
	 * @return If the timer has finished.
	 */
	public boolean hasFinished(){
		boolean finished = false;

		if (startTimestamp != 0){
			long elapsed = System.nanoTime() - startTimestamp;
			finished = elapsed / 1000000 >= duration;		
		}		
		return finished;
	}
}
