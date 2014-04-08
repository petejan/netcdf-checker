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


import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ucar.nc2.Attribute;
import ucar.nc2.dataset.NetcdfDataset;

public class Globals extends Check
{
	static Logger logger = Logger.getLogger(Globals.class.getName());
	
	public Globals()
	{
		result = new PassFail();		
	}
	
	@Override
	public PassFail check(Element eElement)
	{
		String checkName = eElement.getAttribute("name");
		
		NodeList aN = eElement.getElementsByTagName("attribute");
		for(int i=0;i<aN.getLength();i++)
		{
			Node nAttribute = aN.item(i);
			NamedNodeMap nNM = nAttribute.getAttributes();
			
			Pattern p = null;
			String type = null;
			
			for(int j=0;j<nNM.getLength();j++)
			{
				//System.out.println("nNM " + nNM.item(j).getNodeName());
				if (nNM.item(j).getNodeName().equals("regex"))
				{
					p = Pattern.compile(nNM.item(j).getNodeValue());
				}
				if (nNM.item(j).getNodeName().equals("type"))
				{
					type = nNM.item(j).getNodeValue();
				}
			}
			String varName = nAttribute.getTextContent();

			Attribute check = ds.findGlobalAttribute(varName.trim());
			if (check == null)
			{
				logger.warn("FAILED:: " + checkName + " ATTRIBUTE " + varName);
				result.fail();
			}
			else
			{
				if ((p != null) && (check.isString())) // can only really regex a string type
				{
					String val = check.getStringValue();
					if (p.matcher(val).matches())
					{
						result.pass();
					}
					else
					{
						logger.warn("FAILED:: " + checkName + " ATTRIBUTE " + varName + " failed regex " + p + " is " + check.getStringValue());
						result.fail();						
					}
				}
				else if (type != null)
				{
					if (check.getDataType().toString().equals(type))
					{
						result.pass();
					}
					else
					{
						logger.warn("FAILED:: " + checkName + " ATTRIBUTE " + varName + " not type " + type);
						result.fail();						
					}
				}
				else if (check.isString())
				{
					result.pass();
				}
				else
				{
					logger.warn("FAILED:: " + checkName + " ATTRIBUTE " + varName + " not string");
					result.fail();
				}
			}
		}
				
		return result;
	}
	
	public void setDataFile(NetcdfDataset ds)
	{
		this.ds = ds;
	}
}
