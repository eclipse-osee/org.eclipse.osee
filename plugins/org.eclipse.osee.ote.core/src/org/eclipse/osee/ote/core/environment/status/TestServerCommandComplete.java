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
package org.eclipse.osee.ote.core.environment.status;

import java.io.Serializable;

import org.eclipse.osee.ote.core.framework.command.TestCommandStatus;

/**
 * @author Andrew M. Finkbeiner
 */
public class TestServerCommandComplete implements Serializable, IServiceStatusData {

   private static final long serialVersionUID = -2678833105694275416L;
   private TestCommandStatus cmdStatus;
   private Throwable th;

   public TestServerCommandComplete(TestCommandStatus cmdStatus, Throwable th) {
      this.cmdStatus = cmdStatus;
      this.th = th;
   }

   @Override
   public void accept(IServiceStatusDataVisitor visitor) {
      visitor.asTestServerCommandComplete(this);
   }

   public TestCommandStatus getCmdStatus() {
      return cmdStatus;
   }
   
   public Throwable getThrowable() {
      return th;
   }
}
