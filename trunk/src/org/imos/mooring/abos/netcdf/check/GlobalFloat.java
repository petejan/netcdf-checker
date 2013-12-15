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

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import ucar.ma2.DataType;
import ucar.nc2.Attribute;

public class GlobalFloat extends Check
{
	static Logger logger = Logger.getLogger(GlobalFloat.class.getName());
	
	@Override
	public PassFail check(Element eElement)
	{
		String checkName = eElement.getAttribute("name");
		
		for(int i=0;i<eElement.getElementsByTagName("attribute").getLength();i++)
		{
			String varName = eElement.getElementsByTagName("attribute").item(i).getTextContent();
			Attribute check = ds.findGlobalAttribute(varName.trim());
			if (check == null)
			{
				logger.warn("FAILED:: " + checkName + " ATTRIBUTE " + varName);
				result.fail();
			}
			else
			{
				if ((check.getDataType() == DataType.FLOAT) || (check.getDataType() == DataType.DOUBLE))
				{
					result.pass();
				}
				else
				{
					logger.warn("FAILED:: " + checkName + " ATTRIBUTE " + varName + " not float is " + check.getDataType());
					
					result.fail();
				}
			}
		}
				
		return result;
	}

}
