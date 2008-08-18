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
   protected void processArtifact(String guid, String type, String hrid, String modType, int txCurrent) throws Exception {
      System.out.print("\t\t");
      System.out.println(String.format("Artifact %s %s %s modType=[%s] txCurrent=[%s]", type, hrid, guid, modType,
            txCurrent));
   }

   @Override
   protected void processAttribute(String artifactHrid, String attributeGuid, String attributeType, String stringValue, String uriValue, String modType, int txCurrent) throws Exception {
      System.out.print("\t\t\t");
      System.out.println(String.format("Attribute %s %s %s uri=[%s] modType=[%s] txCurrent=[%s]", attributeType,
            attributeGuid, stringValue, uriValue, modType, txCurrent));
   }

   @Override
   protected void processBranch(String name, Timestamp time, String associatedArtGuid, String branchType) throws Exception {
      System.out.println(String.format("Branch (%s) %s - artGuid=[%s] branchType=[%s]", time, name, associatedArtGuid,
            branchType));
   }

   @Override
   protected void processLink(String guid, String type, String aguid, String bguid, String aOrder, String bOrder, String rationale, String modType, int txCurrent) throws Exception {
      System.out.print("\t\t");
      System.out.println(String.format("Link %s %s %s(%s)<-->%s(%s) %s modType=[%s] txCurrent=[%s]", type, guid, aguid,
            aOrder, bguid, bOrder, rationale, modType, txCurrent));
   }

   @Override
   protected void processTransaction(String author, Timestamp time, String comment, String commitArtGuid, Integer txType) throws Exception {
      System.out.print("\t");
      System.out.println(String.format("Transaction (%s), %s, %s, %s", author, comment, commitArtGuid, txType));
   }
}
