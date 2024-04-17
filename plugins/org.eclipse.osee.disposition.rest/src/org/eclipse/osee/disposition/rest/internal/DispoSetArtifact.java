/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.disposition.rest.internal;

import java.util.Date;
import java.util.List;
import org.eclipse.osee.disposition.model.DispoSet;
import org.eclipse.osee.disposition.model.Note;
import org.eclipse.osee.disposition.model.OperationReport;
import org.eclipse.osee.disposition.rest.util.DispoUtil;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DispoOseeTypes;
import org.eclipse.osee.framework.jdk.core.type.BaseIdentity;

/**
 * @author Angel Avila
 */
public class DispoSetArtifact extends BaseIdentity<String> implements DispoSet {

   private final ArtifactReadable artifact;

   public DispoSetArtifact(ArtifactReadable artifact) {
      super(artifact.getIdString());
      this.artifact = artifact;
   }

   @Override
   public String getIdString() {
      return artifact.getIdString();
   }

   @Override
   public String getName() {
      return artifact.getName();
   }

   @Override
   public String getImportPath() {
      return artifact.getSoleAttributeAsString(CoreAttributeTypes.CoverageImportPath);
   }

   @Override
   public String getServerImportPath() {
      if (artifact.getAttributeCount(CoreAttributeTypes.ServerImportPath) == 1) {
         return artifact.getSoleAttributeAsString(CoreAttributeTypes.ServerImportPath);
      }
      return "";
   }

   @Override
   public boolean serverImportPathExists() {
      if (artifact.getAttributeCount(CoreAttributeTypes.ServerImportPath) == 1) {
         return true;
      }
      return false;
   }

   @Override
   public List<Note> getNotesList() {
      String notesJson = artifact.getSoleAttributeAsString(CoreAttributeTypes.CoverageNotesJson, "[]");
      return DispoUtil.jsonStringToList(notesJson, Note.class);
   }

   @Override
   public OperationReport getOperationSummary() {
      String operationSummaryJson =
         artifact.getSoleAttributeAsString(CoreAttributeTypes.CoverageOperationSummary, "{}");
      return DispoUtil.jsonObjToOperationSummary(operationSummaryJson);
   }

   @Override
   public String toString() {
      return getName();
   }

   @Override
   public String getImportState() {
      return artifact.getSoleAttributeAsString(CoreAttributeTypes.CoverageImportState, "None");
   }

   @Override
   public String getDispoType() {
      return artifact.getSoleAttributeAsString(DispoOseeTypes.CoverageConfig, "");
   }

   @Override
   public String getCiSet() {
      return artifact.getSoleAttributeAsString(DispoOseeTypes.DispoCiSet, "");
   }

   @Override
   public String getRerunList() {
      return artifact.getSoleAttributeAsString(CoreAttributeTypes.CoverageRerunList, "");
   }

   @Override
   public Date getTime() {
      return artifact.getSoleAttributeValue(CoreAttributeTypes.CoverageImportDate, null);
   }
}
