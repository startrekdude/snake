#include "com_gmail_cheesedude54321_utility_HighResolutionTimer.h"

#include <windows.h>

/*
 * DllMain stub
 */
BOOL WINAPI DllMain(
  _In_ HINSTANCE hinstDLL,
  _In_ DWORD     fdwReason,
  _In_ LPVOID    lpvReserved
  ) {
	return TRUE;
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
JNIEXPORT jint JNICALL Java_com_gmail_cheesedude54321_utility_HighResolutionTimer_timeBeginPeriod
  (JNIEnv * env, jclass cls, jint uPeriod) {
	return timeBeginPeriod(uPeriod);
}


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
JNIEXPORT jint JNICALL Java_com_gmail_cheesedude54321_utility_HighResolutionTimer_timeEndPeriod
  (JNIEnv * env, jclass cls, jint uPeriod) {
	return timeEndPeriod(uPeriod);
}