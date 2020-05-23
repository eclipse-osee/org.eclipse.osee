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

package org.eclipse.osee.ats.ide.workflow.review;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.framework.core.operation.AbstractOperation;

/**
 * @author Donald G. Dunne
 */
public class ReviewNavigateViewItemsOperation extends AbstractOperation {

   public ReviewNavigateViewItemsOperation() {
      super("Loading Review Navigate View Items", Activator.PLUGIN_ID);
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      ReviewNavigateViewItems.getInstance().getSearchNavigateItems();
   }
}
