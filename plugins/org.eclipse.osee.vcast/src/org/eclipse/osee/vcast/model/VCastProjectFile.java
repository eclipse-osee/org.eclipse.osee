/*********************************************************************
 * Copyright (c) 2012 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.vcast.model;

/**
 * @author Shawn F. Cook
 */
public class VCastProjectFile {

   private final int projectId;
   private final int sourceFileId;
   private final int instrumentedFileId;
   private final int timestamp;
   private final String buildMd5Sum;

   public VCastProjectFile(int projectId, int sourceFileId, int instrumentedFileId, int timestamp, String buildMd5Sum) {
      super();
      this.projectId = projectId;
      this.sourceFileId = sourceFileId;
      this.instrumentedFileId = instrumentedFileId;
      this.timestamp = timestamp;
      this.buildMd5Sum = buildMd5Sum;
   }

   public int getProjectId() {
      return projectId;
   }

   public int getSourceFileId() {
      return sourceFileId;
   }

   public int getInstrumentedFileId() {
      return instrumentedFileId;
   }

   public int getTimestamp() {
      return timestamp;
   }

   public String getBuildMd5Sum() {
      return buildMd5Sum;
   }

}
