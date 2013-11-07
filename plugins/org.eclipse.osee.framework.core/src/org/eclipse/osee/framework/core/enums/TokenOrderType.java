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
package org.eclipse.osee.framework.core.enums;

/**
 * @author John Misinco
 */
public enum TokenOrderType implements QueryOption {
   ANY_ORDER,
   MATCH_ORDER;

   public boolean isMatchOrder() {
      return MATCH_ORDER == this;
   }

   @Override
   public void accept(OptionVisitor visitor) {
      visitor.asTokenOrderType(this);
   }

   public static TokenOrderType getTokenOrderType(boolean matchOrder) {
      return matchOrder ? MATCH_ORDER : ANY_ORDER;
   }
}
