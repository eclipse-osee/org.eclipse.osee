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
package org.eclipse.osee.framework.skynet.core.commit;

import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Roberto E. Escobar
 */
public class ComputeNetChangeOperation extends AbstractOperation {

   private final Collection<OseeChange> rawChanges;
   private final IChangeResolver resolver;

   public ComputeNetChangeOperation(String name, Collection<OseeChange> rawChanges, IChangeResolver resolver) {
      super(name, Activator.PLUGIN_ID);
      this.rawChanges = rawChanges;
      this.resolver = resolver;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      if (!rawChanges.isEmpty()) {
         resolver.reset();
         double workPercentage = 1.0 / rawChanges.size();
         for (OseeChange oseeChange : rawChanges) {
            checkForCancelledStatus(monitor);
            oseeChange.accept(resolver);
            monitor.worked(calculateWork(workPercentage));
         }
         resolver.resolve();
      }
   }
}
