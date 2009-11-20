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
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.core.model.AttributeType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Ryan D. Brooks
 */
public class VariableMap {
   private final HashMap<String, Object> variableMap = new HashMap<String, Object>();

   public VariableMap() {
   }

   /**
    * @throws OseeArgumentException
    */
   public VariableMap(Object... optionArgs) throws OseeArgumentException {
      for (int i = 0; i < optionArgs.length; i += 2) {
         if (optionArgs[i] instanceof String) {
            variableMap.put((String) optionArgs[i], optionArgs[i + 1]);
         } else if (optionArgs[i] == null) {
            throw new OseeArgumentException(String.format("The %dth option must not be null", i));
         } else {
            throw new OseeArgumentException(String.format("The %dth option must be of type string but is of type %s",
                  i, optionArgs[i].getClass().getName()));
         }
      }
   }

   public void setValue(String variableName, Object value) {
      variableMap.put(variableName, value);
   }

   public ArtifactType getArtifactType(String parameterName) throws OseeArgumentException {
      return getSingleCollectionValue(ArtifactType.class, parameterName);
   }

   public AttributeType getAttributeType(String parameterName) throws OseeArgumentException {
      return getSingleCollectionValue(AttributeType.class, parameterName);
   }

   public String getString(String parameterName) throws OseeArgumentException {
      return getValue(String.class, parameterName);
   }

   public Branch getBranch(String parameterName) throws OseeArgumentException {
      return getValue(Branch.class, parameterName);
   }

   public Boolean getBoolean(String parameterName) throws OseeArgumentException {
      return getValue(Boolean.class, parameterName);
   }

   @SuppressWarnings("unchecked")
   public <T> Collection<T> getCollection(Class<T> clazz, String parameterName) throws OseeArgumentException {
      return getValue(Collection.class, parameterName);
   }

   public User getUser(String parameterName) throws OseeArgumentException {
      return getValue(User.class, parameterName);
   }

   public List<Artifact> getArtifacts(String parameterName) throws OseeArgumentException {
      Collection<Artifact> artiafcts = getCollection(Artifact.class, parameterName);
      if (artiafcts == null) {
         return new ArrayList<Artifact>();
      }
      return new ArrayList<Artifact>(artiafcts);
   }

   private <T> T getSingleCollectionValue(Class<T> clazz, String parameterName) throws OseeArgumentException {
      Collection<T> objects = getCollection(clazz, parameterName);
      if (objects.size() != 1) {
         throw new OseeArgumentException("Require a collection of size 1 not " + objects.size());
      }
      return objects.iterator().next();
   }

   private <T> T getValue(Class<T> clazz, String variableName) throws OseeArgumentException {
      Object value = variableMap.get(variableName);

      if (value != null && !clazz.isInstance(value)) {
         throw new OseeArgumentException(
               "Expecting object of type " + clazz.getName() + " not " + value.getClass().getName());
      }
      return clazz.cast(value);
   }

   public Object getValue(String variableName) throws OseeArgumentException {
      return variableMap.get(variableName);
   }
}