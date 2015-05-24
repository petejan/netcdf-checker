OceanSITES googlecode repository is now at https://code.google.com/p/oceansites/

Java code for checking compliance with IMOS and OceanSITES data format.

This is a eclipse Java project that allows rules to be configured via a XML file.

Checks are java classes configure from an input XML file.

Run with

java -jar FileChecker.jar -x tests\tests.xml <netcdf.nc>

Current Tests:

    Dimensions

        checks DIMENSIONS have an associated Coordinate variable, and also check any required dimensions with the XML tag

        <dimension-required>

    Globals

        Checks global attributes exists and is a String variable 

    GlobalFloat

        Checks global attributes exists and is a Float variable 

    StandardNames

        Checks any variable attribute standard name is in the CF conventions standard_name catalogue 

    StdUnits

        Checks any variable (non coordinate variable) unit attribute is in the UnitDB 

    Name

        Checks variables have a standard_name or a long_name 

    AncillaryVariableAttributes

        Checks any Ancillary variables attributes, Also check ancilary variables have the same dimensions as its main variable 

    Time

        Checks that the time DIMENSION exists, and the TIME variable parses, Check time range in time_coverage_start, time_coverage_end Check time units is 'days since 1950-01-01T00:00:00Z' 

    VariableAttribute

        Check that for any non-ancillary variable, attributes exist 

Other work

    https://pypi.python.org/pypi/cfchecker/2.0.2
    http://www.ifremer.fr/co/etc/oceansites/checker/ 
