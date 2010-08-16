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
package org.eclipse.osee.framework.skynet.core.event;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.event2.BranchEvent;

/**
 * @author Donald G. Dunne
 */
public interface IBranchEventListener extends IEventFilteredListener {
   // TODO Remove this after REM2 release - legacy branch event call
   // REM1 event handler
   public void handleBranchEventREM1(Sender sender, BranchEventType branchModType, int branchId) throws OseeCoreException;

   // REM2 event handler
   public void handleBranchEvent(Sender sender, BranchEvent branchEvent);

}
