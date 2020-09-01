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
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactHierarchyComparator;
import org.eclipse.osee.framework.skynet.core.change.ArtifactDelta;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;
import org.eclipse.osee.framework.ui.skynet.render.ArtifactGuis;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;
import org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer;

/**
 * This class is for creating a Word Template Change Report, which uses the Word Diff to compare artifacts. Can add
 * context artifacts that includes all ancestors and siblings. Includes the option to only include Word Content Changes
 *
 * @author Branden W. Phillips
 */
public final class WordChangeUtil {

   public static void generateWordTemplateChangeReport(List<Change> changes, PresentationType presentationType, boolean addContextArtifacts, boolean wordChangesOnly) {
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
      }
   }

   private static Collection<ArtifactDelta> updateDeltas(Collection<ArtifactDelta> originalDeltas) {
      List<ArtifactDelta> returnDeltas = new LinkedList<>();
      Map<ArtifactId, ArtifactDelta> originalDeltaMap = new HashMap<>();
      List<Artifact> artList = new LinkedList<>();

      /**
       * Loop through original artifact deltas, place them in a hash map, add to the new list, add siblings and
       * ancestors to the list
       */
      for (ArtifactDelta delta : originalDeltas) {
         ArtifactId artId = delta.getEndArtifact();
         Artifact art = delta.getEndArtifact();
         if (artId == null || art == null) {
            artId = delta.getBaseArtifact();
            art = delta.getBaseArtifact();
         }
         originalDeltaMap.put(artId, delta);

         if (!artList.contains(art)) {
            artList.add(art);
         }

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
       * Create a new list in sorted order, of ArtifactDeltas. Either use the original deltas for those artifacts, or
       * create a new delta consisting of the same artifact for those that have not changed
       */
      for (Artifact art : artList) {
         ArtifactDelta delta = originalDeltaMap.get(art);
         if (delta == null) {
            delta = new ArtifactDelta(art, art);
         }
         returnDeltas.add(delta);
      }

      return returnDeltas;
   }
}
