/*********************************************************************
 * Copyright (c) 2013 Boeing
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
