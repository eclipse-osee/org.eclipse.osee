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

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.Options;

/**
 * @author John Misinco
 */
public class CriteriaAttributeTypeNotExists extends Criteria {

   private final Collection<AttributeTypeToken> attributeTypes;
   private final String value;

   public CriteriaAttributeTypeNotExists(Collection<AttributeTypeToken> attributeTypes) {
      this.attributeTypes = attributeTypes;
      value = null;
   }

   public CriteriaAttributeTypeNotExists(AttributeTypeToken attributeType) {
      this(attributeType, null);
   }

   public CriteriaAttributeTypeNotExists(AttributeTypeToken attributeType, String value) {
      this.attributeTypes = Arrays.asList(attributeType);
      this.value = value;
   }

   public Collection<AttributeTypeToken> getTypes() {
      return attributeTypes;
   }

   public String getValue() {
      return value;
   }

   @Override
   public void checkValid(Options options) {
      Conditions.checkNotNull(getTypes(), "attribute types");
   }

   @Override
   public String toString() {
      return "CriteriaAttributeTypeNotExists [attributeType=" + attributeTypes + "]";
   }
}
