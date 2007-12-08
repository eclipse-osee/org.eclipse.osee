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
package org.eclipse.osee.framework.messaging.event.skynet.filter;

import java.io.Serializable;
import org.eclipse.osee.framework.messaging.event.skynet.ISkynetEvent;

/**
 * @author Robert A. Fisher
 */
public interface IEventFilter extends Serializable {

   /**
    * Determine if the event meets the requirement of this filter.
    * 
    * @param event The event to inspect.
    * @return boolean
    */
   public boolean accepts(ISkynetEvent event);
}
