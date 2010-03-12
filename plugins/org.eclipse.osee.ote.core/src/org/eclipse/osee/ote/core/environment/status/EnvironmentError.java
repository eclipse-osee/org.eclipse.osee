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

public class EnvironmentError implements IServiceStatusData, Serializable {

   private static final long serialVersionUID = -7077313410529981519L;
   private Throwable err;

   public EnvironmentError(Throwable err) {
      this.err = err;
   }

   public EnvironmentError() {
   }

   public Throwable getErr() {
      return err;
   }

   public void set(Throwable err) {
      this.err = err;
   }

   public void accept(IServiceStatusDataVisitor visitor) {
      if (visitor != null) {
         visitor.asEnvironmentError(this);
      }
   }
}
