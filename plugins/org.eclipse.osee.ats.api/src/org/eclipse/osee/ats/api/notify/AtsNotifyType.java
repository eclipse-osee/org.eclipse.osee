/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.notify;

/**
 * @author Donald G. Dunne
 */
public enum AtsNotifyType {
   Subscribed, // Workflow transitioned, notify subscribers
   Cancelled, // Workflow completed, notify Originator
   Completed, // Workflow completed, notify Originator
   Assigned, // New assignee added, notify assignee
   Originator, // Originator Changed, notify new originator
   Peer_Reviewers_Completed, // Review has been completed , notify authors and moderators
   SubscribedTeamOrAi // Subscribed to get email of action written against team or ai
};
