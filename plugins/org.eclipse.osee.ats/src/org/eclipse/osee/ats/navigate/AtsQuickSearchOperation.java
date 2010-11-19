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
package org.eclipse.osee.ats.navigate;

import static org.eclipse.osee.framework.core.enums.DeletionFlag.EXCLUDE_DELETED;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.world.IWorldEditorConsumer;
import org.eclipse.osee.ats.world.WorldEditor;
import org.eclipse.osee.ats.world.WorldEditorOperationProvider;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.util.HumanReadableId;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;

/**
 * @author Donald G. Dunne
 */
public class AtsQuickSearchOperation extends AbstractOperation implements IWorldEditorConsumer {
   Set<Artifact> allArtifacts = new HashSet<Artifact>();
   private final AtsQuickSearchData data;
   private WorldEditor worldEditor;

   public AtsQuickSearchOperation(AtsQuickSearchData data) {
      super(data.toString(), AtsPlugin.PLUGIN_ID);
      this.data = data;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws OseeCoreException {
      if (!Strings.isValid(data.getSearchStr())) {
         AWorkbench.popup("Must Enter Search String");
         return;
      }
      if (worldEditor == null) {
         WorldEditor.open(new WorldEditorOperationProvider(new AtsQuickSearchOperation(data)));
         return;
      }
      for (String str : data.getSearchStr().split(", ")) {
         if (HumanReadableId.isValid(str)) {
            try {
               Artifact art = ArtifactQuery.getArtifactFromId(str, AtsUtil.getAtsBranch());
               if (art != null) {
                  allArtifacts.add(art);
               }
            } catch (ArtifactDoesNotExist ex) {
               // do nothing
            }
         }
      }
      for (Artifact art : ArtifactQuery.getArtifactListFromAttributeKeywords(AtsUtil.getAtsBranch(),
         data.getSearchStr(), false, EXCLUDE_DELETED, false)) {
         // only ATS Artifacts
         if (art instanceof AbstractWorkflowArtifact) {
            AbstractWorkflowArtifact sma = (AbstractWorkflowArtifact) art;
            // default excludes canceled/completed
            if (data.isIncludeCompleteCancelled() == false) {
               if (!sma.isCompletedOrCancelled()) {
                  allArtifacts.add(art);
               }
            } else {
               allArtifacts.add(art);
            }
         }
      }
      if (allArtifacts.isEmpty()) {
         AWorkbench.popup(getName(), getName() + "\n\nNo Results Found");
      } else if (worldEditor != null) {
         worldEditor.getWorldComposite().load(getName(), allArtifacts, TableLoadOption.None);
      }
   }

   @Override
   public String getName() {
      return data.toString();
   }

   public Set<Artifact> getAllArtifacts() {
      return allArtifacts;
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
