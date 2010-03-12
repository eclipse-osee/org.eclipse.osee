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
package org.eclipse.osee.framework.skynet.core.access;

public class NoPoliciesException extends SecurityException {

   private static final long serialVersionUID = 7021179561030107435L;

   public NoPoliciesException() {
      super("NoPoliciesException");
   }

   public NoPoliciesException(String fname) {
      super(fname + " has no policies.");
   }
}
