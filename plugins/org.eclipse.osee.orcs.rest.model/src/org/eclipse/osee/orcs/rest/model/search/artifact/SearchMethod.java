/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.model.search.artifact;

import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author John R. Misinco
 * @author Roberto E. Escobar
 */
public enum SearchMethod {
   IDS("ids"),
   GUIDS("guids"),
   IS_OF_TYPE("isOfType"),
   TYPE_EQUALS("typeEquals"),
   EXISTS_TYPE("exists"),
   NOT_EXISTS_TYPE("notExists"),
   ATTRIBUTE_TYPE("attrType"),
   RELATED_TO("related");

   private final String token;

   private SearchMethod(String token) {
      this.token = token;
   }

   public String getToken() {
      return token;
   }

   public static SearchMethod fromString(String value)  {
      SearchMethod toReturn = null;
      for (SearchMethod op : SearchMethod.values()) {
         if (op.getToken().equals(value)) {
            toReturn = op;
         }
      }
      Conditions.checkNotNull(toReturn, "SearchMethod", "Invalid type [%s]", value);
      return toReturn;
   }

   public boolean isOfType(SearchMethod... methods) {
      for (SearchMethod method : methods) {
         if (this == method) {
            return true;
         }
      }
      return false;
   }
}