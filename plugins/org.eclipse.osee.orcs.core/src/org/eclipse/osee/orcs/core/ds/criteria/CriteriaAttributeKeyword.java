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
package org.eclipse.osee.orcs.core.ds.criteria;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.QueryOptions;
import org.eclipse.osee.orcs.search.CaseType;
import org.eclipse.osee.orcs.search.StringOperator;

/**
 * @author Roberto E. Escobar
 */
public class CriteriaAttributeKeyword extends Criteria {

   private final Collection<? extends IAttributeType> attributeType;
   private final String value;
   private final StringOperator stringOp;
   private final CaseType match;

   public CriteriaAttributeKeyword(Collection<? extends IAttributeType> attributeType, String value, StringOperator stringOp, CaseType match) {
      super();
      this.attributeType = attributeType;
      this.value = value;
      this.stringOp = stringOp;
      this.match = match;
   }

   public Collection<? extends IAttributeType> getTypes() {
      return attributeType;
   }

   public String getValue() {
      return value;
   }

   public StringOperator getStringOp() {
      return stringOp;
   }

   public CaseType getMatch() {
      return match;
   }

   @Override
   public void checkValid(QueryOptions options) throws OseeCoreException {
      super.checkValid(options);
      Conditions.checkNotNullOrEmpty(getValue(), "search value");
   }

   @Override
   public String toString() {
      return "CriteriaAttributeKeyword [attributeType=" + attributeType + ", value=" + value + ", stringOp=" + stringOp + ", match=" + match + "]";
   }

}
