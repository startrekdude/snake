/*
 * Date: 12/10/2018
 * Dev: Sam Haskins
 * Version: 1.0
 */
package com.gmail.cheesedude54321.utility;

import javax.swing.JOptionPane;

/*
 * Name: Sam Haskins
 * Date: 12/10/2018
 * Inputs: The action to take
 * Outputs: On Windows, enables/disables the high resolution timer
 * Description:
 *     A part-Java, part-C component that enables high resolution timers
 *     on Windows. Please read README-HRT for the rationale and details.
 */
public final class HighResolutionTimer {
    private static Boolean isWindows;
    
    /*
     * Name: Sam Haskins
     * Date: 12/10/2018
     * Inputs: None
     * Outputs: On Windows platforms, tries to load the native library
     * Description:
     *     If the platform is Windows, loads the native
     *     highresolutiontimer.dll library. If this fails, tells the
     *     user and exits.
     */
    static {
        // This is only relevant on Windows
        String osName = System.getProperty("os.name");
        isWindows = osName.toLowerCase().indexOf("win") >= 0;
        if (isWindows) {
            try {
                // Try to load the native code
                // JNI for the win! I was sooo happy when this worked, first try!
                System.loadLibrary("highresolutiontimer");
            } catch (Throwable e) {
                // Oh well. We can't proceed without the native library
                // due to our inability to invoke Win32 functions from Java
                String message = "A native component required to enable high" +
                        " resolution timers failed to load. Please ensure" +
                        " highresolutiontimer.dll is in the same directory" +
                        " as the Java program. The program will not start.\n\n" +
                        "Details of the error are: " +
                        ThrowableUtils.getStackTrace(e);
                JOptionPane.showMessageDialog(null, message, "An error occurred",
                                              JOptionPane.ERROR_MESSAGE);
                System.exit(-1);
            }
        }
    }
    
    /*
     * Name: Sam Haskins
     * Date: 12/10/2018
     * Language: C
     * Inputs: The requested timer resolution
     * Outputs: Calls the Win32 method of the same name to request a high resolution timer
     * Description:
     *     Read README-HRT for rationale
     *     Sets the System Timer Resolution to a specified value
     */
    private static native int timeBeginPeriod(int uPeriod);
    
    /*
     * Name: Sam Haskins
     * Date: 12/10/2018
     * Language: C
     * Inputs: The previously-set timer value
     * Outputs: Releases the process' hold on the System Timer Resolution
     * Description:
     *     Read README-HRT for rationale
     *     Unsets the System Timer Resolution
     */
    private static native int timeEndPeriod(int uPeriod);
    
    /*
     * Name: Sam Haskins
     * Date: 12/10/2018
     * Inputs: None
     * Outputs: On Windows, enables support for high resolution timers
     * Description:
     *     On Windows, invokes timeBeginPeriod 1 to enable high resolution timers
     *     On other platforms, does nothing
     */
    public static void enable() {
        if (isWindows) {
            // Set high resolution timers
            timeBeginPeriod(1);
        }
    }
    
    /*
     * Name: Sam Haskins
     * Date: 12/10/2018
     * Inputs: None
     * Outputs: On Windows, disables support for high resolution timers
     * Description:
     *     On Windows, invokes timeEndPeriod 1 to disable high resolution timers
     *     On other platforms, does nothing
     */
    public static void disable() {
        if (isWindows) {
            // Unset high resolution timers
            timeEndPeriod(1);
        }
    }
}