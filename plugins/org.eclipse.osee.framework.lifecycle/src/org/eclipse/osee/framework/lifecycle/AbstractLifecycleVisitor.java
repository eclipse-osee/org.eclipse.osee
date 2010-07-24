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
package org.eclipse.osee.framework.lifecycle;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

/**
 * @author Roberto E. Escobar
 * @author Jeff C. Phillips
 */
public abstract class AbstractLifecycleVisitor<H extends LifecycleHandler> {

   public static class Type<H> {
      private static int nextHashCode;
      private final int index;

      public Type() {
         index = ++nextHashCode;
      }

      @Override
      public final int hashCode() {
         return index;
      }

      @Override
      public String toString() {
         return "Handler Type";
      }
   }

   protected AbstractLifecycleVisitor() {
   }

   public abstract Type<H> getAssociatedType();

   /**
    * Should only be called by {@link ILifecycleService}.
    */
   protected abstract IStatus dispatch(IProgressMonitor monitor, H handler, String sourceId);

}
