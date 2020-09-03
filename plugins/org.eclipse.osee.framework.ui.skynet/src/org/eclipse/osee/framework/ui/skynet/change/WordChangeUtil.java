/*********************************************************************
 * Copyright (c) 2020 Boeing
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
package org.eclipse.osee.framework.ui.skynet.change;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.util.RendererOption;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactHierarchyComparator;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.change.ArtifactDelta;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.render.ArtifactGuis;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;
import org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;

/**
 * This class is for creating a Word Template Change Report, which uses the Word Diff to compare artifacts. Can add
 * context artifacts that includes all ancestors and siblings. Includes the option to only include Word Content Changes
 *
 * @author Branden W. Phillips
 */
public final class WordChangeUtil {

   public static void generateWordTemplateChangeReport(List<Change> changes, PresentationType presentationType, boolean addContextArtifacts, boolean wordChangesOnly) {
      try {
         if (!changes.isEmpty()) {
            Collection<Change> changesForReport = new ArrayList<>(changes.size());
            Set<Artifact> artifacts = new HashSet<>();
            for (Change change : changes) {
               boolean addChange = wordChangesOnly ? change.getChangeItem().getItemTypeId().equals(
                  CoreAttributeTypes.WordTemplateContent) : true;
               if (addChange) {
                  Artifact artifact = change.getChangeArtifact();
                  if (!artifacts.contains(artifact)) {
                     artifacts.add(artifact);
                     changesForReport.add(change);
                  }
               }
            }
            Collection<ArtifactDelta> artifactDeltas = ChangeManager.getCompareArtifacts(changesForReport);
            if (addContextArtifacts) {
               artifactDeltas = updateDeltas(artifactDeltas);
            }
            if (ArtifactGuis.checkDeletedOnParent(artifacts)) {
               String pathPrefix = RenderingUtil.getAssociatedArtifactName(changes);
               WordTemplateRenderer preferredRenderer = new WordTemplateRenderer();
               preferredRenderer.updateOption(RendererOption.VIEW, Handlers.getViewId());
               RendererManager.diffInJobWithPreferedRenderer(artifactDeltas, pathPrefix, preferredRenderer,
                  presentationType);
            }
         } else {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "No changes have been found for this request");
         }
      } catch (Exception ex) {
         XResultData result = new XResultData();
         result.error(Lib.exceptionToString(ex));
         ResultsEditor.open("Change Report Error", result);
      }
   }

   private static Collection<ArtifactDelta> updateDeltas(Collection<ArtifactDelta> originalDeltas) {
      Map<ArtifactId, ArtifactDelta> originalDeltaMap = new HashMap<>();
      List<ArtifactDelta> returnDeltas = new LinkedList<>();
      List<ArtifactDelta> deletedDeltas = new LinkedList<>();
      List<Artifact> artList = new LinkedList<>();

      /**
       * This processes each of the original deltas that are passed into this method
       */
      for (ArtifactDelta delta : originalDeltas) {
         ArtifactId artId = delta.getEndArtifact();
         Artifact art = delta.getEndArtifact();
         if (artId == null || art == null) {
            artId = delta.getBaseArtifact();
            art = delta.getBaseArtifact();
         }
         /**
          * If the artifact is historical, we need to go out and query for a non historical version that way the
          * relations are supported and sorted doesn't fail. If the artifact was deleted, that delta is stashed for
          * later and the context is not supported.
          */
         if (art.isHistorical()) {
            art = ArtifactQuery.checkArtifactFromId(artId, delta.getBranch());
            if (art == null) {
               deletedDeltas.add(delta);
               continue;
            }
         }
         originalDeltaMap.put(artId, delta);

         if (!artList.contains(art)) {
            artList.add(art);
         }

         /**
          * Using the parent, the siblings are found and added to the artifact list, then the ancestors are followed up
          * the hierarchy until the root, or an already added artifact.
          */
         Artifact parent = art.getParent();
         if (parent != null) {
            for (Artifact sibling : parent.getChildren()) {
               if (!artList.contains(sibling) && !sibling.getArtifactType().equals(CoreArtifactTypes.HeadingMsWord)) {
                  artList.add(sibling);
               }
            }
            while (!parent.equals(CoreArtifactTokens.DefaultHierarchyRoot)) {
               if (!artList.contains(parent)) {
                  artList.add(parent);
                  parent = parent.getParent();
               } else {
                  break;
               }
            }
         }
      }
      /**
       * Sort that list of artifacts that have been added by their location in the OSEE Hierarchy
       */
      ArtifactHierarchyComparator comparator = new ArtifactHierarchyComparator();
      artList.sort(comparator);

      /**
       * Create a new list in sorted order of ArtifactDeltas. Either use the original deltas for those artifacts, or
       * create a new delta consisting of the same artifact for those that have not changed. After that, add in the
       * deleted artifact deltas.
       */
      for (Artifact art : artList) {
         ArtifactDelta delta = originalDeltaMap.get(art);
         if (delta == null) {
            delta = new ArtifactDelta(art, art);
         }
         returnDeltas.add(delta);
      }
      for (ArtifactDelta deletedDelta : deletedDeltas) {
         returnDeltas.add(deletedDelta);
      }

      return returnDeltas;
   }
}
