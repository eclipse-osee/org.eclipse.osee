/*********************************************************************
 * Copyright (c) 2020 Robert Bosch Engineering and Business Solutions Ltd India
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Robert Bosch Engineering and Business Solutions Ltd India - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.icteam.web.mail.notifier;

public enum ICTeamNotifyType {
   Subscribed, // Workflow transitioned, notify subscribers
   Cancelled, // Workflow completed, notify Originator
   Completed, // Workflow completed, notify Originator
   Assigned, // New assignee added, notify assignee
   Originator, // Originator Changed, notify new originator
   Peer_Reviewers_Completed, // Review has been completed , notify authors and moderators
   Updated, // Changes done to task
   Created, // New Task Created
   predecessor, // Mail for successor task after completion of predecessor task
   UserCreated, // user created
   AdminUserCreated, // admin user created
   PasswordChangeRequest// password change request
};
