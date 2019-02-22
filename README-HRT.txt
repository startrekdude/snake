A README for the High Resolution Timer component of Snake


Let me tell you the story of a bug. An obnoxious, weird, *impossible* bug.

The bug manifested itself in the following way: The Snake program would
run very slowly whenever Eclipse wasn't open. Whenever Eclipse was open,
it would run normally. This was 100% reproducible.

I know, weird bug, right?

Windows has a concept called the "System Timer Resolution."
The System Timer Resolution "determines how frequently Windows [...]
 [checks] whether a scheduled timer object has expired" (Microsoft, 2010).

By default, Windows sets the System Timer Resolution to 15.6 ms.
In hard mode, Snake sets a timer for 5 ms. Clearly, this won't work.
(Eclipse, for some reason, sets it to ~ 1ms)

Therefore, Snake needs to set the System Timer Resolution when it runs.
Great, how do you do that from Java? Ah, you ... don't?

Well then, I guess I need to write some C. Fun! Always a good time!

The HighResolutionTimer component of Snake calls into the
highresolutiontimer.dll component, which calls the appropriate
timeBeginPeriod and timeEndPeriod Win32 calls.

On non-Windows platforms, HighResolutionTimer does nothing
whatsoever.


Frequently Asked Questions (FAQs) about the High Resolution Timer
component

What about macOS and Linux?
---------------------------
I don't have a macOS or Linux system to test with. If necessary, I'd
be willing to write corresponding native code for these systems too.
Edit 12/11: I can confirm that Snake works perfectly on a Linux
(Bionic Beaver) system without any native code hijinks. I believe it's
safe to say that this trickery is only required for Windows.

Does setting the System Timer Resolution have any negative consequences?
------------------------------------------------------------------------
Yep! It "reduces the battery run time on mobile systems by as much as
25 percent" (Microsoft, 2010). But we can't not set the System Timer
Resolution. Snake needs access to a 5ms timer to run appropriately.
Besides, this is a pretty standard thing for games to do. Even Chrome
will increase the System Timer Resolution when necessary.

How do you think this problem would be handled, in a perfect world?
-------------------------------------------------------------------
Glad you asked! In a perfect world, I think that Java should handle
this the same way web browsers do: when application code requests
a timer of a granularity that the System Timer Resolution cannot
provide, silently increase the System Timer Resolution.
For example setInterval(..., 5), on Chrome will silently (and IMHO
correctly) increase the System Timer Resolution. This works well:
I've never once had to go on a debugging rabbit-hole ending in
an NT Kernel routine while writing javascript ;) .


References

(2010, June 16). Microsoft. Timers, Timer Resolution, and Development of Efficient Code.
    Retrieved December 10, 2018, from http://download.microsoft.com/download/3/0/2/3027
    d574-c433-412a-a8b6-5e0a75d5b237/timer-resolution.docx