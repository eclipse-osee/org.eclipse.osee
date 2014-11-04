/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.vcast.model;

/**
 * @author Shawn F. Cook
 */
public class VCastInstrumentedFile {

   private final int id;
   private final int sourceFileId;
   private final int projectId;
   private final int unitIndex;
   private final VCastCoverageType coverageType;
   private final String LISFile;
   private final int checksum;

   public VCastInstrumentedFile(int id, int sourceFileId, int projectId, int unitIndex, VCastCoverageType coverageType, String LISFile, int checksum) {
      this.id = id;
      this.sourceFileId = sourceFileId;
      this.projectId = projectId;
      this.unitIndex = unitIndex;
      this.coverageType = coverageType;
      this.LISFile = LISFile;
      this.checksum = checksum;
   }

   public int getId() {
      return id;
   }

   public int getSourceFileId() {
      return sourceFileId;
   }

   public int getProjectId() {
      return projectId;
   }

   public int getUnitIndex() {
      return unitIndex;
   }

   public VCastCoverageType getCoverageType() {
      return coverageType;
   }

   public String getLISFile() {
      return LISFile;
   }

   public int getChecksum() {
      return checksum;
   }

}
