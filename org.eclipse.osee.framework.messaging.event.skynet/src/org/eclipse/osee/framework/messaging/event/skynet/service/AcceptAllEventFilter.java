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
package org.eclipse.osee.framework.messaging.event.skynet.service;

import org.eclipse.osee.framework.messaging.event.skynet.ISkynetEvent;
import org.eclipse.osee.framework.messaging.event.skynet.filter.IEventFilter;

/**
 * @author Roberto E. Escobar
 */
public class AcceptAllEventFilter implements IEventFilter {

   private static final long serialVersionUID = -7624321113355047868L;

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.messaging.event.skynet.filter.IEventFilter#accepts(org.eclipse.osee.framework.messaging.event.skynet.ISkynetEvent)
    */
   @Override
   public boolean accepts(ISkynetEvent event) {
      return true;
   }

}
