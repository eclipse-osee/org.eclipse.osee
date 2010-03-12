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
package org.eclipse.osee.ats.notify;

import java.util.Collection;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.notify.OseeNotificationEvent;

/**
 * @author Donald G. Dunne
 */
public interface IAtsNotification {

   /**
    * Descriptive name that admin can identify the group/notification that will get sent
    * 
    * @return name
    */
   public String getNotificationName();

   public Collection<OseeNotificationEvent> getNotificationEvents() throws OseeCoreException;

}
