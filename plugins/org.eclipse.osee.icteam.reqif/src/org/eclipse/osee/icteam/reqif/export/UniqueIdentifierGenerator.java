/*********************************************************************
 * Copyright (c) 2021 Robert Bosch Engineering and Business Solutions Ltd India
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Robert Bosch Engineering and Business Solutions Ltd India - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.icteam.reqif.export;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;

/**
 * This is Class which is used to generate the Identifier for Reqif tags
 * 
 * @author nas1kor
 */
public class UniqueIdentifierGenerator {

  /**
   * Creates a unique identifier for the given unique name using ConnectionHandler of Data base
   * 
   * @param uniqueKey
   * @return
   */
  private static long generateUniqueIdentifier(final String uniqueKey) {
    
      long sequence = ConnectionHandler.getNextSequence(uniqueKey,true);
      return sequence;
  }

  /**
   * Creates a Unique name
   * 
   * @param artifactName : name
   * @return : unique name
   */
  public static String createUniqueNameWithID(final String artifactName) {
    String temp = "ID_" + artifactName;
    long generateUniqueIdentifier = generateUniqueIdentifier(artifactName);
    temp = temp + "_" + generateUniqueIdentifier;
    return temp;
  }

  /**
   * @param artifactName
   * @return
   */
  public static String createUniqueName(final String artifactName) {
    long generateUniqueIdentifier = generateUniqueIdentifier(artifactName);
    String temp = artifactName + "_" + generateUniqueIdentifier;
    return temp;

  }

  /**
   * This is used to reset the Identifier
   */
  public static void resetNumber() {

    String UPDATE_SEQUENCE_COMMAND = "update osee_sequence SET last_sequence = ? WHERE sequence_name = ? ";

    try {
      List<Object[]> data = new ArrayList<Object[]>();
      data.add(new Object[] { 0, "DataTypeDefinitionString" });
      data.add(new Object[] { 0, "Header" });
      data.add(new Object[] { 0, "AttributeDefinitionString" });
      data.add(new Object[] { 0, "SpecificationType" });
      data.add(new Object[] { 0, "SpecObjectType" });
      data.add(new Object[] { 0, "SpecObject" });
      data.add(new Object[] { 0, "SpecHierarchy" });
      data.add(new Object[] { 0, "Specification" });
      data.add(new Object[] { 0, "AttributeDefiniitonXHTML" });
      data.add(new Object[] { 0, "DataTypeDefinitionXhtml" });
      data.add(new Object[] { 0, "SpecRelation" });
      data.add(new Object[] { 0, "SpecRelationType" });
      data.add(new Object[] { 0, "Requirement" });
      data.add(new Object[] { 0, "Requirement_Folder" });
      ConnectionHandler.runBatchUpdate(UPDATE_SEQUENCE_COMMAND, data);
    }
    catch (OseeCoreException e) {
      e.printStackTrace();
    }
  }
}
