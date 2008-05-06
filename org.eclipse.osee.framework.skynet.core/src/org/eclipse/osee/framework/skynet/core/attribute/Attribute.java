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
package org.eclipse.osee.framework.skynet.core.attribute;

import java.sql.SQLException;
import org.eclipse.osee.framework.jdk.core.util.PersistenceMemo;
import org.eclipse.osee.framework.jdk.core.util.PersistenceObject;
import org.eclipse.osee.framework.skynet.core.artifact.AttributeMemo;

/**
 * @author Ryan D. Brooks
 */
public abstract class Attribute<T> implements PersistenceObject {

   private final DynamicAttributeDescriptor attributeType;
   private AttributeStateManager stateManager;
   private AttributeMemo memo;

   protected Attribute(DynamicAttributeDescriptor attributeType) {
      this.attributeType = attributeType;
      this.stateManager = null;
      this.memo = null;
   }

   /**
    * @return the attribute name/value description
    */
   public String getNameValueDescription() {
      return attributeType.getName() + ": " + toString();
   }

   /**
    * @return stateManager Object managing this attributes state
    */
   protected AttributeStateManager getStateManager() {
      return stateManager;
   }

   /**
    * @param stateManager Object managing this attributes state
    */
   protected void setStateManager(AttributeStateManager stateManager) {
      this.stateManager = stateManager;
      this.stateManager.setAttribute(this);
   }

   /**
    * @return attributeType Attribute Type Information
    */
   public DynamicAttributeDescriptor getAttributeType() {
      return attributeType;
   }

   /**
    * @return manager The instance managing this attribute.
    */
   public DynamicAttributeManager getAttributeManager() {
      return stateManager.getAttributeManager();
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.plugin.core.util.PersistenceObject#getPersistenceMemo()
    */
   public AttributeMemo getPersistenceMemo() {
      stateManager.checkDeleted();
      return memo;
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.plugin.core.util.PersistenceObject#setPersistenceMemo(osee.plugin.core.util.PersistenceMemo)
    */
   public void setPersistenceMemo(PersistenceMemo memo) {
      stateManager.checkDeleted();
      if (memo instanceof AttributeMemo) {
         this.memo = (AttributeMemo) memo;
      } else {
         throw new IllegalArgumentException("Invalid memo type");
      }
   }

   /**
    * @return <b>true</b> if this attribute is dirty
    */
   public boolean isDirty() {
      return getStateManager().isDirty();
   }

   /**
    * Deletes the attribute
    */
   public void delete() {
      if (stateManager.isDeletable()) {
         getAttributeManager().removeAttribute(this);
      }
   }

   /**
    * Purges the attribute from the database.
    */
   public void purge() throws SQLException {
      getAttributeManager().purge(this);
      getStateManager().setDeleted(true);
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      return getDisplayableString();
   }

   public abstract void setValue(T value);

   public abstract T getValue();

   public abstract String getDisplayableString();
}