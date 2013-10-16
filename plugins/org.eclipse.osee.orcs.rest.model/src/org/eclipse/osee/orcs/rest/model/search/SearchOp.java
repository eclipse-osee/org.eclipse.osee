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
package org.eclipse.osee.orcs.rest.model.search;

import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author John R. Misinco
 * @author Roberto E. Escobar
 */
public enum SearchOp {
   EQUALS("=="),
   LESS_THAN("<"),
   GREATER_THAN(">");

   private final String token;

   private SearchOp(String token) {
      this.token = token;
   }

   public String getToken() {
      return token;
   }

   public static SearchOp fromString(String value) throws OseeCoreException {
      SearchOp toReturn = null;
      for (SearchOp op : SearchOp.values()) {
         if (op.getToken().equals(value)) {
            toReturn = op;
         }
      }
      Conditions.checkNotNull(toReturn, "searchOp", "Invalid op [%s]", value);
      return toReturn;
   }
}