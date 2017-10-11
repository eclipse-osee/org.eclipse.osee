/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.workflow.note;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public class ArtifactNote implements INoteStorageProvider {
   private final IAtsWorkItem workItem;
   private final AtsApi atsApi;

   public ArtifactNote(IAtsWorkItem workItem, AtsApi atsApi) {
      this.workItem = workItem;
      this.atsApi = atsApi;
   }

   @Override
   public String getNoteXml() {
      return atsApi.getAttributeResolver().getSoleAttributeValue(workItem, AtsAttributeTypes.StateNotes, "");
   }

   @Override
   public Result saveNoteXml(String xml) {
      try {
         atsApi.getAttributeResolver().setSoleAttributeValue(workItem, AtsAttributeTypes.StateNotes, xml);
         return Result.TrueResult;
      } catch (OseeCoreException ex) {
         atsApi.getLogger().error(ex, "Error saving note xml");
         return new Result(false, "saveLogXml exception " + ex.getLocalizedMessage());
      }
   }

   @Override
   public String getNoteTitle() {
      return "History for \"" + atsApi.getStoreService().getArtifactType(
         workItem.getStoreObject()).getName() + "\" - " + getNoteId() + " - titled \"" + workItem.getName() + "\"";
   }

   @Override
   public String getNoteId() {
      return workItem.getAtsId();
   }

   @Override
   public boolean isNoteable() {
      return atsApi.getStoreService().isAttributeTypeValid(workItem, AtsAttributeTypes.StateNotes);
   }

}
