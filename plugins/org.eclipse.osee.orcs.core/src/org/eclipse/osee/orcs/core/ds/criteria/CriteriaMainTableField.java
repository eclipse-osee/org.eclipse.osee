/*******************************************************************************
 * Copyright (c) 2013 Boeing.
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
import java.util.Collections;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.Options;

/**
 * @author Ryan D. Brooks
 */
public class CriteriaMainTableField extends Criteria {
   private final Collection<? extends Id> values;

   public CriteriaMainTableField(Collection<? extends Id> values) {
      this.values = values;
   }

   public CriteriaMainTableField(Id value) {
      this.values = Collections.singletonList(value);
   }

   public Collection<? extends Id> getValues() {
      return values;
   }

   @Override
   public void checkValid(Options options) {
      Conditions.checkNotNullOrEmpty(values, "main table field values");
   }

   @Override
   public String toString() {
      return getClass().getSimpleName() + " " + values;
   }
}