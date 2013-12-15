package org.imos.mooring.abos.netcdf;

//Copyright (c) 2013, Peter Jansen
//All rights reserved.
//
//Redistribution and use in source and binary forms, with or without modification, are permitted provided 
//that the following conditions are met:
//
//1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
//
//2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer 
// in the documentation and/or other materials provided with the distribution.
//
//3. Neither the name of the IMOS nor the names of its contributors may be used to endorse or promote products derived 
// from this software without specific prior written permission.
//
//THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, 
//INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
//IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
//OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
//OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, 
//OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.



import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ucar.ma2.DataType;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.Variable;
import ucar.nc2.dataset.CoordinateAxis1DTime;
import ucar.nc2.dataset.NetcdfDataset;
import ucar.nc2.dataset.VariableDS;
import ucar.nc2.time.CalendarDate;
import ucar.units.StandardUnitDB;
import ucar.units.Unit;
import ucar.units.UnitDBException;

public class FileChecker 
{
	static Logger logger = Logger.getLogger(XMLfileChecker.class);

	String fileName;
	int pass = 0;
	int fail = 0;
	
	public FileChecker(String filename)
	{
		fileName = filename;		
	}
		
	public int checkGlobalExists(NetcdfDataset nc, String v)
	{
		Attribute check = nc.findGlobalAttribute(v);
		if (check == null)
		{
			logger.info("FAIL::Global Attribute does not exist : " + v);
			
			fail++;
			return 0;
		}
		
		if (!check.isString())
		{
			logger.info("FAIL::Global Attribute not String Type : " + v);
			
			fail++;
			return 0;
		}
				
		pass += 1;
		
		return 1;
	}
	
	public int checkGlobalFloatExists(NetcdfDataset nc, String v)
	{
		Attribute check = nc.findGlobalAttribute(v);
		if (check == null)
		{
			logger.info("FAIL::Global Attribute does not exist : " + v);
			
			fail++;
			return 0;
		}
		
		if (!check.getDataType().isFloatingPoint())
		{
			logger.info("FAIL::Global Attribute not float Type : " + v);
			
			fail++;
			return 0;
		}
				
		pass += 1;
		
		return 1;
	}
	public int checkGlobalValue(NetcdfDataset nc, String v, String s)
	{
		Attribute check = nc.findGlobalAttribute(v);
		if (check == null)
		{
			logger.info("FAIL::Global Attribute does not exist : " + v);
			
			fail++;
			return 0;
		}
		
		if (!check.isString())
		{
			logger.info("FAIL::Global Attribute not String Type : " + v);
			
			fail++;
			return 0;
		}
				
		if (!check.getStringValue().matches(s))
		{
			logger.info("FAIL::Global Attribute wrong value for : " + v + " is " + check.getStringValue() + " should be " + s);
			
			fail++;
			return 0;
		}
		pass += 1;
		
		return 1;
	}
	
	String conventions = "Unknown";
	
	public int checkGlobals(NetcdfDataset nc)
	{
		logger.info("CHECK GLOBALS:");
		// iterate over globals
		List<Attribute> globals = nc.getGlobalAttributes();
		Iterator<Attribute> gi = globals.iterator();
		while (gi.hasNext())
		{
			Attribute att = (Attribute) gi.next();
			logger.info("GLOBAL: " + att.getFullName());
		}
		
		// From required table 2

		// Check conventions attribute
		Attribute convAtt = nc.findGlobalAttribute("Conventions");
		if (convAtt == null)
		{
			convAtt = nc.findGlobalAttribute("conventions");
		}
		if (convAtt == null)
		{
			logger.info("FAIL::Global no Attribute 'Conventions' probably should give up");
			
			fail++;						
		}
		else
		{
			conventions = convAtt.getStringValue();
			if (conventions.compareTo("CF-1.6;IMOS-1.3") != 0)
			{
				logger.info("FAIL::Global Attribute Conventions should be : CF-1.6;IMOS-1.3");
				
				fail++;			
			}
		}
		
		checkGlobalExists(nc, "project");
		checkGlobalExists(nc, "title");
		checkGlobalExists(nc, "institution");
		checkGlobalExists(nc, "date_created");
		checkGlobalExists(nc, "abstract");
		checkGlobalExists(nc, "keywords");
		checkGlobalValue(nc, "naming_authority", "IMOS");
		if (conventions.contains("IMOS"))
		{
			checkGlobalFloatExists(nc, "geospatial_lat_min");
			checkGlobalFloatExists(nc, "geospatial_lon_min");
			checkGlobalFloatExists(nc, "geospatial_lon_max");
			checkGlobalFloatExists(nc, "geospatial_lon_min");
			checkGlobalFloatExists(nc, "geospatial_vertical_min");
			checkGlobalFloatExists(nc, "geospatial_vertical_max");
		}
		checkGlobalExists(nc, "time_coverage_start");
		checkGlobalExists(nc, "time_coverage_end");
		checkGlobalExists(nc, "data_centre");
		checkGlobalExists(nc, "data_centre_email");
		checkGlobalExists(nc, "author");
		checkGlobalExists(nc, "principal_investigator");
		checkGlobalExists(nc, "citation");
		checkGlobalExists(nc, "acknowledgement");
		checkGlobalExists(nc, "distribution_statement");
		
		// From optional table 3
		checkGlobalExists(nc, "site_code");
		checkGlobalExists(nc, "netcdf_version");
		checkGlobalExists(nc, "geospatial_lat_units");
		checkGlobalExists(nc, "geospatial_lon_units");
		checkGlobalExists(nc, "geospatial_vertical_units");
		checkGlobalExists(nc, "author_email");
		checkGlobalExists(nc, "principal_investigator_email");
		
		return pass;
	}
	
