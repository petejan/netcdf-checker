package org.imos.mooring.abos.netcdf;

import ucar.nc2.units.SimpleUnit;
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

public class UnitCheck
{
	public static void main(String[] args)
	{
		SimpleUnit u = SimpleUnit.factory(args[0]);
	
		System.out.println("Unit " + u.getCanonicalString() + " " + u.getClass().getName());
		
		UnitFormat format = UnitFormatManager.instance();
		try
		{
			Unit u2 = format.parse(args[0]);
			
			System.out.println("Unit " + u2.getCanonicalString() + " " + u2.getClass().getName() + " " + u2.getUnitName());
			
			if (u2 instanceof ScaledUnit)
			{
				ScaledUnit su = (ScaledUnit)u2;
				System.out.println("Scaled Unit " + su.getDerivedUnit());

				Unit u3 = format.parse(su.getDerivedUnit().toString());
				
				System.out.println("Scaled Unit " + u3.getClass().getName());
			}
			
		}
		catch (NoSuchUnitException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (UnitParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (SpecificationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
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
