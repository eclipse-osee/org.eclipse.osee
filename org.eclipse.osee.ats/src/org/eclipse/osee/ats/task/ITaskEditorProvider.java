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
package org.eclipse.osee.ats.task;

import java.util.Collection;
import org.eclipse.nebula.widgets.xviewer.customize.CustomizeData;
import org.eclipse.osee.ats.world.search.WorldSearchItem.SearchType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;

/**
 * @author Donald G. Dunne
 */
public interface ITaskEditorProvider {

   public Collection<? extends Artifact> getTaskEditorTaskArtifacts() throws OseeCoreException;

   public String getTaskEditorLabel(SearchType searchType) throws OseeCoreException;

   public String getName() throws OseeCoreException;

   public Collection<TableLoadOption> getTableLoadOptions() throws OseeCoreException;

   public void setCustomizeData(CustomizeData customizeData);

   public void setTableLoadOptions(TableLoadOption... tableLoadOptions);

   public ITaskEditorProvider copyProvider();
}
