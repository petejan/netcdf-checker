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

import org.w3c.dom.Element;

import ucar.nc2.Attribute;
import ucar.nc2.Variable;
import ucar.units.StandardUnitDB;
import ucar.units.Unit;
import ucar.units.UnitDBException;

public class StdUnits extends Check
{
	StandardUnitDB units = null;
	public StdUnits()
	{
		try
		{
			units = StandardUnitDB.instance();
		}
		catch (UnitDBException e)
		{
			System.out.println("Cannot find units database, won't check for valid units");
		}
		
	}
	public PassFail check(Element eElement)
	{
		String checkName = eElement.getAttribute("name");
		
		List<Variable> vars = ds.getVariables();
		Iterator<Variable> vi = vars.iterator();
		while (vi.hasNext())
		{
			Variable var = (Variable) vi.next();
			if (!var.isCoordinateVariable())
			{
				Attribute ua = var.findAttribute("units");
				if ((ua != null) && (units != null))
				{
					Unit u = units.getByName(ua.getStringValue());
					if (u == null)
					{
						System.out.println("FAIL::Variable " + var.getShortName() + " Unknown unit : " + ua.getStringValue());
						
						result.fail();					
					}
					else
						result.pass();
				}
			}
			
		}
		
		return result;
	}
}
