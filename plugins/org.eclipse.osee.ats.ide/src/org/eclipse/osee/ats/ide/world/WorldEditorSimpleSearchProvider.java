/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