	public int checkDimension(NetcdfDataset nc, String d)
	{
		Dimension check = nc.findDimension(d);
		if (check == null)
		{
			logger.info("FAIL::Dimension does not exist : " + d);
			
			fail++;
			return 0;			
		}
		Variable checkVar = nc.findVariable(check.getShortName());
		if (checkVar == null)				
		{
			logger.info("FAIL::Dimension has no variable does not exist : " + d);
			
			fail++;
			return 0;			
		}
		if (!checkVar.isCoordinateVariable())
		{
			logger.info("FAIL::Dimension variable is not a coordinate variable : " + d);
			
			fail++;
			return 0;			
		}

		pass++;
		return 1;		
	}
	
	public int checkDimensionVariable(NetcdfDataset nc, Dimension dim)
	{
		Variable checkVar = nc.findVariable(dim.getShortName());
		if (checkVar == null)				
		{
			logger.info("FAIL::Dimension has no variable does not exist : " + dim.getShortName());
			
			fail++;
			return 0;			
		}
		if (!checkVar.isCoordinateVariable())
		{
			logger.info("FAIL::Dimension variable is not a coordinate variable : " + dim.getShortName());
			
			fail++;
			return 0;			
		}

		pass++;
		return 1;		
	}
	
	public int checkVariableAttribute(NetcdfDataset nc, Variable v, String s)
	{
		Attribute check =v.findAttribute(s);
		if (check == null)				
		{
			logger.info("FAIL::Variable " + v.getFullName() + " has no attribute : " + s);
			
			fail++;
			return 0;			
		}

		pass++;
		return 1;		
	}
	
	public int checkDimensions(NetcdfDataset nc)
	{
		logger.info("CHECK DIMENSIONS");
		
		// iterate over dimensions
		List<Dimension> dims = nc.getDimensions();
		Iterator<Dimension> di = dims.iterator();
		while (di.hasNext())
		{
			Dimension dim = (Dimension) di.next();
			logger.info("DIMENSION: " + dim.getFullName());
			checkDimensionVariable(nc, dim);
		}
				
		checkDimension(nc, "TIME");
		return pass;
	}
	
