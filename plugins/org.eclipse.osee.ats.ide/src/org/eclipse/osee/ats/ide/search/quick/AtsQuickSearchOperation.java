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
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.NewSearchUI;

/**
 * ATS Quick Search Operation that can display results in either the WorldEditor or the Eclipse Search View.
 * <p>
 * Usage for WorldEditor results (default):
 * 
 * <pre>
 * AtsQuickSearchData data = new AtsQuickSearchData("ATS Quick Search", searchStr, includeCompleteCancelled);
 * AtsQuickSearchOperation operation = new AtsQuickSearchOperation(data);
 * Operations.executeAsJob(operation, true);
 * </pre>
 * <p>
 * Usage for Eclipse Search View results:
 * 
 * <pre>
 * AtsQuickSearchData data = new AtsQuickSearchData("ATS Quick Search", searchStr, includeCompleteCancelled);
 * data.setUseEclipseSearchView(true);
 * data.setCaseSensitive(caseSensitive);
 * data.setIncludeDeleted(includeDeleted);
 * AtsQuickSearchOperation.runInEclipseSearchView(data);
 * </pre>
 *
 * @author Donald G. Dunne
 */
public class AtsQuickSearchOperation extends AbstractOperation implements WorldEditorOperation, IWorldEditorConsumer {
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

      // Check if we should use Eclipse Search View
      if (data.isUseEclipseSearchView()) {
         runInEclipseSearchView(data);
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

   /**
    * Run the ATS Quick Search and display results in the Eclipse Search View. This method activates the Search Results
    * view and runs the query in the background.
    *
    * @param data the search data containing search criteria
    */
   public static void runInEclipseSearchView(AtsQuickSearchData data) {
      if (!Strings.isValid(data.getSearchStr())) {
         AWorkbench.popup("Must Enter Search String");
         return;
      }

      NewSearchUI.activateSearchResultView();
      ISearchQuery query = new AtsQuickSearchQuery(data);
      NewSearchUI.runQueryInBackground(query);
   }

   @Override
   public Collection<Artifact> performSearch() {
      allArtifacts.clear();
      allArtifacts.addAll(AtsApiService.get().getQueryServiceIde().getArtifactsByIdsOrAtsIds(data.getSearchStr()));

      DeletionFlag deletionFlag =
         data.isIncludeDeleted() ? DeletionFlag.INCLUDE_DELETED : DeletionFlag.EXCLUDE_DELETED;

      for (Artifact art : ArtifactQuery.getArtifactListFromAttributeKeywords(AtsApiService.get().getAtsBranch(),
         data.getSearchStr(), data.isCaseSensitive(), deletionFlag, false)) {
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
