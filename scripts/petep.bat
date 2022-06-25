@echo off
set JAVA_EXE=java.exe
set JAVAW_EXE=javaw.exe
set DIRNAME=%~dp0
set APP_HOME=%DIRNAME%
set CMD_LINE_ARGS=%*
set DEFAULT_JVM_OPTS=
set CLASSPATH=%APP_HOME%\lib\*
set MAIN_CLASS="com.warxim.petep.Main"

if "%~2"=="--nogui" (
	goto NOGUI
)

goto GUI

:NOGUI
	rem Start PETEP without GUI (let console open).
	"%JAVA_EXE%" %DEFAULT_JVM_OPTS% -classpath "%CLASSPATH%" %MAIN_CLASS% %CMD_LINE_ARGS%
	goto DONE
:GUI
	rem Start PETEP with GUI (without console).
	start "" /b "%JAVAW_EXE%" %DEFAULT_JVM_OPTS% -classpath "%CLASSPATH%" %MAIN_CLASS% %CMD_LINE_ARGS%
	goto DONE
:DONE
