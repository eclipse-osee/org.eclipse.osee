/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.ide.search.quick;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.world.IWorldEditorConsumer;
import org.eclipse.osee.ats.ide.world.WorldEditor;
import org.eclipse.osee.ats.ide.world.WorldEditorOperation;
import org.eclipse.osee.ats.ide.world.WorldEditorOperationProvider;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;

/**
 * @author Donald G. Dunne
 */
class AtsQuickSearchOperation extends AbstractOperation implements WorldEditorOperation, IWorldEditorConsumer {
   Set<Artifact> allArtifacts = new HashSet<>();
   private final AtsQuickSearchData data;
   private WorldEditor worldEditor;

   public AtsQuickSearchOperation(AtsQuickSearchData data) {
      super(data.toString(), Activator.PLUGIN_ID);
      this.data = data;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) {
      if (!Strings.isValid(data.getSearchStr())) {
         AWorkbench.popup("Must Enter Search String");
         return;
      }
      if (worldEditor == null) {
         WorldEditor.open(new WorldEditorOperationProvider(new AtsQuickSearchOperation(data)));
         return;
      }
      performSearch();
      if (allArtifacts.isEmpty()) {
         AWorkbench.popup(getName(), getName() + "\n\nNo Results Found");
      } else if (worldEditor != null) {
         worldEditor.getWorldComposite().load(getName(), allArtifacts, TableLoadOption.None);
      }
   }

   @Override
   public Collection<Artifact> performSearch() {
      allArtifacts.clear();
      allArtifacts.addAll(
         Collections.castAll(AtsApiService.get().getQueryService().getArtifactsByIdsOrAtsIds(data.getSearchStr())));
      for (Artifact art : ArtifactQuery.getArtifactListFromAttributeKeywords(AtsApiService.get().getAtsBranch(),
         data.getSearchStr(), false, DeletionFlag.EXCLUDE_DELETED, false)) {
         try {
            // only ATS Artifacts
            if (art instanceof AbstractWorkflowArtifact) {
               AbstractWorkflowArtifact awa = (AbstractWorkflowArtifact) art;
               // default excludes canceled/completed
               if (data.isIncludeCompleteCancelled() == false) {
                  if (!awa.isCompletedOrCancelled()) {
                     allArtifacts.add(art);
                  }
               } else {
                  allArtifacts.add(art);
               }
            }
         } catch (final Exception ex) {
            String str = "Exception occurred in " + art.toStringWithId();
            OseeLog.log(Activator.class, Level.SEVERE, str, ex);
         }
      }
      return allArtifacts;
   }

   @Override
   public String getName() {
      return data.toString();
   }

   @Override
   public WorldEditor getWorldEditor() {
      return worldEditor;
   }

   @Override
   public void setWorldEditor(WorldEditor worldEditor) {
      this.worldEditor = worldEditor;
   }

}
