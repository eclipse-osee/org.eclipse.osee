/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.notify;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public interface IAtsNotificationService {

   public void addNotificationEvent(AtsNotificationEvent notificationEvent) throws OseeCoreException;

   public void clear();

   public List<AtsNotificationEvent> getNotificationEvents() throws OseeCoreException;

   public void sendNotifications();

   public void notify(IAtsWorkItem workItem, Collection<? extends IAtsUser> notifyUsers, AtsNotifyType... notifyTypes) throws OseeCoreException;

}
