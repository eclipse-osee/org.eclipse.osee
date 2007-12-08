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
import java.util.HashSet;
import java.util.Set;

/**
 * Rips valuable debug data from an import file.
 * 
 * @author Robert A. Fisher
 */
public class BranchStatsRipperSaxHandler extends BranchSaxHandler {
   private Set<String> artifactTypes;
   private Set<String> expectedGuids;

   public BranchStatsRipperSaxHandler() {
      this.artifactTypes = new HashSet<String>();
   }

   /**
    * As artifact guids are encountered they will be removed from this list so that if any expected guids were not in
    * the file then they will still be in the collection.
    * 
    * @param expectedGuids
    */
   public void setExpectedGuids(Set<String> expectedGuids) {
      this.expectedGuids = expectedGuids;
   }

   @Override
   protected void processArtifact(String guid, String type, String hrid, boolean deleted) throws Exception {
      artifactTypes.add(type);
      if (expectedGuids != null) {
         expectedGuids.remove(guid);
      }
   }

   @Override
   protected void processAttribute(String attributeGuid, String attributeType, String stringValue, byte[] contentValue, boolean deleted) throws Exception {
   }

   @Override
   protected void processBranch(String name, Timestamp time, String associatedArtGuid) throws Exception {
   }

   @Override
   protected void processLink(String guid, String type, String aguid, String bguid, int aOrder, int bOrder, String rationale, boolean deleted) throws Exception {
   }

   @Override
   protected void processTransaction(String author, Timestamp time, String comment) throws Exception {
   }

   public Set<String> getArtifactTypes() {
      return artifactTypes;
   }

}
