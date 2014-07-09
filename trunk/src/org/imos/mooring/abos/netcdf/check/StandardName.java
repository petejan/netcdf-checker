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

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ucar.nc2.Attribute;
import ucar.nc2.Variable;
import ucar.nc2.dataset.NetcdfDataset;

public class StandardName extends Check
{
	static Logger logger = Logger.getLogger(StandardName.class.getName());
	
	HashMap<String, String> cfNames = new HashMap<String, String>();
	
	public StandardName()
	{
	}
	
	public void setDataFile(NetcdfDataset ds)
	{
		this.ds = ds;
		
		// Read the standard names table, download the XML file from
		// http://cf-pcmdi.llnl.gov/documents/cf-standard-names

		try
		{
			File fXmlFile = new File(auxFilePath + File.separator + "cf-standard-name-table.xml");
			logger.debug("xml file " + fXmlFile.getAbsolutePath());
			
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			// optional, but recommended
			doc.getDocumentElement().normalize();

			logger.info("cf-standard-name-table.xml:: Root element :" + doc.getDocumentElement().getNodeName());

			NodeList nList = doc.getElementsByTagName("entry");

			for (int temp = 0; temp < nList.getLength(); temp++)
			{
				Node nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE)
				{

					Element eElement = (Element) nNode;

					cfNames.put(eElement.getAttribute("id"), eElement.getElementsByTagName("canonical_units").item(0).getTextContent());
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

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
				String name = sn.getStringValue();
				// check for standard_name modifiers first
				int i = name.lastIndexOf("status_flag");
				if (i < 0)
				{
					i = name.lastIndexOf("standard_error");
				}
				if (i < 0)
				{
					i = name.lastIndexOf("number_of_observations");
				}
				if (i < 0)
				{
					i = name.lastIndexOf("detection_minimum");
				}
				if (i > 0)
				{
					name = name.substring(0, i - 1).trim();
				}
				
				// Check standard_name for a unit in the cf conventions
				String cfUnit = cfNames.get(name);
				logger.debug("VARIABLE STD-NAME: " + var.getShortName() + " std_name " + name +" standard name CF Unit " + cfUnit);
				if (cfUnit == null)
				{
					logger.warn("FAIL::Variable " + var.getShortName() + " not standard name : " + name);
	
					result.fail();					
				}
				else
					result.pass();
			}
		}		
				
		return result;
	}

}