	public int checkVariables(NetcdfDataset nc)
	{
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		logger.info("CHECK VARIABLES");
		StandardUnitDB units = null;
		try
		{
			units = StandardUnitDB.instance();
		}
		catch (UnitDBException e)
		{
			logger.info("Cannot find units database, won't check for valid units");
		}
		
		// iterate over variables
		List<Variable> vars = nc.getVariables();
		Iterator<Variable> vi = vars.iterator();
		while (vi.hasNext())
		{
			Variable var = (Variable) vi.next();
			logger.info("VARIABLE: " + var.getFullName() + " " + var.getDimensionsString() + " " + var.getDataType());
			
			// TODO:: add check of dimensions, all must have TIME
			
			List<Attribute> atts = var.getAttributes();
			Iterator<Attribute> ai = atts.iterator();
			while (ai.hasNext())
			{
				Attribute a = (Attribute) ai.next();
				//logger.info("VARIABLE-ATTRIBUTE : " + a.getFullName());
			}
			
			// From table 5
			
			// check standard_name or long_name
			Attribute sn = var.findAttribute("standard_name");
			if (sn == null)
			{
				sn = var.findAttribute("long_name");
				if (sn == null)
				{
					logger.info("FAIL::Variable " + var.getShortName() + " no standard_name or long_name attribute");	
				}
			}
			else
			{
				// Check standard_name for a unit in the cf conventions
				String cfUnit = cfNames.get(sn.getStringValue());
				logger.info("VARIABLE STD-NAME: " + var.getShortName() + " std_name " + sn.getStringValue() +" standard name CF Unit " + cfUnit);
				if (cfUnit == null)
				{
					logger.info("FAIL::Variable " + var.getShortName() + " not standard name : " + sn.getShortName());

					fail++;					
				}
			}
			
			if (var.getShortName().compareTo("TIME") != 0)
			{
				// check for units on non _QC variables
				
				// TODO: could be smarter here, and check that the <var>_QC variable is actually a aux variable <var>
				if (!var.getShortName().endsWith("_QC"))
				{
					checkVariableAttribute(nc, var, "units");
					Attribute ua = var.findAttribute("units");
					if ((ua != null) && (units != null))
					{
						Unit u = units.getByName(ua.getStringValue());
						if (u == null)
						{
							logger.info("FAIL::Variable " + var.getShortName() + " Unknown unit : " + ua.getStringValue());
							
							fail++;					
						}						
					}
				}
				else
				{
					if (var.getDataType() != DataType.BYTE)
					{
						logger.info("FAIL::Variable " + var.getShortName() + " should be byte type");
						
						fail++;
					}
				}				
			}
			else // TIME variable checks
			{
	            VariableDS varTds = new VariableDS(null, var, false);
	            try
				{
					CoordinateAxis1DTime timeAxis = CoordinateAxis1DTime.factory(nc, varTds, null);
					List<CalendarDate> dates = timeAxis.getCalendarDates();
					int tSize = dates.size();
					Date startDate = new Date(dates.get(0).getMillis());
					Date endDate = new Date(dates.get(tSize-1).getMillis());
	                logger.info("VARIABLE TIME:: time start " + sdf.format(startDate) + " end " + sdf.format(endDate));
	                
	                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	                Attribute timeStart = nc.findGlobalAttribute("time_coverage_start");
	                Attribute timeEnd = nc.findGlobalAttribute("time_coverage_end");
	                try
					{
						Date st = sdf2.parse(timeStart.getStringValue());
						Date et = sdf2.parse(timeEnd.getStringValue());
		                logger.info("GLOBAL :: time start " + sdf.format(st) + " end " + sdf.format(et));
		                
		                if (st.before(startDate))
		                {
							logger.info("FAIL::Global time_coverage_start before timerange in TIME variable");			                	
		                }
		                if (et.after(endDate))
		                {
							logger.info("FAIL::Global time_coverage_end after timerange in TIME variable");			                	
		                }
					}
					catch (ParseException e)
					{
						logger.info("FAIL::Global parse time_coverage_start time_coverage_end check " + e.getMessage());			                	
					}
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (var.findAttribute("units").getStringValue().compareTo("days since 1950-01-01T00:00:00Z") != 0)
				{
					logger.info("FAIL::Variable TIME units not 'days since 1950-01-01T00:00:00Z'");	
					
					fail++;
				}
			}
			checkVariableAttribute(nc, var, "valid_min");
			checkVariableAttribute(nc, var, "valid_max");
			
			if (!var.isCoordinateVariable())
			{
				checkVariableAttribute(nc, var, "_FillValue");
				if (!var.getShortName().endsWith("_QC"))
				{
					checkVariableAttribute(nc, var, "quality_control_set");
				}
			}
			else
			{
				checkVariableAttribute(nc, var, "axis");				
			}
		}
				
		return pass;
	}
	
	HashMap<String, String> cfNames = new HashMap<String, String>();
	
	public void openCFxmlFile()
	{
		// Read the standard names table, download the XML file from
		// http://cf-pcmdi.llnl.gov/documents/cf-standard-names
		
		try
		{
			File fXmlFile = new File("cf-standard-name-table.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			// optional, but recommended
			doc.getDocumentElement().normalize();

			logger.info("cf-standard-name-table.xml:: Root element :" + doc.getDocumentElement().getNodeName());

			NodeList nList = doc.getElementsByTagName("entry");

			for (int temp = 0; temp < nList.getLength(); temp++)
			{
				Node nNode = nList.item(temp);

				//logger.info("Current Element :" + nNode.getNodeName());

				if (nNode.getNodeType() == Node.ELEMENT_NODE)
				{

					Element eElement = (Element) nNode;

					//logger.info("id : " + eElement.getAttribute("id"));
					//logger.info("canonical_units : " + eElement.getElementsByTagName("canonical_units").item(0).getTextContent());

					cfNames.put(eElement.getAttribute("id"), eElement.getElementsByTagName("canonical_units").item(0).getTextContent());
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public int check()
	{
		try
		{
			//NetcdfDataset.initNetcdfDatasetCache(1, 10, 1);
			NetcdfDataset nc = NetcdfDataset.acquireDataset(fileName, null); 
		
			logger.info("File Title " + nc.getTitle());
			logger.info("File Type " + nc.getFileTypeId());
			
			checkGlobals(nc);
			logger.info("Check Global " + pass + " fail " + fail);
			logger.info("");
			
			checkDimensions(nc);
			logger.info("Check Dimension " + pass + " fail " + fail);
			logger.info("");

			checkVariables(nc);
			logger.info("Check Variables " + pass + " fail " + fail);
			logger.info("");
			
			nc.close();
			//NetcdfDataset.shutdown();
		}
		catch (java.io.IOException e)
		{
			e.printStackTrace();
		}	
		
		return pass;
	}

	public static void main(String[] args) 
	{
		PropertyConfigurator.configure("log4j.properties");
		String fileName = null;
		
		if (args.length == 1)
			fileName = args[0];
		else
		{
			System.err.println("no netCDF file name specified, exiting ...");
			System.exit(-1);
		}

		FileChecker ck = new FileChecker(fileName);
		
		ck.openCFxmlFile();
		ck.check();
		
		logger.info("Totals pass : " + ck.pass + " fail : " + ck.fail);
		
	}

}
