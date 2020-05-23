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
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.ide.world.search.WorldSearchItem.SearchType;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;

/**
 * @author Donald G. Dunne
 */
public interface IWorldEditorProvider {

   /**
    * Called to start the process of search and load.
    */
   public void run(WorldEditor worldEditor, SearchType searchType, boolean forcePend);

   default public void run(WorldEditor worldEditor, SearchType searchType, boolean forcePend, boolean search2) {
      // do nothing
   }

   public String getSelectedName(SearchType searchType);

   public String getName();

   public IAtsVersion getTargetedVersionArtifact();

   public IWorldEditorProvider copyProvider();

   public void setCustomizeData(CustomizeData customizeData);

   public void setTableLoadOptions(TableLoadOption... tableLoadOptions);

   /**
    * Called in background during run process to perform the search. Implementers should perform new searches of the
    * objects so they get loaded fresh. At this point, any items have already been de-cached.
    */
   Collection<? extends ArtifactId> performSearch(SearchType searchType);

}
