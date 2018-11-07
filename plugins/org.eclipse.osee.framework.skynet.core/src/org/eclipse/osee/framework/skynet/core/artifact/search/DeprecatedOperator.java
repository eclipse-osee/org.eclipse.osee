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
