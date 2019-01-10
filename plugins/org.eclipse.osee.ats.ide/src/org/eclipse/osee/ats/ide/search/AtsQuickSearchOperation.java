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
package org.eclipse.osee.ats.ide.search;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.world.IWorldEditorConsumer;
import org.eclipse.osee.ats.ide.world.WorldEditor;
import org.eclipse.osee.ats.ide.world.WorldEditorOperation;
import org.eclipse.osee.ats.ide.world.WorldEditorOperationProvider;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
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
         Collections.castAll(AtsClientService.get().getQueryService().getArtifactsByIdsOrAtsIds(data.getSearchStr())));
      for (Artifact art : ArtifactQuery.getArtifactListFromAttributeKeywords(AtsClientService.get().getAtsBranch(),
         data.getSearchStr(), false, DeletionFlag.EXCLUDE_DELETED, false)) {
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
