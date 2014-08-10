package org.imos.mooring.abos.netcdf.check;

//Copyright (c) 2013, Peter Jansen
//All rights reserved.
//
//Redistribution and use in source and binary forms, with or without modification, are permitted provided 
//that the following conditions are met:
//
//1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
//
//2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer 
//in the documentation and/or other materials provided with the distribution.
//
//3. Neither the name of the IMOS nor the names of its contributors may be used to endorse or promote products derived 
//from this software without specific prior written permission.
//
//THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, 
//INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
//IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
//OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
//OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, 
//OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import ucar.ma2.DataType;
import ucar.nc2.Attribute;
import ucar.nc2.Variable;
import ucar.nc2.dataset.CoordinateAxis1DTime;
import ucar.nc2.dataset.VariableDS;
import ucar.nc2.time.CalendarDate;

public class Time extends Check
{
	static Logger logger = Logger.getLogger(Time.class.getName());
	
	SimpleDateFormat sdf;
	public Time()
	{
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");		
	}
	@Override
	public PassFail check(Element eElement)
	{
		String checkName = eElement.getAttribute("name");
		
		Variable var = ds.findVariable(eElement.getElementsByTagName("variable").item(0).getTextContent());
        VariableDS varTds = new VariableDS(null, var, false);
        try
		{
			CoordinateAxis1DTime timeAxis = CoordinateAxis1DTime.factory(ds, varTds, null);
			List<CalendarDate> dates = timeAxis.getCalendarDates();
			int tSize = dates.size();
			Date startDate = new Date(dates.get(0).getMillis());
			Date endDate = new Date(dates.get(tSize-1).getMillis());
            logger.info("VARIABLE TIME:: time start " + sdf.format(startDate) + " end " + sdf.format(endDate));
            
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            Attribute timeStart = ds.findGlobalAttribute("time_coverage_start");
            Attribute timeEnd = ds.findGlobalAttribute("time_coverage_end");
            try
			{
				Date st = sdf2.parse(timeStart.getStringValue());
				Date et = sdf2.parse(timeEnd.getStringValue());
                logger.info("GLOBAL       :: time start " + sdf.format(st) + " end " + sdf.format(et));
                
                if (st.after(startDate))
                {
					logger.warn("FAIL::Global time_coverage_start before timerange in TIME variable");		
					result.fail();
                }
                else 
                	result.pass();
                
                if (et.after(endDate))
                {
					logger.warn("FAIL::Global time_coverage_end after timerange in TIME variable");			                	
					result.fail();
                }
                else 
                	result.pass();
			}
			catch (ParseException e)
			{
				logger.warn("FAIL::Global parse time_coverage_start time_coverage_end check " + e.getMessage());			                	
				result.fail();
			}
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (!var.findAttribute("units").getStringValue().matches("days since 1950-01-01[T ]00:00:00 UTC"))
		{
			logger.warn("FAIL::Variable TIME units not 'days since 1950-01-01T00:00:00 UTC' is '" + var.findAttribute("units").getStringValue() + "'");	
			
			result.fail();
		}
        else 
        	result.pass();
				
		return result;
	}

}
