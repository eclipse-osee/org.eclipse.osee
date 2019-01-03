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
