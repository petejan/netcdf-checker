/*
 * Note: These examples use the old version 1 Java netCDF interface,
 * which we do not recommend for new development. Instead, please use
 * NetCDF Java Library (Version 2), which is more efficient, simpler,
 * and provides better support for remote access using HTTP or
 * DODS. Similar examples are available in the NetCDF Java (version 2)
 * User's Manual.
 */

package ucar.demo;

import java.util.ArrayList;
import java.util.Iterator;

import ucar.nc2.dataset.NetcdfDataset;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

/**
 * Read all the data from an existing netCDF file of unknown structure. This
 * only outputs variable names as they are being read, without doing anything
 * with the data, so it may useful for timing and tuning.
 * 
 * @author: Russ Rew
 * @version: $Id: ReadAnyNetcdf.java,v 1.9 1998/07/17 15:24:32 russ Exp $
 */
public class ReadAnyNetcdf
{

	static String fileName;

	/**
	 * Reads an existing netCDF file with a specified file name.
	 * 
	 * @param args
	 *            name of netCDF file to be read.
	 */
	public static void main(String[] args)
	{

		if (args.length == 1)
			fileName = args[0];
		else
		{
			System.err.println("no netCDF file name specified, exiting ...");
			System.exit(-1);
		}

		try
		{
			NetcdfFile nc = NetcdfDataset.openDataset(fileName); // open it readonly
			java.util.List<Variable> vars = nc.getVariables();
			Iterator<Variable> vi = vars.iterator();
			while (vi.hasNext())
			{
				Variable var = (Variable) vi.next();
				System.out.println(var.getFullName() + " ...");
			}
		}
		catch (java.io.IOException e)
		{
			e.printStackTrace();
		}

	}
}
