/*
 * Note: These examples use the old version 1 Java netCDF interface,
 * which we do not recommend for new development. Instead, please use
 * NetCDF Java Library (Version 2), which is more efficient, simpler,
 * and provides better support for remote access using HTTP or
 * DODS. Similar examples are available in the NetCDF Java (version 2)
 * User's Manual.
 */

package ucar.demo;

import java.io.File;

import ucar.nc2.NetcdfFile;
import ucar.nc2.dataset.NetcdfDataset;

/**
 * Simple example to print structure of an existing netCDF file of unknown
 * structure.
 * 
 * @author: Russ Rew
 * @version: $Id: ShowNetcdfSchema.java,v 1.9 1998/07/17 15:24:32 russ Exp $
 */
public class ShowNetcdfSchema
{

	static String fileName;

	/**
	 * Prints schema (structure) of an existing netCDF file with a specified
	 * file name.
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
			System.out.println(nc); // output schema in CDL form (like ncdump)
		}
		catch (java.io.IOException e)
		{
			e.printStackTrace();
		}

	}
}
