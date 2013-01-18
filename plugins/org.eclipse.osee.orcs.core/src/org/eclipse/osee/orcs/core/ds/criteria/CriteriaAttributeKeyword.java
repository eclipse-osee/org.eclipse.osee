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

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.QueryOptions;

/**
 * @author Roberto E. Escobar
 */
public class CriteriaAttributeKeyword extends Criteria<QueryOptions> {

   private final Collection<? extends IAttributeType> attributeType;
   private final String value;
   private final QueryOption[] options;

   public CriteriaAttributeKeyword(Collection<? extends IAttributeType> attributeType, String value, QueryOption... options) {
      super();
      this.attributeType = attributeType;
      this.value = value;
      this.options = options;
   }

   public Collection<? extends IAttributeType> getTypes() {
      return attributeType;
   }

   public String getValue() {
      return value;
   }

   public QueryOption[] getOptions() {
      return options;
   }

   @Override
   public void checkValid(QueryOptions options) throws OseeCoreException {
      super.checkValid(options);
      Conditions.checkNotNullOrEmpty(getValue(), "search value");
      Conditions.checkNotNullOrEmpty(getTypes(), "attribute types");
   }

   @Override
   public String toString() {
      return String.format("CriteriaAttributeKeyword [attributeType=%s, value=%s, options=%s]", attributeType, value,
         Collections.toString(",", Arrays.asList(options)));
   }

}
