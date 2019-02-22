@echo off

:: ENV VARS
set JRE=C:\Program Files\Java\jre-10.0.2
set JDK=C:\Program Files\Java\jdk-10.0.2

echo Building Win64 installer for Snake...

echo Copying required files to appdir...
copy ..\Snake.jar appdir
copy ..\highresolutiontimer.dll appdir
copy ..\THIRD-PARTY.txt appdir
copy ..\Snake-Guide.pdf appdir

echo Building JRE...
rmdir /q /s "appdir\jre-10.0.2"
"%JDK%\bin\jlink.exe" -G -p "%JDK%\jmods" --no-header-files --no-man-pages --compress 1 --add-modules java.desktop --output "appdir\jre-10.0.2"

echo Building launcher...
"%JRE%\bin\java.exe" -jar "launch4j\launch4j.jar" snake-launch.xml

echo Building MSI...
echo Collecting deliverables...

wix\heat dir appdir -ag -srd -cg SnakeProgram -dr INSTALLDIR -out SnakeProgram.wxs -swall
wix\candle -arch x64 SnakeProgram.wxs

echo Compiling installer script...
wix\candle -arch x64 Snake.wxs

echo Linking installer...
wix\light -b appdir -ext WixUIExtension -ext WixUtilExtension Snake.wixobj SnakeProgram.wixobj -o Snake.msi
