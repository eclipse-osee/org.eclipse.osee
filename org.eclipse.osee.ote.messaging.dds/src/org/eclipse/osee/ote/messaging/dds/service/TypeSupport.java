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

import org.eclipse.osee.ote.messaging.dds.ReturnCode;
import org.eclipse.osee.ote.messaging.dds.entity.DomainParticipant;

/**
 * The base class that needs to be extended by application specific type support classes.
 * This is used to allow the DDS system to recognize and make use of application specific
 * types for creating topics at run time.
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public abstract class TypeSupport {

   /**
    * The method which tells the system how large the data type is in bytes.
    * 
    * @return The integer count of bytes for the type.
    */
   protected abstract int getTypeDataSize();
   
   /**
    * The method which supplies the <code>Key</code> for distinguishing instances
    * of this data type.
    * 
    * @see Key
    */
   protected abstract Key getKey();
   
   /**
    * The method that supplies the class name for the application class that extends
    * <code>DataReader</code> for this type. The string returned must be formatted
    * such that the <code>ClassLoader</code> designated during registration will be able
    * to supply the reader.
    * 
    * @return The class name for the reader of this type.
    */
   protected abstract String getReaderName();
   
   /**
    * The method that supplies the class name for the application class that extends
    * <code>DataWriter</code> for this type. The string returned must be formatted
    * such that the <code>ClassLoader</code> designated during registration will be able
    * to supply the writer.
    * 
    * @return The class name for the writer of this type.
    */
   protected abstract String getWriterName();

   // TUNE should/could this method be made static ?
   /**
    * The method used to register this type. This method allows a the application to supply any
    * <code>ClassLoader</code> for the system to use to load the application specific data
    * readers and writers.
    * 
    * @param participant - The <code>DomainParticipant</code> to register the type with.
    * @param typeName - The typeName to register this type as.
    * @param classLoader - The <code>ClassLoader</code> to register for this type.
    */
   public ReturnCode registerType(DomainParticipant participant, String typeName, ClassLoader classLoader) {
      return participant.getTypeRegistry().register(
            new TypeSignature(typeName, getTypeDataSize(), getKey(), getReaderName(), getWriterName(), classLoader));
   }

   /**
    * The method used to register this type. This method registers the type with the system
    * <code>ClassLoader</code> as the loader for the reader and writer.
    * 
    * @param participant - The <code>DomainParticipant</code> to register the type with.
    * @param typeName - The typeName to register this type as.
    */
   public ReturnCode registerType(DomainParticipant participant, String typeName) {
      ClassLoader defaultClassLoader = ClassLoader.getSystemClassLoader();
      
      return registerType(participant, typeName, defaultClassLoader);
   }
}
