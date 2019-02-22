@echo off

:: x64
call build-arch.cmd "C:\Program Files (x86)\Microsoft Visual Studio\2017\Community\VC\Auxiliary\Build\vcvars64.bat" "highresolutiontimer.dll"

:: x86
call build-arch.cmd "C:\Program Files (x86)\Microsoft Visual Studio\2017\Community\VC\Auxiliary\Build\vcvarsamd64_x86.bat" "highresolutiontimer-x86.dll"