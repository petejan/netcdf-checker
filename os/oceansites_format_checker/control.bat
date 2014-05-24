@echo off
rem ********************************************************************************
rem * 
rem * MOD : FormatControl
rem * ROL : Check if a netcdf file is compliant with a format definition (argo, oco,
rem *       oceansites, ...)
rem * AUT : Ifremer
rem * VER : control.bat applicationVersion
rem *
rem ********************************************************************************


rem --------------------------------------------------------------------------------
rem - Configuration
rem - JAVA_HOME is the path where the java runtime environment (jre) is installed
rem - JAVA_BIN is the path where the java binaries are installed
rem - DISK is the letter of the disk where the application FormatControl is installed
rem - EXE is the path where the application FormatControl is installed
rem --------------------------------------------------------------------------------
SET JAVA_HOME="D:\Java\jdk1.6.0_25"
SET JAVA_BIN=D:\Java\jdk1.6.0_25\bin
SET DISK=D:
SET EXE=D:\workspaceEclipse\FormatControl



rem --------------------------------------------------------------------------------
rem - Launch the application
rem --------------------------------------------------------------------------------
SET CLASSPATH=%JAVA_HOME%;.;./RULES;./CLASSES;./PROPERTIES;./LIB/netcdfUI-4.2.jar;./LIB/nlog4j-1.2.25.jar;./LIB/xercesImpl.jar
%DISK%
cd %EXE%
"%JAVA_BIN%\java" -cp %CLASSPATH% -Dapplication.properties=application.properties oco.FormatControl %1
pause


