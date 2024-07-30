/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.xviewer.util;

/**
 * @author Donald G. Dunne
 */
public class Pair<X, Y> {
   private X first;
   private Y second;

   public Pair(X first, Y second) {
      this.first = first;
      this.second = second;
   }

   public Y getSecond() {
      return second;
   }

   public void setSecond(Y second) {
      this.second = second;
   }

   public X getFirst() {
      return first;
   }

   public void setFirst(X first) {
      this.first = first;
   }

}