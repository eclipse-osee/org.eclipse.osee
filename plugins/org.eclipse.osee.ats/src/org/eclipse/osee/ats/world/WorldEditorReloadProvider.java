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
package org.eclipse.osee.ats.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.osee.ats.core.client.config.AtsBulkLoad;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.world.search.WorldSearchItem.SearchType;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;

/**
 * @author Donald G. Dunne
 */
public class WorldEditorReloadProvider extends WorldEditorProvider {

   private final String name;
   private final Collection<ArtifactId> artifactIds;
   private Collection<Artifact> artifacts;
   private boolean reload = true;
   private final BranchId branch;

   public WorldEditorReloadProvider(String name, BranchId branch, Collection<ArtifactId> artifactIds) {
      this(name, branch, artifactIds, null, TableLoadOption.None);
   }

   public WorldEditorReloadProvider(String name, BranchId branch, Collection<ArtifactId> artifactIds, CustomizeData customizeData, TableLoadOption... tableLoadOption) {
      super(customizeData, tableLoadOption);
      this.name = name;
      this.branch = branch;
      this.artifactIds = artifactIds;
   }

   @Override
   public IWorldEditorProvider copyProvider() {
      WorldEditorReloadProvider provider =
         new WorldEditorReloadProvider(name, branch, artifactIds, customizeData, tableLoadOptions);
      provider.setReload(reload);
      provider.artifacts = artifacts;
      return provider;
   }

   @Override
   public String getName() {
      return name;
   }

   public boolean isReload() {
      return reload;
   }

   public BranchId getBranch() {
      return branch;
   }

   public boolean searchAndLoad() {
      List<ArtifactId> validArtifactIds = getValidArtUuids();
      if (validArtifactIds.isEmpty()) {
         AWorkbench.popup("No valid ids to load");
      } else {
         BranchId atsBranch = AtsClientService.get().getAtsBranch();
         if (atsBranch.equals(branch)) {
            artifacts = ArtifactQuery.getArtifactListFrom(validArtifactIds, atsBranch);
            AtsBulkLoad.bulkLoadArtifacts(artifacts);
         } else {
            artifacts = new ArrayList<>();
         }
      }
      reload = false;
      return !artifacts.isEmpty();
   }

   public List<ArtifactId> getValidArtUuids() {
      List<ArtifactId> validartUuids = new ArrayList<>();
      for (ArtifactId artifactId : artifactIds) {
         if (artifactId.isValid()) {
            validartUuids.add(artifactId);
         }
      }
      return validartUuids;
   }

   @Override
   public Collection<Artifact> performSearch(SearchType searchType) {
      if (searchType == SearchType.ReSearch) {
         List<Integer> ids = new LinkedList<>();
         for (Artifact art : artifacts) {
            ids.add(art.getArtId());
         }
         artifacts = ArtifactQuery.getArtifactListFromIds(ids, AtsClientService.get().getAtsBranch());
      }
      return artifacts;
   }

   public void setReload(boolean reload) {
      this.reload = reload;
   }
}