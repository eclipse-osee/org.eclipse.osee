package org.eclipse.osee.ats.util;

public enum TransitionOption {
   None,
   Persist,
   // Override check whether workflow allows transition to state
   OverrideTransitionValidityCheck,
   // Allows transition to occur with UnAssigned, OseeSystem or Guest
   OverrideAssigneeCheck
};
