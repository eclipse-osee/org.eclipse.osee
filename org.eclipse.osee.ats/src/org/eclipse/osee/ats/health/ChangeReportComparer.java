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

import org.eclipse.osee.ats.health.change.DataChangeReportComparer;
import org.eclipse.osee.ats.health.change.ValidateChangeReportParser;

/**
 * 
 * Compares two change reports to see if the match.
 * 
 * @author Jeff C. Phillips
 *
 */
public class ChangeReportComparer {
	
	/**
	 * Compares two change report strings by parsing them and comparing each artifact change, attribute change and relation change.
	 * @param currentData
	 * @param storedData
	 * @return Returns true if the change reports matches else false.
	 */
	public boolean compare(String currentData, String storedData){
		boolean success = true;
		ValidateChangeReportParser parser = new ValidateChangeReportParser();
		ArrayList<ArrayList<DataChangeReportComparer>> currentList = parser.parse(currentData);
		ArrayList<ArrayList<DataChangeReportComparer>> storedList = parser.parse(storedData);
		
		if(currentList.size() != storedList.size() || currentList.get(0).size() != storedList.get(0).size() || currentList.get(1).size() != storedList.get(1).size()
				|| currentList.get(2).size() != storedList.get(2).size()){
			throw new IllegalArgumentException("The change reports must have the same number of items");
		}
		try{
		for(int i = 0; i < currentList.size() ; i++){
			for(int j = 0; j < currentList.get(i).size() ; j++){
				if(! currentList.get(i).get(j).getContent().equals(storedList.get(i).get(j).getContent())){
					success = false;
					System.err.println(currentList.get(i).get(j).getContent());
					System.err.println(storedList.get(i).get(j).getContent());
					System.err.println("---------------------------------------------------");
				}
			}
		}}
		catch(Exception ex){
			ex.getLocalizedMessage();
		}
		return success;
	}
}
