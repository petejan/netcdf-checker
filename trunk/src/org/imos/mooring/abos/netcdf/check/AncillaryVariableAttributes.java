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

import org.w3c.dom.Element;

import ucar.nc2.Attribute;
import ucar.nc2.Variable;

public class AncillaryVariableAttributes extends Check
{

	@Override
	public PassFail check(Element eElement)
	{
		String checkName = eElement.getAttribute("name");
		List<Variable> vars = ds.getVariables();
		List<Variable> varsAnc = new ArrayList<Variable>();
		
		// need to remove associated variables from list
		Iterator<Variable> vi = vars.iterator();
		while (vi.hasNext())
		{
			Variable var = (Variable) vi.next();
			//System.out.println("FIND VAR : " + var.getShortName());
			
			if (!var.isCoordinateVariable())
			{
				Attribute check = var.findAttribute("ancillary_variables");
				if (check != null)
				{
					String ancName = check.getStringValue();
					Variable ancVar = ds.findVariable(ancName);
					//System.out.println("adding " + ancName);
					varsAnc.add(ancVar);
					
					String varDim = var.getDimensionsString();
					String ancDim = var.getDimensionsString();
					// System.out.println("VAR : " + var.getDimensionsString() + " ANC " + ancVar.getDimensionsString());
					if (!varDim.contentEquals(ancDim))
					{
						System.out.println("FAILED:: " + checkName + " VARIABLE " + var.getShortName() + " Ancillary Variable different dimensions");
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
			//System.out.println("CHECH VAR : " + var.getShortName());
			
			if (!var.isCoordinateVariable())
			{
				for(int i=0;i<eElement.getElementsByTagName("attribute").getLength();i++)
				{
					String varName = eElement.getElementsByTagName("attribute").item(i).getTextContent();
					Attribute check = var.findAttribute(varName.trim());
					if (check == null)
					{
						System.out.println("FAILED:: " + checkName + " VARIABLE " + var.getShortName() + " missing ATTRIBUTE " + varName);
						result.fail();
					}
					else
					{
						result.pass();
					}
				}
				// TODO: check associated variables exists
			}
		}
				
		return result;
	}

}
