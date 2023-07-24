@echo off

set CAPELLA_HOME=C:\dev\vv\TeamForCapella-6.1.0\TeamForCapella\capella
set ECLIPSE_EXE=%CAPELLA_HOME%\capellac.exe

REM Path for a folder
set P2_NAME=%inject:project.fullname%.site

set P2_LOCATION=file:///%~dp0../../../releng/%P2_NAME%/target/repository/
REM Path for an archive
REM set P2_LOCATION=jar:file:///%~dp0../../../releng/%P2_NAME%/target/%P2_NAME%-1.0.0-SNAPSHOT.zip!/

REM Syntax for several P2 locations
set ECLIPSE_P2=https://download.eclipse.org/releases/2021-06
set P2_SET=%P2_LOCATION%,%ECLIPSE_P2%

set P2_SET=%P2_LOCATION%

set EXTENSION_FEATURES=%inject:project.fullname%.oax.feature.feature.group
set EXTENSION_FEATURES=%EXTENSION_FEATURES%,%inject:project.fullname%.cdo.feature.feature.group

set WS=%~dp0..\..\..\releng\%P2_NAME%\target\ws_install

"%ECLIPSE_EXE%" ^
 -clean -noSplash -console -consoleLog ^
 -data %WS% ^
 -application org.eclipse.equinox.p2.director ^
 -purgeHistory ^
 -profile DefaultProfile ^
 -tag InstallOAx ^
 -repository %P2_SET%  ^
 -installIUs %EXTENSION_FEATURES%

