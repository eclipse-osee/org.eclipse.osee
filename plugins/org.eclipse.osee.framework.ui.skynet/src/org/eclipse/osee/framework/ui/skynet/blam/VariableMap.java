/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.blam;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Ryan D. Brooks
 */
public class VariableMap {
   private final Map<String, Object> variableMap = new HashMap<>();

   public VariableMap() {
      // provides a constructor that does not throw OseeArgumentException
   }

   public VariableMap(Object... optionArgs) {
      setValues(optionArgs);
   }

   public void setValues(Object... optionArgs) {
      for (int i = 0; i < optionArgs.length; i += 2) {
         Object object = optionArgs[i];
         if (object instanceof String) {
            variableMap.put((String) object, optionArgs[i + 1]);
         } else if (object == null) {
            throw new OseeArgumentException("The [%d]th option must not be null", i);
         } else {
            throw new OseeArgumentException("The [%d]th option must be of type string but is of type [%s]", i,
               object.getClass().getName());
         }
      }
   }

   public Object[] getValues() {
      Object[] values = new Object[variableMap.size() * 2];
      int index = 0;
      for (Entry<String, Object> entry : variableMap.entrySet()) {
         values[index++] = entry.getKey();
         values[index++] = entry.getValue();
      }
      return values;
   }

   public void setValue(String variableName, Object value) {
      variableMap.put(variableName, value);
   }

   public ArtifactTypeToken getArtifactType(String parameterName) {
      return getSingleCollectionValue(ArtifactTypeToken.class, parameterName);
   }

   public List<ArtifactTypeToken> getArtifactTypes(String parameterName) {
      Collection<ArtifactTypeToken> artTypes = getCollection(ArtifactTypeToken.class, parameterName);
      if (artTypes == null) {
         return new ArrayList<>();
      }
      return new ArrayList<>(artTypes);
   }

   public Artifact getArtifact(String parameterName) {
      Object object = variableMap.get(parameterName);
      if (object instanceof Artifact) {
         return (Artifact) object;
      }
      return getSingleCollectionValue(Artifact.class, parameterName);
   }

   public AttributeTypeToken getAttributeType(String parameterName) {
      return getSingleCollectionValue(AttributeTypeToken.class, parameterName);
   }

   public List<AttributeTypeToken> getAttributeTypes(String parameterName) {
      Collection<AttributeTypeToken> attrTypes = getCollection(AttributeTypeToken.class, parameterName);
      if (attrTypes == null) {
         return new ArrayList<>();
      }
      return new ArrayList<>(attrTypes);
   }

   public String getString(String parameterName) {
      return getValue(String.class, parameterName);
   }

   public BranchToken getBranch(String parameterName) {
      return getValue(BranchToken.class, parameterName);
   }

   public boolean getBoolean(String parameterName) {
      Boolean value = getValue(Boolean.class, parameterName);
      return value != null ? value : false;
   }

   @SuppressWarnings("unchecked")
   public <T> Collection<T> getCollection(Class<T> clazz, String parameterName) {
      List<T> results = new ArrayList<>();
      Collection<T> collection = getValue(Collection.class, parameterName);

      if (collection != null) {
         for (Object obj : collection) {
            if (clazz.isInstance(obj)) {
               results.add((T) obj);
            }
         }
      }
      return results;
   }

   public User getUser(String parameterName) {
      return getValue(User.class, parameterName);
   }

   public List<Artifact> getArtifacts(String parameterName) {
      Collection<Artifact> artiafcts = getCollection(Artifact.class, parameterName);
      if (artiafcts == null) {
         return new ArrayList<>();
      }
      return new ArrayList<>(artiafcts);
   }

   private <T> T getSingleCollectionValue(Class<T> clazz, String parameterName) {
      Collection<T> objects = getCollection(clazz, parameterName);
      if (objects.size() != 1) {
         throw new OseeArgumentException("Require a collection of size 1 not %d", objects.size());
      }
      return objects.iterator().next();
   }

   private <T> T getValue(Class<T> clazz, String variableName) {
      Object value = variableMap.get(variableName);

      if (value != null && !clazz.isInstance(value)) {
         throw new OseeArgumentException("Expecting object of type [%s] not [%s]", clazz, value.getClass());
      }
      return clazz.cast(value);
   }

   public Object getValue(String variableName) {
      return variableMap.get(variableName);
   }

   public String getRadioSelection(String variableName) {
      return getSingleCollectionValue(String.class, variableName);
   }

   @Override
   public String toString() {
      return variableMap.toString();
   }
}