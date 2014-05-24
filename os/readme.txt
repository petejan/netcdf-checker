*************************************************************************
*									*
*	Author : Coriolis GDAC						*
*	Creation date : 31/03/2008					*
*	Role : Oceansites checker					*
*									*
*************************************************************************
*									*
*	History : 31/03/2008 AB : 1.2 version				*
*		  04/04/2008 AB : 1.4 version, windows support		*
*		  16/09/2008 AB : 1.7 version				*
*		  21/01/2009 TC : update of OceanSITES rule file	*
*		  09/09/2009 TC : add oceansites_profiles_1.1.xml for	*
*				  vertical profiles			*
*		  09/11/2009 TC : add credit to EuroSITES       	*
*		  07/05/2014 MG : add OceanSitesv1.3_time-series.xml    *
*				  OceanSITES V1.3 format		*
*									*
*	The file format checker produces a report on the format 	*
*	of a NetCDF file on a windows or Unix platfrom.			*
*									*
*       Credit                                                          *
*       This software development was funded by the EurosSITES project  *
*       (EU FP7).                                                       *
*									*
*************************************************************************

1. Download http://www.ifremer.fr/co/etc/oceansites/checker/oceansites_format_checker.tar.gz

2. Configure variables in control.csh and/or control.bat :
You have to change application path.
You may check the Java environment variables in control.csh.

3. Launch the checker :
control.csh path/OceanSites_file.nc

4. Read the xml report.


Example :
control.csh OS_2007_DYFAMED_CTD.nc
<?xml version="1.0"?>
<coriolis_function_report>
        <function>CO-03-08-03</function>
        <comment>Control file data format</comment>
        <date>09/09/2009 16:43:40</date>
        <netcdf_file>/home7/illiec/perso/tcarval/tmp/OS_2007_DYFAMED_CTD.nc</netcdf_file>
        <rules_file>oceansites_profiles_1.1.xml</rules_file>
        <data_type>OceanSITES vertical profile</data_type>
        <format_version>1.1</format_version>
        <file_compliant>yes</file_compliant>
        <status>ok</status>
</coriolis_function_report>

