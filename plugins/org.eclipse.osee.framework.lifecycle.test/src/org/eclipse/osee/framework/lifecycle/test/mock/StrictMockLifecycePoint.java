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

package org.eclipse.osee.framework.lifecycle.test.mock;

import org.eclipse.osee.framework.lifecycle.AbstractLifecyclePoint;

/**
 * @author Roberto E. Escobar
 * @author Jeff C. Phillips
 */
public class StrictMockLifecycePoint extends AbstractLifecyclePoint<MockHandler> {

   public static final Type<MockHandler> TYPE = new Type<>();

   private final String a;
   private final String b;

   public StrictMockLifecycePoint(String a, String b) {
      super();
      this.a = a;
      this.b = b;
   }

   @Override
   public Type<MockHandler> getAssociatedType() {
      return TYPE;
   }

   @Override
   protected void initializeHandlerData(MockHandler handler) {
      handler.setData(a, b);
   }
}
