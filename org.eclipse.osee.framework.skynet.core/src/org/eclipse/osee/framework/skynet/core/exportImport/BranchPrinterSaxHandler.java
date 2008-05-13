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
package org.eclipse.osee.framework.skynet.core.exportImport;

import java.sql.Timestamp;

/**
 * @author Robert A. Fisher
 */
public class BranchPrinterSaxHandler extends BranchSaxHandler {

   @Override
   protected void processArtifact(String guid, String type, String hrid, boolean deleted) throws Exception {
      System.out.print("\t\t");
      if (deleted)
         System.out.print("-");
      else
         System.out.print("+");
      System.out.println("Artifact " + type + " " + hrid + " " + guid);
   }

   @Override
   protected void processAttribute(String artifactHrid, String attributeGuid, String attributeType, String stringValue, String uriValue, boolean deleted) throws Exception {
      System.out.print("\t\t\t");
      if (deleted)
         System.out.print("-");
      else
         System.out.print("+");
      System.out.println("Attribute " + attributeType + " " + attributeGuid + " " + stringValue);
   }

   @Override
   protected void processBranch(String name, Timestamp time, String associatedArtGuid) throws Exception {
      System.out.println("Branch (" + time + ") " + name);
   }

   @Override
   protected void processLink(String guid, String type, String aguid, String bguid, int aOrder, int bOrder, String rationale, boolean deleted) throws Exception {
      System.out.print("\t\t");
      if (deleted)
         System.out.print("-");
      else
         System.out.print("+");
      System.out.println("Link " + type + " " + guid + " " + aguid + "(" + aOrder + ")<-->" + bguid + "(" + bOrder + ") " + rationale);
   }

   @Override
   protected void processTransaction(String author, Timestamp time, String comment) throws Exception {
      System.out.print("\t");
      System.out.println("Transaction (" + time + ") " + author + " " + comment);
   }
}
