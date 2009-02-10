/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.health;

import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * Compares two change reports to see if the match.
 * 
 * @author Jeff C. Phillips
 *
 */
public class ChangeReportComparer {
	private static final String ART = "(<ArtChg>.*?</ArtChg>)";
	private static final String ATTR = "(<AttrChg>.*?</AttrChg>)";
	private static final String REL = "(<RelChg>.*?</RelChg>)";
	
	/**
	 * Compares two change report strings by parsing them and comparing each artifact change, attribute change and relation change.
	 * @param changeReport1
	 * @param changeReport2
	 * @return Returns true if the change reports matches else false.
	 */
	public boolean compare(String changeReport1, String changeReport2){
		boolean success = true;
		ArrayList<ArrayList<String>> data1List = parse(changeReport1);
		ArrayList<ArrayList<String>> data2List = parse(changeReport2);
		
		if(data1List.size() != data2List.size() || data1List.get(0).size() != data2List.get(0).size() || data1List.get(1).size() != data2List.get(1).size()
				|| data1List.get(2).size() != data2List.get(2).size()){
			throw new IllegalArgumentException("The change reports must have the same number of items");
		}
		try{
		for(int i = 0; i < data1List.size() ; i++){
			for(int j = 0; j < data1List.get(i).size() ; j++){
				if(! data1List.get(i).get(j).equals(data2List.get(i).get(j))){
					success = false;
					System.err.println(data1List.get(i).get(j));
					System.err.println(data2List.get(i).get(j));
					System.err.println("---------------------------------------------------");
				}
			}
		}}
		catch(Exception ex){
			ex.getLocalizedMessage();
		}
		return success;
	}
	
	/**
	 * 
	 * @param changeReportString
	 * @return Returns Three ArrayLists. 0 index for artifact changes, 1 index for attribute changes and 2 index for relation changes. 
	 */
	public ArrayList<ArrayList<String>> parse(String changeReportString) {
		ArrayList<ArrayList<String>> changeLists = new ArrayList<ArrayList<String>>(3);
		ArrayList<String> artifactChanges = new ArrayList<String>();
		ArrayList<String> attrChanges = new ArrayList<String>();
		ArrayList<String> relChanges = new ArrayList<String>();

		Matcher artChangeMatch = Pattern.compile(ART).matcher(changeReportString);

		while (artChangeMatch.find()) {
			artifactChanges.add(artChangeMatch.group(0));
		}

		Matcher attrChangeMatch = Pattern.compile(ATTR).matcher(changeReportString);

		while (attrChangeMatch.find()) {
			attrChanges.add(attrChangeMatch.group(0));
		}

		Matcher relChangeMatch = Pattern.compile(REL).matcher(changeReportString);

		while (relChangeMatch.find()) {
			relChanges.add(relChangeMatch.group(0));
		}

		Collections.sort(artifactChanges);
		Collections.sort(attrChanges);
		Collections.sort(relChanges);

		changeLists.add(artifactChanges);
		changeLists.add(attrChanges);
		changeLists.add(relChanges);

		return changeLists;
	}
}
