package org.eclipse.osee.ats.core.client.notify;

public enum AtsNotifyType {
   Subscribed, // Workflow transitioned, notify subscribers
   Cancelled, // Workflow completed, notify Originator
   Completed, // Workflow completed, notify Originator
   Assigned, // New assignee added, notify assignee
   Originator, // Originator Changed, notify new originator
   Peer_Reviewers_Completed, // Review has been completed , notify authors and moderators
};
