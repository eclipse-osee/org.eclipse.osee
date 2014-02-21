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
import java.util.List;
import org.eclipse.nebula.widgets.xviewer.customize.CustomizeData;
import org.eclipse.osee.ats.core.client.config.AtsBulkLoad;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.world.search.WorldSearchItem.SearchType;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;

/**
 * @author Donald G. Dunne
 */
public class WorldEditorReloadProvider extends WorldEditorProvider {

   private final String name;
   private final Collection<String> guids;
   private Collection<Artifact> artifacts;
   private boolean reload = true;

   public WorldEditorReloadProvider(String name, Collection<String> guids) {
      this(name, guids, null, TableLoadOption.None);
   }

   public WorldEditorReloadProvider(String name, Collection<String> guids, CustomizeData customizeData, TableLoadOption... tableLoadOption) {
      super(customizeData, tableLoadOption);
      this.name = name;
      this.guids = guids;
   }

   @Override
   public IWorldEditorProvider copyProvider() {
      return new WorldEditorReloadProvider(name, guids, customizeData, tableLoadOptions);
   }

   @Override
   public void run(WorldEditor worldEditor, SearchType searchtype, boolean forcePend) {
      worldEditor.getWorldComposite().load(name, artifacts, customizeData, getTableLoadOptions());
   }

   @Override
   public String getName() {
      return name;
   }

   public Collection<Artifact> getArtifacts() {
      return artifacts;
   }

   public boolean isReload() {
      return reload;
   }

   public void searchAndLoad() {
      List<String> validGuids = getValidGuids();
      if (validGuids.isEmpty()) {
         AWorkbench.popup("No valid guids to load");
      } else {
         artifacts = ArtifactQuery.getArtifactListFromIds(new ArrayList<String>(validGuids), AtsUtil.getAtsBranch());
         AtsBulkLoad.bulkLoadArtifacts(artifacts);
      }
      reload = false;
   }

   public List<String> getValidGuids() {
      List<String> validGuids = new ArrayList<String>();
      for (String guid : guids) {
         if (GUID.isValid(guid)) {
            validGuids.add(guid);
         }
      }
      return validGuids;
   }
}
