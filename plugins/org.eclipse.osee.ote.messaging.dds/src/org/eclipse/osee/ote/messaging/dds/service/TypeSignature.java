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
package org.eclipse.osee.ote.messaging.dds.service;

import java.lang.ref.WeakReference;

/**
 * Provides the necessary information to communicate type information for messaging
 * within the DDS system. This allows the DDS system to be introduced to new types
 * at run time, and be able to handle them properly.
 * 
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public class TypeSignature {
   private String typeName;
   private int typeDataSize;
   private WeakReference<Key> key;
   private String readerName;
   private String writerName;
   private WeakReference<ClassLoader> classLoader;

   /**
    * Create a <code>TypeSignature</code> with all of the necessary information.
    * 
    * @param typeName The name used to reference this type.
    * @param typeDataSize The full size in bytes of this type when transmitted.
    * @param key The <code>Key</code> used to distinguish instances of data for this type.
    * @param readerName The name used to load the reader class for this type from the supplied class loader
    * @param writerName The name used to load the reader class for this type from the supplied class loader
    * @param classLoader The class loader which can load the reader and writer for this type
    */
   public TypeSignature(String typeName, int typeDataSize, Key key, String readerName, String writerName, ClassLoader classLoader) {
      super();
      this.typeName = typeName;
      this.typeDataSize = typeDataSize;
      this.key = new WeakReference<Key>(key);
      this.readerName = readerName;
      this.writerName = writerName;
      this.classLoader = new WeakReference<ClassLoader>( classLoader);
   }

   /**
    * @return Returns the classLoader.
    */
   public ClassLoader getClassLoader() {
      return classLoader.get();
   }

   /**
    * @return Returns the key.
    */
   public Key getKey() {
      return key.get();
   }

   /**
    * @return Returns the readerName.
    */
   public String getReaderName() {
      return readerName;
   }

   /**
    * @return Returns the typeDataSize.
    */
   public int getTypeDataSize() {
      return typeDataSize;
   }

   /**
    * @return Returns the typeName.
    */
   public String getTypeName() {
      return typeName;
   }

   /**
    * @return Returns the writerName.
    */
   public String getWriterName() {
      return writerName;
   }
   
   public boolean equals(Object obj) {

      if (obj instanceof TypeSignature) {
         TypeSignature signature = (TypeSignature)obj;
         
         return typeName.equals(signature.typeName) &&
                typeDataSize == signature.typeDataSize &&
                readerName.equals(signature.readerName) &&
                writerName.equals(signature.writerName) &&
                classLoader.get().equals(signature.classLoader.get());
      }

      return false;
   }

   public int hashCode() {
      return typeName.hashCode();
   }
}
