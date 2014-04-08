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

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import ucar.nc2.Attribute;
import ucar.nc2.Variable;
import ucar.units.NoSuchUnitException;
import ucar.units.PrefixDBException;
import ucar.units.ScaledUnit;
import ucar.units.SpecificationException;
import ucar.units.StandardUnitDB;
import ucar.units.Unit;
import ucar.units.UnitDBException;
import ucar.units.UnitFormat;
import ucar.units.UnitFormatManager;
import ucar.units.UnitParseException;
import ucar.units.UnitSystemException;
import ucar.units.UnknownUnit;

public class StdUnits extends Check
{
	static Logger logger = Logger.getLogger(StdUnits.class.getName());
	
	UnitFormat format = UnitFormatManager.instance();
	
	public StdUnits()
	{
	}
	public PassFail check(Element eElement)
	{
		String checkName = eElement.getAttribute("name");
		
		List<Variable> vars = ds.getVariables();
		Iterator<Variable> vi = vars.iterator();
		while (vi.hasNext())
		{
			Variable var = (Variable) vi.next();
			//if (!var.isCoordinateVariable())
			{
				Attribute ua = var.findAttribute("units");
				if (ua != null)
				{
					String unitName = ua.getStringValue();
					Unit u;
					try
					{
						logger.debug("UNIT :" + unitName + " ");
						u = format.parse(unitName);
						logger.debug(u);
						if ((u == null) && (!(u instanceof UnknownUnit)))
						{
							logger.warn("FAIL::Variable " + var.getShortName() + " Unknown unit : " + ua.getStringValue());
							
							result.fail();					
						}
						else if (u instanceof ScaledUnit)
						{
							ScaledUnit su = (ScaledUnit)u;
							logger.debug("Scaled Unit " + su.getDerivedUnit());

							Unit u3 = format.parse(su.getDerivedUnit().toString());
							
							logger.debug("Derived Scaled Unit " + u3.getClass().getName());

							if (u3 instanceof UnknownUnit)
							{
								logger.warn("FAIL::Variable " + var.getShortName() + " Unknown base unit : " + ua.getStringValue());
								
								result.fail();
							}
						}
						else							
							result.pass();						
					}
					catch (NoSuchUnitException e)
					{
						logger.warn("FAIL::Variable " + var.getShortName() + " NoSuchUnitException : " + ua.getStringValue());
						result.fail();					
					}
					catch (StringIndexOutOfBoundsException e)
					{
						//e.printStackTrace();
						logger.warn("FAIL::Variable " + var.getShortName() + " StringIndexOutOfBoundsException : " + ua.getStringValue());
						result.fail();					
					}
					catch (UnitParseException e)
					{
						logger.warn("FAIL::Variable " + var.getShortName() + " UnitParseException : " + ua.getStringValue());
						result.fail();					
					}
					catch (SpecificationException e)
					{
						logger.warn("FAIL::Variable " + var.getShortName() + "SpecificationException " + ua.getStringValue());
						result.fail();					
					}
					catch (UnitDBException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					catch (PrefixDBException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					catch (UnitSystemException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
		}
		
		return result;
	}
}
