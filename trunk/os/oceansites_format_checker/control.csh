#!/bin/sh
#*******************************************************************************
# 
# MOD : FormatControl
# ROL : Check if a netcdf file is compliant with a format definition (argo, oco,
#       oceansites, ...)
# AUT : Ifremer
# VER : control.csh applicationVersion
#
#*******************************************************************************


#-------------------------------------------------------------------------------
# Configuration
# JAVA_HOME is the path where the java runtime environment (jre) is installed
# JAVA_BIN is the path where the java binaries are installed
# EXE is the path where the application FormatControl is installed
#-------------------------------------------------------------------------------
#set JAVA_HOME=/export/home/java/jre1.6
#set EXE=/home/coriolis_dev/val/binlx/co03/co0308/co030803/exe

#-------------------------------------------------------------------------------
# Launch the application
#-------------------------------------------------------------------------------
CLASSPATH=./RULES:./CLASSES:./PROPERTIES:./LIB/netcdfUI-4.2.jar:./LIB/nlog4j-1.2.25.jar:./LIB/xercesImpl.jar
echo java -cp $CLASSPATH -Dapplication.properties=application.properties oco.FormatControl $1



