package org.imos.mooring.abos.netcdf.check;

import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Element;

import ucar.nc2.Attribute;
import ucar.nc2.Variable;

public class Name extends Check
{

	@Override
	public PassFail check(Element eElement)
	{
		String checkName = eElement.getAttribute("name");
		
		List<Variable> vars = ds.getVariables();
		Iterator<Variable> vi = vars.iterator();
		while (vi.hasNext())
		{
			Variable var = (Variable) vi.next();

			Attribute sn = var.findAttribute("standard_name");
			if (sn != null)
			{
				result.pass();
			}
			else
			{
				sn = var.findAttribute("long_name");
				if (sn != null)
				{
					result.pass();
				}
				else
				{
					System.out.println("FAIL::Variable " + var.getShortName() + " no standard_name or long_name ");
					result.fail();
				}
			}
			
		}		
				
		return result;
	}

}
