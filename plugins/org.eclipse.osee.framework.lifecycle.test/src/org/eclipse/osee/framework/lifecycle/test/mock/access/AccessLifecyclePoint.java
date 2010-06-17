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
package org.eclipse.osee.framework.lifecycle.test.mock.access;

import org.eclipse.osee.framework.lifecycle.AbstractLifecyclePoint;

/**
 * @author Roberto E. Escobar
 * @author Jeff C. Phillips
 */
public class AccessLifecyclePoint extends AbstractLifecyclePoint<ChangeMgmtHandler> {

   public static final Type<ChangeMgmtHandler> TYPE = new Type<ChangeMgmtHandler>();

   @Override
   protected void initializeHandlerData(ChangeMgmtHandler handler) {
   }

   @Override
   public org.eclipse.osee.framework.lifecycle.AbstractLifecycleVisitor.Type<ChangeMgmtHandler> getAssociatedType() {
      return TYPE;
   }

}
