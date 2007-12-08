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

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.util.PersistenceMemo;
import org.eclipse.osee.framework.jdk.core.util.PersistenceObject;
import org.eclipse.osee.framework.jdk.core.util.io.Streams;
import org.eclipse.osee.framework.skynet.core.artifact.AttributeMemo;
import org.eclipse.osee.framework.skynet.core.artifact.CacheArtifactModifiedEvent;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactModifiedEvent.ModType;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;

/**
 * @author Ryan D. Brooks
 */
public abstract class Attribute implements PersistenceObject {

   private static final SkynetEventManager eventManager = SkynetEventManager.getInstance();

   private String name;
   private DynamicAttributeManager manager;
   private AttributeMemo memo;
   private boolean required;
   private boolean deletable;
   private boolean deleted;
   protected boolean dirty;
   private IMediaResolver resolver;

   /**
    * Create a default attribute. This is available for persistance
    */
   protected Attribute(IMediaResolver resolver) {
      this.name = null;
      this.required = false;
      this.deletable = false;
      this.dirty = true;
      this.deleted = false;
      this.memo = null;
      this.resolver = resolver;
   }

   /**
    * Create an attribute with a particular name. Attributes are required by default.
    * 
    * @param name The name of the attribute
    */
   protected Attribute(IMediaResolver resolver, String name) {
      super();
      this.name = name;
      this.required = true;
      this.deletable = true;
      this.dirty = false;
      this.memo = null;
      this.resolver = resolver;
   }

   public void setDirty() {
      checkDeleted();
      this.dirty = true;
      if (getManager() != null && getManager().getParentArtifact() != null) {
         getManager().getParentArtifact().setInTransaction(false);
         eventManager.kick(new CacheArtifactModifiedEvent(getManager().getParentArtifact(), ModType.Changed, this));
      }
   }

   /**
    * @return Returns the name.
    */
   public String getName() {
      checkDeleted();
      return name;
   }

   /**
    * @param name The name to set.
    */
   public void setName(String name) {
      checkDeleted();
      this.name = name;
      dirty = true;
   }

   /**
    * @return Returns the required.
    */
   public boolean isRequired() {
      checkDeleted();
      return required;
   }

   /**
    * @param required The required to set.
    */
   public void setRequired(boolean required) {
      checkDeleted();
      this.required = required;
   }

   public abstract String getTypeName();

   /*
    * (non-Javadoc)
    * 
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      checkDeleted();
      //      return getTypeName() + ": " + getStringData();
      return getNameValueDescription();
   }

   public String getNameValueDescription() {
      return manager.getDescriptor().getName() + ": " + getStringData();
   }

   /**
    * @return Returns the deletable.
    */
   public boolean isDeletable() {
      checkDeleted();
      return deletable;
   }

   /**
    * @param deletable The deletable to set.
    */
   public void setDeletable(boolean deletable) {
      checkDeleted();
      this.deletable = deletable;
   }

   public abstract void setValidityXml(String validityXml) throws Exception;

   /**
    * @param parent The parent to set.
    */
   protected void setParent(DynamicAttributeManager parent) {
      checkDeleted();
      this.manager = parent;
   }

   /**
    * @return Returns the parent.
    */
   public DynamicAttributeManager getManager() {
      checkDeleted();
      return manager;
   }

   /**
    * @return Returns the dirty.
    */
   public boolean isDirty() {
      return dirty;
   }

   /**
    * Set this attribute as not dirty. Should only be called my the persistence manager once it has persisted this
    * attribute.
    */
   public void setNotDirty() {
      checkDeleted();
      this.dirty = false;
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.plugin.core.util.PersistenceObject#getPersistenceMemo()
    */
   public AttributeMemo getPersistenceMemo() {
      checkDeleted();
      return memo;
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.plugin.core.util.PersistenceObject#setPersistenceMemo(osee.plugin.core.util.PersistenceMemo)
    */
   public void setPersistenceMemo(PersistenceMemo memo) {
      checkDeleted();
      if (memo instanceof AttributeMemo)
         this.memo = (AttributeMemo) memo;
      else
         throw new IllegalArgumentException("Invalid memo type");
   }

   public void delete() {
      checkDeleted();
      if (deletable) {
         manager.removeAttribute(this);
      }
   }

   public boolean isDeleted() {
      return deleted;
   }

   protected void checkDeleted() {
      if (deleted) throw new IllegalStateException("This artifact has been deleted");
   }

   /**
    * @param data
    */
   public void setDat(InputStream data) {
      if (resolver == null) throw new IllegalStateException("Resolver can not be null");

      if (resolver.setValue(data)) setDirty();
   }

   public void loadDat(InputStream data) {
      if (resolver == null) throw new IllegalStateException("Resolver can not be null");

      resolver.setValue(data);
   }

   public byte[] getDat() {
      if (resolver == null) throw new IllegalStateException("Resolver can not be null");

      return resolver.getValue();
   }

   public void setBlobData(InputStream stream) {
      resolver.setBlobData(stream);
   }

   public byte[] getBlobData() {
      return resolver.getBlobData();
   }

   public void setVarchar(String varchar) {
      resolver.setVarchar(varchar);
   }

   public String getVarchar() {
      return resolver.getvarchar();
   }

   public String getStringData() {
      try {
         // TODO this is inefficient for a value stored as a string to begin with
         byte[] data = getDat();
         if (data == null) {
            return null;
         }
         return new String(data, "UTF-8");
      } catch (IOException ex) {
         throw new RuntimeException("This should never happen", ex);
      }
   }

   public void setStringData(String value) {
      setStringData(value, true);
   }

   public void setStringData(String value, boolean perssistAttribute) {
      if (value.equals(getStringData())) return;
      try {
         if (value != null) {
            if (perssistAttribute)
               setDat(Streams.convertStringToInputStream(value, "UTF-8"));
            else
               loadDat(Streams.convertStringToInputStream(value, "UTF-8"));
         }
      } catch (UnsupportedEncodingException ex) {
         throw new RuntimeException("This should never happen", ex);
      }
   }

   public void swagValue(String value) {
      setStringData(value);
   }

   protected IMediaResolver getResolver() {
      return resolver;
   }

   public void replaceAll(String regex, String replacement) {
      replaceAll(Pattern.compile(regex), replacement);
   }

   public void replaceAll(Pattern pattern, String replacement) {
      setStringData(pattern.matcher(getStringData()).replaceAll(replacement));
   }

   /**
    * Purge attribute from the database.
    */
   public void purge() throws SQLException {
      manager.purge(this);
      deleted = true;
   }

   public boolean isEqualInValueTo(Attribute attribute) {
      if (attribute == null) throw new IllegalArgumentException("attribute can not be null");

      String stringData = getStringData();
      if (stringData == null) {
         return attribute.getStringData() == null;
      }

      return stringData.equals(attribute.getStringData());
   }
}
