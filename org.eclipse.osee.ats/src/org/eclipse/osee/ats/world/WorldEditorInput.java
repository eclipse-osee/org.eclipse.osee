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

import java.util.Collection;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.world.search.WorldSearchItem;
import org.eclipse.osee.ats.world.search.WorldSearchItem.SearchType;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.CustomizeData;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * @author Donald G. Dunne
 */
public class WorldEditorInput implements IEditorInput {

   private final TableLoadOption[] tableLoadOptions;
   private final WorldSearchItem searchItem;
   private final SearchType searchType;
   private String name = "World Editor";
   private final Collection<? extends Artifact> arts;
   private final CustomizeData customizeData;

   /**
    * @return the customizeData
    */
   public CustomizeData getCustomizeData() {
      return customizeData;
   }

   /**
    * @param artifact
    */
   public WorldEditorInput(WorldSearchItem searchItem, SearchType searchType, CustomizeData customizeData, TableLoadOption... tableLoadOptions) {
      this.searchItem = searchItem;
      this.searchType = searchType;
      this.customizeData = customizeData;
      this.tableLoadOptions = tableLoadOptions;
      this.arts = null;
   }

   public WorldEditorInput(String name, Collection<? extends Artifact> arts, CustomizeData customizeData, TableLoadOption... tableLoadOption) {
      this.name = name;
      this.arts = arts;
      this.customizeData = customizeData;
      tableLoadOptions = tableLoadOption;
      this.searchType = SearchType.Search;
      this.searchItem = null;
   }

   @Override
   public boolean equals(Object obj) {
      if (getSearchItem() == null) return false;
      if (obj instanceof WorldEditorInput) {
         WorldEditorInput input = (WorldEditorInput) obj;
         if (input.getSearchItem() == null) {
            return false;
         }
         //         return input.getSearchItem().equals(getSearchItem());
      }
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.IEditorInput#exists()
    */
   public boolean exists() {
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
    */
   public ImageDescriptor getImageDescriptor() {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.IEditorInput#getPersistable()
    */
   public IPersistableElement getPersistable() {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.IEditorInput#getToolTipText()
    */
   public String getToolTipText() {
      return "";
   }

   /* (non-Javadoc)
    * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
    */
   @SuppressWarnings("unchecked")
   @Override
   public Object getAdapter(Class adapter) {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.IEditorInput#getName()
    */
   @Override
   public String getName() {
      if (searchItem != null && searchItem.getName() != null) {
         return searchItem.getName();
      }
      return name;
   }

   /**
    * @return the tableLoadOptions
    */
   public TableLoadOption[] getTableLoadOptions() {
      return tableLoadOptions;
   }

   /**
    * @return the searchItem
    */
   public WorldSearchItem getSearchItem() {
      return searchItem;
   }

   /**
    * @return the searchType
    */
   public SearchType getSearchType() {
      return searchType;
   }

   /**
    * @return the arts
    */
   public Collection<? extends Artifact> getArts() {
      return arts;
   }

   /**
    * @param name the name to set
    */
   public void setName(String name) {
      this.name = name;
   }

}
