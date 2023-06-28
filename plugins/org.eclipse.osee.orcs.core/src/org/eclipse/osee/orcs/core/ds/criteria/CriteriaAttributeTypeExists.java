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

package org.eclipse.osee.orcs.core.ds.criteria;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.Options;

/**
 * @author Roberto E. Escobar
 */
public class CriteriaAttributeTypeExists extends Criteria {

   private final Collection<AttributeTypeToken> attributeTypes;

   public CriteriaAttributeTypeExists(Collection<AttributeTypeToken> attributeTypes) {
      this.attributeTypes = attributeTypes;
   }

   public Collection<AttributeTypeToken> getTypes() {
      return attributeTypes;
   }

   @Override
   public void checkValid(Options options) {
      Conditions.checkNotNullOrEmpty(getTypes(), "attribute types");
   }

   @Override
   public String toString() {
      return "CriteriaAttributeTypeExists [attributeTypes=" + attributeTypes + "]";
   }
}
