/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.framework.ui.skynet.change.operations;

import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.revision.ChangeDataLoader;
import org.eclipse.osee.framework.ui.skynet.change.ChangeUiData;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

public class LoadChangesOperation extends AbstractOperation {
   private final ChangeUiData changeData;

   public LoadChangesOperation(ChangeUiData changeData) {
      super("Load Change Data", Activator.PLUGIN_ID);
      this.changeData = changeData;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      changeData.setIsLoaded(false);
      Collection<Change> changes = changeData.getChanges();
      changes.clear();
      monitor.worked(calculateWork(0.10));

      IOperation subOp = new ChangeDataLoader(changes, changeData.getTxDelta());
      doSubWork(subOp, monitor, 0.80);

      changeData.setIsLoaded(true);
      monitor.worked(calculateWork(0.10));
   }
};