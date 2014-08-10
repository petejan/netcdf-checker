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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ucar.nc2.Attribute;
import ucar.nc2.Variable;

public class VariableAttribute extends Check
{
	static Logger logger = Logger.getLogger(VariableAttribute.class.getName());
	
	@Override
	public PassFail check(Element eElement)
	{
		boolean option = false;
		
		String checkName = eElement.getAttribute("name");
		List<Variable> vars = ds.getVariables();
		List<Variable> varsNoAnc = new ArrayList<Variable>(vars);
		
		// need to remove associated variables from list
		Iterator<Variable> vi = vars.iterator();
		while (vi.hasNext())
		{
			Variable var = (Variable) vi.next();
			//logger.debug("FIND VAR : " + var.getShortName());
			
			if (!var.isCoordinateVariable())
			{
				Attribute check = var.findAttribute("ancillary_variables");
				if (check != null)
				{
					String ancName = check.getStringValue();
					Variable ancVar = ds.findVariable(ancName);
					logger.debug("Deleteing " + ancName + " as its an auxilllary variable");
					varsNoAnc.remove(ancVar);				
				}			
			}
		}
				
		// iterate through non coordinate variables
		vi = varsNoAnc.iterator();
		while (vi.hasNext())
		{
			Variable var = (Variable) vi.next();
			//logger.debug("CHECH VAR : " + var.getShortName());
			
			if (!var.isCoordinateVariable())
			{
				// TODO: check associated variables exists
				NodeList aN = eElement.getElementsByTagName("attribute");
				for(int j=0;j<aN.getLength();j++)
				{
					Node nAttribute = aN.item(j);
					NamedNodeMap nNM = nAttribute.getAttributes();
					
					Pattern p = null;
					String type = null;
					
					for(int k=0;k<nNM.getLength();k++)
					{
						//System.out.println("nNM " + nNM.item(k).getNodeName());
						if (nNM.item(k).getNodeName().equals("regex"))
						{
							p = Pattern.compile(nNM.item(k).getNodeValue());
						}
						if (nNM.item(k).getNodeName().equals("type"))
						{
							type = nNM.item(k).getNodeValue().trim();
						}
						if (nNM.item(k).getNodeName().equals("vartype"))
						{
							type = var.getDataType().toString();
						}
						if (nNM.item(k).getNodeName().equals("optional"))
						{
							String sOption = nNM.item(k).getNodeValue();
							//logger.debug(checkName + " " +  var.getShortName() + " " + " option " + sOption);
							if (Integer.parseInt(sOption) > 0)
							{					
								option = true;
							}
						}
						else
						{
							option = false;
						}
					}
					String varName = nAttribute.getTextContent();

					Attribute check = var.findAttribute(varName.trim());
					if (check == null)
					{
						if (!option)
						{
							logger.warn("FAILED:: " + checkName + " VARIABLE " + var.getShortName() + " ATTRIBUTE " + varName + " does not exist");
							result.fail();
						}
					}
					else
					{
						list.put(var.getShortName() + ":" + check.getShortName(), true);

						if ((p != null) && (check.isString())) // can only really regex a string type
						{
							String val = check.getStringValue();
							if (p.matcher(val).matches())
							{
								result.pass();
							}
							else
							{
								if (!option)
								{	
									logger.warn("FAILED:: " + checkName + " VARIABLE " + var.getShortName() + " ATTRIBUTE " + varName + " failed regex " + p + " is " + check.getStringValue());
									result.fail();
								}
							}
						}
						else if (type != null)
						{
							String varType = check.getDataType().toString().trim();
							if (varType.matches(type))
							{
								result.pass();
							}
							else
							{
								if (!option)
								{	
									logger.warn("FAILED:: " + checkName + " VARIABLE " + var.getShortName() + " ATTRIBUTE " + varName + " not type " + type + " is " + varType);
									result.fail();
								}
							}
						}
						else if (check.isString())
						{
							result.pass();
						}
						else
						{
							if (!option)
							{							
								logger.warn("FAILED:: " + checkName + " VARIABLE " + var.getShortName() + " ATTRIBUTE " + varName + " not string is " + var.getDataType());
								result.fail();
							}
						}
					}
				}
				
			}
		}
				
		return result;
	}

}
