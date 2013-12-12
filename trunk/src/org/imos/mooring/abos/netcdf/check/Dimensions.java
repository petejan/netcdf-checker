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

import ucar.ma2.DataType;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.Variable;
import ucar.nc2.dataset.NetcdfDataset;

public class Dimensions extends Globals
{
	public int checkDimensionVariable(NetcdfDataset nc, Dimension dim)
	{
		Variable checkVar = nc.findVariable(dim.getShortName());
		if (checkVar == null)				
		{
			System.out.println("FAIL::Dimension has no variable does not exist : " + dim.getShortName());
			
			result.fail();
			return 0;			
		}
		if (!checkVar.isCoordinateVariable())
		{
			System.out.println("FAIL::Dimension variable is not a coordinate variable : " + dim.getShortName());
			
			result.fail();
			return 0;			
		}

		result.pass();
		
		return 1;		
	}

	@Override
	public PassFail check(Element eElement)
	{
		String checkName = eElement.getAttribute("name");
		// iterate over dimensions
		List<Dimension> dims = ds.getDimensions();
		Iterator<Dimension> di = dims.iterator();
		while (di.hasNext())
		{
			Dimension dim = (Dimension) di.next();
			checkDimensionVariable(ds, dim);
		}

		for(int i=0;i<eElement.getElementsByTagName("dimension-required").getLength();i++)
		{
			String varName = eElement.getElementsByTagName("dimension-required").item(i).getTextContent();
			Dimension check = ds.findDimension(varName);
			if (check == null)
			{
				System.out.println("FAILED:: " + checkName + " DIMENSION " + varName);
				result.fail();
			}
			else
			{
				result.pass();
			}
		}
				
		return result;
	}

}
