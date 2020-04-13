/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.task;

import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.task.AbstractAtsTaskService;
import org.eclipse.osee.ats.api.task.IAtsTaskProvider;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.core.task.internal.AtsTaskProviderCollector;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractAtsTaskServiceCore extends AbstractAtsTaskService {

   protected String DE_REFERRENCED_NOTE = "No Matching Artifact; Task can be deleted.";

   public AbstractAtsTaskServiceCore(AtsApi atsApi) {
      super(atsApi);
   }

   @Override
   public Collection<IAtsTaskProvider> getTaskProviders() {
      return AtsTaskProviderCollector.getTaskProviders();
   }

   @Override
   public boolean isAutoGen(IAtsTask task) {
      for (IAtsTaskProvider provider : getTaskProviders()) {
         if (provider.isAutoGen(task)) {
            return true;
         }
      }
      return false;
   }

   @Override
   public boolean isAutoGenDeReferenced(IAtsTask task) {
      String note = atsApi.getAttributeResolver().getSoleAttributeValue(task, AtsAttributeTypes.WorkflowNotes, "");
      boolean deReferenced = note.contains(DE_REFERRENCED_NOTE);
      return deReferenced;
   }

   @Override
   public void addDeReferencedNote(IAtsTask task, IAtsChangeSet changes) {
      // Add note to user that task is de-referenced
      String note = atsApi.getAttributeResolver().getSoleAttributeValue(task, AtsAttributeTypes.WorkflowNotes, "");
      if (!note.contains(DE_REFERRENCED_NOTE)) {
         note = note + DE_REFERRENCED_NOTE;
         changes.setSoleAttributeValue(task, AtsAttributeTypes.WorkflowNotes, note);
      }
      // Remove auto-gen version
      changes.deleteAttributes(task, AtsAttributeTypes.TaskAutoGenVersion);
   }
}
