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
package org.eclipse.osee.framework.ui.data.model.editor.model;

/**
 * @author Roberto E. Escobar
 */
public class DataType extends NodeModel {
   protected static final String EMPTY_STRING = "";
   private String name;
   private String namespace;
   private String uniqueId;

   public DataType() {
      this(EMPTY_STRING, EMPTY_STRING, EMPTY_STRING);
   }

   public DataType(String namespace, String name) {
      this(EMPTY_STRING, namespace, name);
   }

   public DataType(String typeId, String namespace, String name) {
      super();
      this.uniqueId = typeId;
      this.namespace = namespace;
      this.name = name;
   }

   /**
    * @return the name
    */
   public String getName() {
      return name;
   }

   /**
    * @param name the name to set
    */
   public void setName(String name) {
      if (this.name != name) {
         this.name = name;
         fireModelEvent();
      }
   }

   /**
    * @return the namespace
    */
   public String getNamespace() {
      return namespace;
   }

   /**
    * @param namespace the namespace to set
    */
   public void setNamespace(String namespace) {
      if (this.namespace != namespace) {
         this.namespace = namespace;
         fireModelEvent();
      }
   }

   /**
    * @return the typeId
    */
   public String getUniqueId() {
      return uniqueId;
   }

   /**
    * @param typeId the typeId to set
    */
   public void setUniqueId(String typeId) {
      this.uniqueId = typeId;
   }
}
