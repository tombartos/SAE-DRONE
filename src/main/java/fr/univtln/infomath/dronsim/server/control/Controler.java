package fr.univtln.infomath.dronsim.server.control;

/**
 * Interface for controlling a simulated drone.
 *
 * This is the interface through which a simulated drone gets its servo motor
 * input and sends its sensors output. It is analogous to the microcontroller on
 * a physical drone.
 */
public interface Controler {
    /**
     * Get throttle value for motor.
     *
     * Full speed is 1.0f, 0.0f is stopped, -1.0f is full speed in reverse.
     */
    public float getMotorThrottle(int motorIndex);

    /**
     * Get the value of a sensor.
     *
     * The sensor is identified by its index.
     */
    public void setSensorValue(int captorIndex, int[] values);

    /**
     * Get the value of a sensor.
     *
     * The sensor is identified by its index.
     */
    public void setSensorValue(int captorIndex, float[] values);

    /**
     * Get the value of a sensor.
     *
     * The sensor is identified by its index.
     */
    public void setSensorValue(int captorIndex, long[] values);

    /**
     * Frees resources associated with this controller.
     *
     * Resources associated with this controller are freed and its behaviour becomes
     * unspecified. The drone should call this method when it is destroyed.
     */
    public void destroy();
}
