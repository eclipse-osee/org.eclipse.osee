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

package org.eclipse.osee.framework.skynet.core.artifact.search;

@Deprecated
/**
 * @author Andrew M. Finkbeiner
 */
public enum DeprecatedOperator {
   EQUAL("="),
   NOT_EQUAL("<>"),
   LIKE(" like "),
   CONTAINS(" like "),
   IS(" is ");

   private String expression;

   private DeprecatedOperator(String expression) {
      this.expression = expression;
   }

   @Override
   public String toString() {
      return expression;
   }
}
