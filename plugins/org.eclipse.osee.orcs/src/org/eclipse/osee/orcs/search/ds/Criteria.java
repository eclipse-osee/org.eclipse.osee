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

package org.eclipse.osee.orcs.search.ds;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.eclipse.osee.orcs.search.ds.criteria.CriteriaAttributeKeywords;

/**
 * @author Roberto E. Escobar
 * @author Donald G. Dunne
 */
@JsonTypeInfo( //
   use = JsonTypeInfo.Id.NAME, // embed a type name
   include = JsonTypeInfo.As.PROPERTY, // as a property in JSON
   property = "type" // property name to carry the type
)
@JsonSubTypes({ //
   @JsonSubTypes.Type(value = CriteriaAttributeKeywords.class, name = "CriteriaAttributeKeywords"), //
})
public class Criteria {

   public void checkValid(Options options) {
      // For subclasses to implement
   }

   public boolean isReferenceHandler() {
      return false;
   }

   public String getName() {
      return getClass().getSimpleName();
   }

   @Override
   public String toString() {
      return getClass().getSimpleName();
   }
}
