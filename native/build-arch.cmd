@echo off

:: Build script for highresolutiontimer.dll
set JDK_PATH=C:\Program Files\Java\jdk-10.0.2
set VCVARSALL=%1
set OUT=%2

:: Generate the headers
"%JDK_PATH%\bin\javac" -cp "..\bin" "..\src\com\gmail\cheesedude54321\utility\HighResolutionTimer.java" -h .

:: We need a C compiler, ofc
call %VCVARSALL%

:: Compile
cl /c /MT /I "%JDK_PATH%\include" /I "%JDK_PATH%\include\win32" /TC highresolutiontimer.c

:: Link
link /DLL /NODEFAULTLIB /ENTRY:DllMain highresolutiontimer.obj winmm.lib /OUT:%OUT%

copy %OUT% ..