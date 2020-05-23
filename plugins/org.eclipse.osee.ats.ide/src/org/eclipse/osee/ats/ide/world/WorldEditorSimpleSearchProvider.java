/*********************************************************************
 * Copyright (c) 2016 Boeing
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
import org.eclipse.osee.ats.ide.world.search.WorldSearchItem.SearchType;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;

/**
 * @author Donald G. Dunne
 */
public class WorldEditorSimpleSearchProvider extends WorldEditorProvider {

   private final IWorldEditorSimpleSearchProvider worldEditorSimpleSearchProvider;

   public WorldEditorSimpleSearchProvider(IWorldEditorSimpleSearchProvider worldEditorSimpleSearchProvider) {
      super(null, new TableLoadOption[] {TableLoadOption.None});
      this.worldEditorSimpleSearchProvider = worldEditorSimpleSearchProvider;
   }

   @Override
   public IWorldEditorProvider copyProvider() {
      return worldEditorSimpleSearchProvider.copyProvider();
   }

   @Override
   public String getName() {
      return worldEditorSimpleSearchProvider.getSearchName();
   }

   @Override
   public Collection<Artifact> performSearch(SearchType searchType) {
      return worldEditorSimpleSearchProvider.performSearch(searchType);
   }

   public static interface IWorldEditorSimpleSearchProvider {
      public Collection<Artifact> performSearch(SearchType searchType);

      public String getSearchName();

      public IWorldEditorProvider copyProvider();
   }
}
