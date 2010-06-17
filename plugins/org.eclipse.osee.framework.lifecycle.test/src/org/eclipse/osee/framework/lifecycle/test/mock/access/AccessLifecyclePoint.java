/*
 * Created on Jun 17, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.lifecycle.test.mock.access;

import org.eclipse.osee.framework.lifecycle.AbstractLifecyclePoint;

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
/**
 * @author Roberto E. Escobar
 * @author Jeff C. Phillips
 */
public class AccessLifecyclePoint extends AbstractLifecyclePoint<AccessHandler> {

   public static final Type<AccessHandler> TYPE = new Type<AccessHandler>();

   @Override
   protected void initializeHandlerData(AccessHandler handler) {
   }

   @Override
   public org.eclipse.osee.framework.lifecycle.AbstractLifecycleVisitor.Type<AccessHandler> getAssociatedType() {
      return TYPE;
   }

}
