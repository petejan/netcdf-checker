package org.imos.mooring.abos.netcdf;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;
import org.imos.mooring.abos.netcdf.check.Check;
import org.imos.mooring.abos.netcdf.check.PassFail;

import org.jfree.util.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ucar.nc2.Attribute;
import ucar.nc2.Variable;
import ucar.nc2.dataset.NetcdfDataset;

// Copyright (c) 2013, Peter Jansen
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without modification, are permitted provided 
// that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer 
//    in the documentation and/or other materials provided with the distribution.
//
// 3. Neither the name of the IMOS nor the names of its contributors may be used to endorse or promote products derived 
//    from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, 
// INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
// IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
// OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
// OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, 
// OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.



public class XMLfileChecker
{
	String fileName;
	HashMap <String, Boolean> checkedList = new HashMap<String, Boolean>();
	
	static Logger logger = Logger.getLogger(XMLfileChecker.class);
	
	public void check(String f)
	{
		fileName = f;
		try
		{
			logger.info("Checking File : " + fileName);
			
			NetcdfDataset nc = NetcdfDataset.acquireDataset(fileName, null); 
			
			// create a full list of variable attribues
			for(Variable v : nc.getVariables())
			{
				for(Attribute a : v.getAttributes())
				{
					if (!a.getShortName().startsWith("_"))
						checkedList.put(v.getShortName() + ":" + a.getShortName() , false);
				}				
			}
			for(Attribute a : nc.getGlobalAttributes())
			{
				if (!a.getShortName().startsWith("_"))
					checkedList.put("(global):" + a.getShortName(), false);							
			}
			SortedSet<String> keys = new TreeSet<String>(checkedList.keySet());
			
//			for(String va : keys)
//			{
//				logger.debug("All atts " + va);
//			}

			// find which conventions are being used
			Attribute attConventions = nc.findGlobalAttributeIgnoreCase("Conventions");
			String conventions = attConventions.getStringValue();
			logger.info("File Conventions : " + conventions);
			String split = " ";
			if (conventions.indexOf(',') >= 0)
				split = ",";
			
			String[] convention = conventions.split(split);

			// read the XML file for the checked to do
			File fXmlFile = new File(checkRules.get(0));
			File testPath = fXmlFile.getParentFile();
			logger.debug("testPath " + testPath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			// optional, but recommended
			doc.getDocumentElement().normalize();

			logger.info("Root element :" + doc.getDocumentElement().getNodeName());

			NodeList nList = doc.getElementsByTagName("rule");

			PassFail result = new PassFail();
			for (int temp = 0; temp < nList.getLength(); temp++)
			{
				Node nNode = nList.item(temp);

				//logger.info("Current Element :" + nNode.getNodeName());

				if (nNode.getNodeType() == Node.ELEMENT_NODE)
				{
					Element eElement = (Element) nNode;

					String checkName = eElement.getAttribute("name");
					String checkClassName = eElement.getElementsByTagName("class").item(0).getTextContent();
					
					logger.debug("class : " + checkClassName + " : " + checkName);

					NodeList checkConvention = eElement.getElementsByTagName("convention");
					boolean bConvention = true;
					if (checkConvention.getLength() > 0)
					{
						Pattern p = Pattern.compile(checkConvention.item(0).getTextContent());

						bConvention = false;
						//logger.info("convention : " + checkConvention.getLength());
						for(int i=0;i<convention.length;i++)
						{
							//logger.info(i + " " + convention[i] + " " + checkConvention.item(0).getTextContent() );
							if (p.matcher(convention[i]).matches())
							{
								bConvention = true;
								//logger.info("Matched Convention");
							}
						}
					}
										
					if (bConvention)
					{
						logger.info("class : " + checkClassName + " : " + checkName);

						Check agent = (Check) Class.forName(checkClassName).newInstance();
						agent.setCheckedList(checkedList);
						agent.setAuxFilePath(testPath);
						
						agent.setDataFile(nc);
						PassFail test = agent.check(eElement);
						
						logger.info("Test Result : " + checkName + " " + test + "\n");
						result.add(test);
					}
				}				
			}
			// print attributes checked
			for(String va : keys)
			{
				if (checkedList.get(va))
					logger.debug("Checked " + va);
			}
			for(String va : keys)
			{
				if (!checkedList.get(va))
					logger.info("Unchecked " + va);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}

	List<String> checkRules = new ArrayList<String>();
	
	public void addXMLfile(String s)
	{
		checkRules.add(s);
	}
	
	public static void main(String[] args) 
	{
		XMLfileChecker ck = new XMLfileChecker();
		PropertyConfigurator.configure("log4j.properties");
		
		String file = null;
		int i = 0;
		while (i < args.length)
		{
			//logger.info("ARG : " + i + " value " + args[i]);
			if (args[i].startsWith("-x"))
			{
				ck.addXMLfile(args[++i]);
			}
			else
			{
				file = args[i];
			}
			i++;
		}
		
		ck.check(file);
		
	}

}
