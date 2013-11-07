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
package org.eclipse.osee.framework.core.enums;

/**
 * @author Ryan D. Brooks
 * @author Roberto E. Escobar
 */
public enum CaseType implements QueryOption {
   MATCH_CASE,
   IGNORE_CASE;

   public boolean isCaseSensitive() {
      return MATCH_CASE == this;
   }

   @Override
   public void accept(OptionVisitor visitor) {
      visitor.asCaseType(this);
   }

   public static CaseType getCaseType(boolean isCaseSensitive) {
      return isCaseSensitive ? MATCH_CASE : IGNORE_CASE;
   }
}
