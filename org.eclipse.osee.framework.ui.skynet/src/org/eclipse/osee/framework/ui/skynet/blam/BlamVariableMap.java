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
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.attribute.DynamicAttributeDescriptor;

/**
 * @author Ryan D. Brooks
 */
public class BlamVariableMap {
   public static enum BlamVariableType {
      STRING, INTEGER, REAL, ARTIFACT
   };

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

   public Object getValue(String variableName) {
      return getBlamVariable(variableName).getValue();
   }

   public void setValue(String variableName, Object value) {
      try {
         getBlamVariable(variableName).setValue(value);
      } catch (Exception e) {
         addBlamVariable(variableName, value);
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
      Collection<Object> objects = getCollection(parameterName);

      List<Artifact> artifacts = new ArrayList<Artifact>(objects.size());
      for (Object object : objects) {
         artifacts.add((Artifact) object);
      }
      return artifacts;
   }

   public ArtifactSubtypeDescriptor getArtifactSubtypeDescriptor(String parameterName) {
      Object object = getSingleCollectionValue(parameterName);

      if (!(object instanceof ArtifactSubtypeDescriptor)) {
         throw new IllegalArgumentException(
               "Expecting object of type ArtifactSubtypeDescriptor not " + object.getClass().getName());
      }

      return (ArtifactSubtypeDescriptor) object;
   }

   public DynamicAttributeDescriptor getAttributeDescriptor(String parameterName) {
      Object object = getSingleCollectionValue(parameterName);

      if (!(object instanceof DynamicAttributeDescriptor)) {
         throw new IllegalArgumentException(
               "Expecting object of type DynamicAttributeDescriptor not " + object.getClass().getName());
      }

      return (DynamicAttributeDescriptor) object;
   }

   public String getString(String parameterName) {
      Object object = getValue(parameterName);

      if (object == null) {
         throw new IllegalArgumentException("Parameter can not be null");
      }
      if (!(object instanceof String)) {
         throw new IllegalArgumentException("Expecting object of type String not " + object.getClass().getName());
      }
      return (String) object;
   }

   @SuppressWarnings("unchecked")
   private Collection<Object> getCollection(String parameterName) {
      Object object = getValue(parameterName);

      if (object == null) {
         throw new IllegalArgumentException("Parameter can not be null");
      }
      if (!(object instanceof Collection)) {
         throw new IllegalArgumentException("Expecting object of type Collection not " + object.getClass().getName());
      }
      return (Collection<Object>) object;
   }

   private Object getSingleCollectionValue(String parameterName) {
      Collection<Object> objects = getCollection(parameterName);
      if (objects.size() != 1) {
         throw new IllegalArgumentException("Require a collection of size 1 not " + objects.size());
      }
      return objects.iterator().next();
   }
}
