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
package org.eclipse.osee.framework.ui.skynet.blam;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;

/**
 * @author Ryan D. Brooks
 */
public class BlamVariableMap {
   private final HashMap<String, BlamVariable> variableMap;

   public BlamVariableMap() {
      super();
      variableMap = new HashMap<String, BlamVariable>();
   }

   /**
    * This method is used by the Blam engine to put all Blam variables (parameters and local) into the map and should
    * not be used directly by Blam operations
    * 
    * @param name
    * @param object
    */
   protected void addBlamVariable(String name, Object object) {
      variableMap.put(name, new BlamVariable(object));
   }

   private BlamVariable getBlamVariable(String name) throws IllegalArgumentException {
      BlamVariable variable = variableMap.get(name);
      if (variable == null) {
         throw new IllegalArgumentException("No variable existing with the name " + name);
      }
      return variable;
   }

   protected void aliasVariable(String existingName, String alias) {
      BlamVariable variable = getBlamVariable(existingName);
      variableMap.put(alias, variable);
   }

   public void setValue(String variableName, Object value) {
      BlamVariable variable = variableMap.get(variableName);
      if (variable == null) {
         addBlamVariable(variableName, value);
      } else {
         variable.setValue(value);
      }
   }

   private static class BlamVariable {
      private Object value;

      public BlamVariable(Object value) {
         this.value = value;
      }

      public Object getValue() {
         return value;
      }

      public void setValue(Object value) {
         this.value = value;
      }
   }

   public List<Artifact> getArtifacts(String parameterName) {
      Collection<Artifact> arts = getCollection(Artifact.class, parameterName);
      if (arts == null) return new ArrayList<Artifact>();
      return new ArrayList<Artifact>(arts);
   }

   public ArtifactType getArtifactSubtypeDescriptor(String parameterName) {
      return getSingleCollectionValue(ArtifactType.class, parameterName);
   }

   public AttributeType getAttributeDescriptor(String parameterName) {
      return getSingleCollectionValue(AttributeType.class, parameterName);
   }

   public String getString(String parameterName) {
      return getValue(String.class, parameterName);
   }

   public Branch getBranch(String parameterName) {
      return getValue(Branch.class, parameterName);
   }

   public boolean getBoolean(String parameterName) {
      return getValue(Boolean.class, parameterName);
   }

   @SuppressWarnings("unchecked")
   public <T> Collection<T> getCollection(Class<T> clazz, String parameterName) {
      return getValue(Collection.class, parameterName);
   }

   @SuppressWarnings("unchecked")
   public User getUser(String parameterName) {
      return getValue(User.class, parameterName);
   }

   private <T> T getSingleCollectionValue(Class<T> clazz, String parameterName) {
      Collection<T> objects = getCollection(clazz, parameterName);
      if (objects.size() != 1) {
         throw new IllegalArgumentException("Require a collection of size 1 not " + objects.size());
      }
      return objects.iterator().next();
   }

   public <T> T getValue(Class<T> clazz, String variableName) {
      Object object = getBlamVariable(variableName).getValue();

      if (object != null && !clazz.isInstance(object)) {
         throw new IllegalArgumentException(
               "Expecting object of type " + clazz.getName() + " not " + object.getClass().getName());
      }
      return clazz.cast(object);
   }
}