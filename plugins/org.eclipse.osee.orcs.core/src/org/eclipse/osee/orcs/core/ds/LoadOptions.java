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
package org.eclipse.osee.orcs.core.ds;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.LoadLevel;

/**
 * @author Roberto E. Escobar
 */
public class LoadOptions {

   private boolean historical;
   private DeletionFlag includeDeleted;
   private LoadLevel loadLevel;
   private Collection<Integer> attributeIds;
   private Collection<Integer> relationIds;
   private Collection<IAttributeType> attributeTypes;
   private Collection<IRelationType> relationTypes;

   public LoadOptions() {
      this(false, DeletionFlag.EXCLUDE_DELETED, LoadLevel.SHALLOW);
   }

   public LoadOptions(boolean historical, boolean includeDeleted, LoadLevel loadLevel) {
      this(historical, DeletionFlag.allowDeleted(includeDeleted), loadLevel);
   }

   public LoadOptions(boolean historical, DeletionFlag includeDeleted, LoadLevel loadLevel) {
      super();
      this.historical = historical;
      this.includeDeleted = includeDeleted;
      this.loadLevel = loadLevel;
   }

   public boolean isHistorical() {
      return historical;
   }

   public boolean areDeletedIncluded() {
      return includeDeleted.areDeletedAllowed();
   }

   public void setIncludeDeleted(boolean enabled) {
      includeDeleted = DeletionFlag.allowDeleted(enabled);
   }

   public LoadLevel getLoadLevel() {
      return loadLevel;
   }

   public void setHistorical(boolean historical) {
      this.historical = historical;
   }

   public void setLoadLevel(LoadLevel loadLevel) {
      this.loadLevel = loadLevel;
   }

   public boolean isSelectiveLoadingByType() {
      return (attributeTypes != null && !attributeTypes.isEmpty()) || (relationTypes != null && !relationTypes.isEmpty());
   }

   public boolean isSelectiveLoadingById() {
      return (attributeIds != null && !attributeIds.isEmpty()) || (relationIds != null && !relationIds.isEmpty());
   }

   public Collection<Integer> getAttributeIds() {
      return attributeIds;
   }

   public void setAttributeIds(Collection<Integer> attributeIds) {
      this.attributeIds = attributeIds;
   }

   public Collection<Integer> getRelationIds() {
      return relationIds;
   }

   public void setRelationIds(Collection<Integer> relationIds) {
      this.relationIds = relationIds;
   }

   public Collection<IAttributeType> getAttributeTypes() {
      return attributeTypes;
   }

   public void setAttributeTypes(Collection<IAttributeType> attributeTypes) {
      this.attributeTypes = attributeTypes;
   }

   public Collection<IRelationType> getRelationTypes() {
      return relationTypes;
   }

   public void setRelationTypes(Collection<IRelationType> relationTypes) {
      this.relationTypes = relationTypes;
   }

   @Override
   public String toString() {
      return "LoadOptions [historical=" + historical + ", includeDeleted=" + includeDeleted + ", loadLevel=" + loadLevel + "]";
   }

}