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
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.world.search.WorldSearchItem.SearchType;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;

/**
 * @author Donald G. Dunne
 */
public class WorldEditorReloadProvider extends WorldEditorProvider {

   private final String name;
   private final Collection<Integer> artUuids;
   private Collection<Artifact> artifacts;
   private boolean reload = true;
   private final long branchUuid;

   public WorldEditorReloadProvider(String name, long branchUuid, Collection<Integer> artUuids) {
      this(name, branchUuid, artUuids, null, TableLoadOption.None);
   }

   public WorldEditorReloadProvider(String name, long branchUuid, Collection<Integer> artUuids, CustomizeData customizeData, TableLoadOption... tableLoadOption) {
      super(customizeData, tableLoadOption);
      this.name = name;
      this.branchUuid = branchUuid;
      this.artUuids = artUuids;
   }

   @Override
   public IWorldEditorProvider copyProvider() {
      WorldEditorReloadProvider provider =
         new WorldEditorReloadProvider(name, branchUuid, artUuids, customizeData, tableLoadOptions);
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

   public long getBranchUuid() {
      return branchUuid;
   }

   public boolean searchAndLoad() {
      List<Integer> validartUuids = getValidArtUuids();
      if (validartUuids.isEmpty()) {
         AWorkbench.popup("No valid ids to load");
      } else {
         artifacts = new ArrayList<>();
         if (AtsUtilCore.getAtsBranch().getUuid().equals(branchUuid)) {
            artifacts.addAll(
               ArtifactQuery.getArtifactListFromIds(new ArrayList<Integer>(validartUuids), AtsUtilCore.getAtsBranch()));
            AtsBulkLoad.bulkLoadArtifacts(artifacts);
         }
      }
      reload = false;
      return artifacts.size() > 0;
   }

   public List<Integer> getValidArtUuids() {
      List<Integer> validartUuids = new ArrayList<>();
      for (Integer artUuid : artUuids) {
         if (artUuid > 0) {
            validartUuids.add(artUuid);
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
         artifacts = ArtifactQuery.getArtifactListFromIds(ids, AtsUtilCore.getAtsBranch());
      }
      return artifacts;
   }

   public void setReload(boolean reload) {
      this.reload = reload;
   }
}
