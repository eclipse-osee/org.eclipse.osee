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

import org.eclipse.nebula.widgets.xviewer.customize.CustomizeData;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.world.search.WorldSearchItem.SearchType;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;

/**
 * @author Donald G. Dunne
 */
public interface IWorldEditorProvider {

   public void run(WorldEditor worldEditor, SearchType searchType, boolean forcePend) throws OseeCoreException;

   public String getSelectedName(SearchType searchType) throws OseeCoreException;

   public String getName() throws OseeCoreException;

   public VersionArtifact getTargetedVersionArtifact() throws OseeCoreException;

   public IWorldEditorProvider copyProvider() throws OseeArgumentException;

   public void setCustomizeData(CustomizeData customizeData);

   public void setTableLoadOptions(TableLoadOption... tableLoadOptions);
}
