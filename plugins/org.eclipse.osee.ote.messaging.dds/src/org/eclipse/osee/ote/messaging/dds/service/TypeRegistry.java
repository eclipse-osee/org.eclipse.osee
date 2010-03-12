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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.osee.ote.messaging.dds.ReturnCode;

/**
 * Provides registration functionality of types that are used in the DDS system. The
 * registry relies on a hash map for optimum performance tuning which can be done via
 * the parameters to the constructor. The registry acts as the authority for types
 * by keeping track of <code>TypeSignature</code>'s.
 * 
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public class TypeRegistry {
   private Map<String, TypeSignature> typeHash;
   
   /**
    * Create a new <code>TypeRegistry</code> with particular characteristics. This is used
    * by portions of the DDS system for resolving necessary information about types during
    * run time.
    * 
    * @param initialCapacity The initial capacity to create the hash map with
    * @param loadFactor The load factor for the hash map. This specifies how full the map can get
    *                   before more memory is allocated for the map.
    */
   public TypeRegistry(int initialCapacity, float loadFactor) {
      super();
      typeHash = Collections.synchronizedMap(new HashMap<String, TypeSignature>(initialCapacity, loadFactor));
   }
   
   /**
    * Register a <code>TypeSignature</code> with the registry.
    * 
    * @see TypeSignature
    * @param signature The signature of the type to register.
    * @return OK if the type was registered. OUT_OF_RESOURCES if a memory error happened, or PRECONDITION_NOT_MET
    *         if the signature conflicts with one already registered.
    */
   public ReturnCode register(TypeSignature signature) {
      ReturnCode retCode = ReturnCode.OK;
      
      TypeSignature checkSignature = typeHash.get(signature.getTypeName());
      // If the signature was not already registered, then add it.
      if (checkSignature == null) {
         try {
            typeHash.put(signature.getTypeName(), signature);
         } catch (OutOfMemoryError er) {
            er.printStackTrace();
            retCode = ReturnCode.OUT_OF_RESOURCES;
         }
         
      // If the signature was already registered, then make sure the signature is the same
      } else {
         if (!checkSignature.equals(signature))
            retCode = ReturnCode.PRECONDITION_NOT_MET;
      }
      
      return retCode;
   }
   
   /**
    * Provides the signature for a type with a particular name.
    * 
    * @param typeName The name of the type to lookup
    * @return The appropriate signature for the name as registered with the registry, null if no
    *         such signature exists.
    */
   public TypeSignature lookupSignature(String typeName) {
      return typeHash.get(typeName);
   }
   
   public void clear() {
      typeHash.clear();
   }
}
