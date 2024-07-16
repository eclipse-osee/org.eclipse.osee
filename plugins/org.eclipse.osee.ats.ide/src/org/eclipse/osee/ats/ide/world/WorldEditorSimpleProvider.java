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

package org.eclipse.osee.ats.ide.world;

import java.util.Collection;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.world.search.WorldSearchItem.SearchType;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;

/**
 * @author Donald G. Dunne
 */
public class WorldEditorSimpleProvider extends WorldEditorProvider {

   private final String name;
   private Collection<? extends ArtifactId> artifacts;
   private final Artifact expandToArtifact;

   public WorldEditorSimpleProvider(Collection<IAtsWorkItem> collection, String name) {
      this(name, AtsObjects.getArtifacts(collection), null, TableLoadOption.None);
   }

   public WorldEditorSimpleProvider(String name, Collection<? extends ArtifactId> collection) {
      this(name, collection, null, TableLoadOption.None);
   }

   public WorldEditorSimpleProvider(String name, Collection<? extends ArtifactId> collection, CustomizeData customizeData, TableLoadOption... tableLoadOption) {
      this(name, collection, customizeData, null, tableLoadOption);
   }

   /**
    * @param expandToArtifact if given, expand World Editor and highlight this item
    */
   public WorldEditorSimpleProvider(String name, Collection<? extends ArtifactId> artifacts, CustomizeData customizeData, Artifact expandToArtifact, TableLoadOption... tableLoadOption) {
      super(customizeData, tableLoadOption);
      this.name = name;
      this.artifacts = artifacts;
      this.expandToArtifact = expandToArtifact;
   }

   @Override
   public IWorldEditorProvider copyProvider() {
      return new WorldEditorSimpleProvider(name, artifacts, customizeData, tableLoadOptions);
   }

   @Override
   public String getName() {
      return name;
   }

   public Artifact getExpandToArtifact() {
      return expandToArtifact;
   }

   @Override
   public Collection<? extends ArtifactId> performSearch(SearchType searchType) {
      if (searchType == SearchType.ReSearch) {
         artifacts = ArtifactQuery.getArtifactListFrom(artifacts, AtsApiService.get().getAtsBranch());
      }
      return artifacts;
   }
}