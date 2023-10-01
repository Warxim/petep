@echo off

rem Useful params, change if needed
set JAVA_EXE=java.exe
set JAVAW_EXE=java.exe
set APP_HOME=%~dp0
set WORKING_DIR=%APP_HOME%
set CMD_LINE_ARGS=%*
set DEFAULT_JVM_OPTS=
set CLASSPATH=%APP_HOME%\lib\*
set MAIN_CLASS="com.warxim.petep.Main"

rem Set working directory (important for petep.json), by default the startup directory is used
cd /D "%WORKING_DIR%"

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
