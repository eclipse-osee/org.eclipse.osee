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
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.search.CaseType;
import org.eclipse.osee.orcs.search.StringOperator;

/**
 * @author Roberto E. Escobar
 */
public class CriteriaAttributeKeyword extends Criteria {

   private final IAttributeType attributeType;
   private final Collection<String> values;
   private final StringOperator stringOp;
   private final CaseType match;

   public CriteriaAttributeKeyword(IAttributeType attributeType, Collection<String> values, StringOperator stringOp, CaseType match) {
      super();
      this.attributeType = attributeType;
      this.values = values;
      this.stringOp = stringOp;
      this.match = match;
   }

   public IAttributeType getAttributeType() {
      return attributeType;
   }

   public Collection<String> getValues() {
      return values;
   }

   public StringOperator getStringOp() {
      return stringOp;
   }

   public CaseType getMatch() {
      return match;
   }

}
