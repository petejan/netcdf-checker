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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ucar.ma2.Array;
import ucar.nc2.Attribute;
import ucar.nc2.Variable;

public class AncillaryVariableAttributes extends Check
{
	static Logger logger = Logger.getLogger(AncillaryVariableAttributes.class.getName());
	
	@Override
	public PassFail check(Element eElement)
	{
		boolean option = false;
		
		String checkName = eElement.getAttribute("name");
		
		List<Variable> vars = ds.getVariables();
		List<Variable> varsAnc = new ArrayList<Variable>();
		
		// need to remove associated variables from list
		Iterator<Variable> vi = vars.iterator();
		while (vi.hasNext())
		{
			Variable var = (Variable) vi.next();
			//logger.debug("FIND VAR : " + var.getShortName());
			
			if (!var.isCoordinateVariable())
			{
				List <Attribute> atts = var.getAttributes();
//				for(Attribute a : atts)
//				{
//					logger.debug(" attrib " + a.getShortName());					
//				}
				Attribute check = var.findAttribute("ancillary_variables");
				if (check != null)
				{
					String ancName = check.getStringValue();
					Variable ancVar = ds.findVariable(ancName);
					//logger.debug("adding " + ancName);
					varsAnc.add(ancVar);
					
					String varDim = var.getDimensionsString();
					String ancDim = var.getDimensionsString();
					//logger.debug("VAR : " + var.getDimensionsString() + " ANC " + ancVar.getDimensionsString());
					if (!varDim.contentEquals(ancDim))
					{
						logger.warn("FAILED:: " + checkName + " VARIABLE " + var.getShortName() + " Ancillary Variable different dimensions");
						result.fail();
					}
					else
						result.pass();
						
				}			
			}
		}
				
		// iterate through non coordinate variables
		vi = varsAnc.iterator();
		while (vi.hasNext())
		{
			Variable var = (Variable) vi.next();
			//logger.debug("CHECH VAR : " + var.getShortName());
			
			if (!var.isCoordinateVariable())
			{
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
							logger.warn("FAILED:: " + checkName + " VARIABLE " + var.getShortName() + " missing ATTRIBUTE " + varName);
							result.fail();
						}
					}
					else
					{
						//logger.debug("Checking " + var.getShortName() + ":" + check.getShortName());
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
						else
						{
							result.pass();
						}
					}
				}
				// TODO: check associated variables exists
				Attribute ln = var.findAttribute("long_name");
				if (ln.getStringValue().startsWith(("quality flag")))
				{
					Set<Byte> list = new HashSet<Byte>();
					try
					{
						Array a = var.read();
						while(a.hasNext())
						{
							list.add(a.nextByte());
						}
						logger.info( var.getShortName() + " : flag values " + list);
					}
					catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else
				{
					if (!option)
					{
						logger.warn("FAILED:: " + checkName + " VARIABLE " + var.getShortName() + " long_name does not start with quality flag");
						result.fail();
					}
				}
			}
		}
				
		return result;
	}

}
