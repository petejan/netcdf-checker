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
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ucar.ma2.DataType;
import ucar.nc2.Attribute;
import ucar.nc2.Variable;
import ucar.nc2.dataset.CoordinateAxis1DTime;
import ucar.nc2.dataset.VariableDS;
import ucar.nc2.time.CalendarDate;

public class Axis extends Check
{
	static Logger logger = Logger.getLogger(Axis.class.getName());

	SimpleDateFormat sdf;

	public Axis()
	{
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

		sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}

	@Override
	public PassFail check(Element eElement)
	{
		NodeList aN = eElement.getElementsByTagName("variable");
		logger.debug("number to check " + aN.getLength());
		for (int i = 0; i < aN.getLength(); i++)
		{
			Node nAttribute = aN.item(i);
			String varName = nAttribute.getTextContent();
			logger.debug("checking " + varName);

			Variable var = ds.findVariable(varName);

			if (var.findAttribute("axis") == null)
			{
				logger.warn("FAIL::Variable " + var.getShortName() + " has no axis attribute");

				result.fail();
			}
			else
				result.pass();
		}

		return result;
	}

}
