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
import org.eclipse.osee.ats.world.search.WorldSearchItem;
import org.eclipse.osee.ats.world.search.WorldUISearchItem;
import org.eclipse.osee.ats.world.search.WorldSearchItem.SearchType;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeStateException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;

/**
 * @author Donald G. Dunne
 */
public class TaskEditorSearchItemProvider extends TaskEditorProvider {

   private final WorldSearchItem worldSearchItem;

   public TaskEditorSearchItemProvider(WorldSearchItem worldSearchItem) {
      this(worldSearchItem, null, TableLoadOption.None);
   }

   public TaskEditorSearchItemProvider(WorldSearchItem worldSearchItem, CustomizeData customizeData, TableLoadOption... tableLoadOptions) {
      super(customizeData, tableLoadOptions);
      this.worldSearchItem = worldSearchItem;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.ITaskEditorProvider#getTaskEditorLabel()
    */
   @Override
   public String getTaskEditorLabel(SearchType searchType) throws OseeCoreException {
      return worldSearchItem.getSelectedName(searchType);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.ITaskEditorProvider#getTaskEditorTaskArtifacts()
    */
   @Override
   public Collection<? extends Artifact> getTaskEditorTaskArtifacts() throws OseeCoreException {
      if (worldSearchItem instanceof WorldUISearchItem) {
         return ((WorldUISearchItem) worldSearchItem).performSearchGetResults(false, SearchType.ReSearch);
      } else
         throw new OseeStateException("Unsupported WorldSearchItem");
   }

   /**
    * @return the worldSearchItem
    */
   public WorldSearchItem getWorldSearchItem() {
      return worldSearchItem;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.task.ITaskEditorProvider#getName()
    */
   @Override
   public String getName() throws OseeCoreException {
      return worldSearchItem.getName();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.task.ITaskEditorProvider#copyProvider()
    */
   @Override
   public ITaskEditorProvider copyProvider() {
      return new TaskEditorSearchItemProvider(worldSearchItem.copy(), customizeData, tableLoadOptions);
   }

}
