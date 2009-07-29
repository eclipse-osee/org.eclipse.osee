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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;

/**
 * @author Donald G. Dunne
 */
public abstract class WorldEditorProvider implements IWorldEditorProvider {

   protected TableLoadOption[] tableLoadOptions;
   protected CustomizeData customizeData;

   public WorldEditorProvider(CustomizeData customizeData, TableLoadOption[] tableLoadOptions) {
      this.customizeData = customizeData;
      this.tableLoadOptions = tableLoadOptions;
   }

   @Override
   public String getSelectedName(SearchType searchType) throws OseeCoreException {
      return getName();
   }

   @Override
   public VersionArtifact getTargetedVersionArtifact() throws OseeCoreException {
      return null;
   }

   public TableLoadOption[] getTableLoadOptions() {
      return tableLoadOptions;
   }

   public CustomizeData getCustomizeData() {
      return customizeData;
   }

   public void setTableLoadOptions(TableLoadOption... tableLoadOptions) {
      this.tableLoadOptions = tableLoadOptions;
   }

   public void setCustomizeData(CustomizeData customizeData) {
      this.customizeData = customizeData;
   }

}
