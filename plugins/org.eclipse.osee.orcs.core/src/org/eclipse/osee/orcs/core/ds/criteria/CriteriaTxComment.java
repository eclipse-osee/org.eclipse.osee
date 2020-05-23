/*********************************************************************
 * Copyright (c) 2013 Boeing
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

import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.Options;

/**
 * @author Roberto E. Escobar
 */
public class CriteriaTxComment extends Criteria implements TxCriteria {

   private final boolean isPattern;
   private final String value;

   public CriteriaTxComment(String value, boolean isPattern) {
      super();
      this.value = value;
      this.isPattern = isPattern;
   }

   public boolean isPattern() {
      return isPattern;
   }

   public String getValue() {
      return value;
   }

   @Override
   public void checkValid(Options options) {
      Conditions.checkNotNullOrEmpty(getValue(), "comment value");
   }

   @Override
   public String toString() {
      return "CriteriaTxComment [isPattern=" + isPattern + ", value=" + value + "]";
   }

}
