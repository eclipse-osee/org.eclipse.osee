/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.disposition.rest.internal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.disposition.model.DispoSet;
import org.eclipse.osee.disposition.model.Note;
import org.eclipse.osee.disposition.model.OperationReport;
import org.eclipse.osee.disposition.rest.DispoConstants;
import org.eclipse.osee.disposition.rest.util.DispoUtil;
import org.eclipse.osee.framework.jdk.core.type.BaseIdentity;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
   public String getName() {
      return artifact.getName();
   }

   @Override
   public String getImportPath() {
      return artifact.getSoleAttributeAsString(DispoConstants.ImportPath);
   }

   @Override
   public List<Note> getNotesList() {
      List<Note> toReturn = new ArrayList<Note>();
      String notesJson = artifact.getSoleAttributeAsString(DispoConstants.DispoNotesJson, "[]");
      try {
         JSONArray jArray = new JSONArray(notesJson);
         for (int i = 0; i < jArray.length(); i++) {
            toReturn.add(DispoUtil.jsonObjToNote(jArray.getJSONObject(i)));
         }
         return toReturn;
      } catch (JSONException ex) {
         throw new OseeCoreException("Could not parse Notes Json", ex);
      }
   }

   @Override
   public OperationReport getOperationSummary() {
      String operationSummaryJson = artifact.getSoleAttributeAsString(DispoConstants.OperationSummary, "{}");
      try {
         JSONObject jsonObject = new JSONObject(operationSummaryJson);
         return DispoUtil.jsonObjToOperationSummary(jsonObject);
      } catch (JSONException ex) {
         throw new OseeCoreException("Could not parse Operation Sumary Json", ex);
      }
   }

   @Override
   public String toString() {
      return getName();
   }

   @Override
   public String getImportState() {
      return artifact.getSoleAttributeAsString(DispoConstants.ImportState, "None");
   }

   @Override
   public String getDispoType() {
      return artifact.getSoleAttributeAsString(DispoConstants.DispoType, "");
   }

   @Override
   public String getCiSet() {
      return artifact.getSoleAttributeAsString(DispoConstants.DispoCiSet, "");
   }

   @Override
   public String getRerunList() {
      return artifact.getSoleAttributeAsString(DispoConstants.DispoRerunList, "");
   }

   @Override
   public String getTime() {
      SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
      return sdf.format(new Date(System.currentTimeMillis()));
   }

}
