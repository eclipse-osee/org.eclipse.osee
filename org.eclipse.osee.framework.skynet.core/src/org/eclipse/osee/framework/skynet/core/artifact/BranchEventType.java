/*
 * Created on Sep 16, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.artifact;

/**
 * @author Donald G. Dunne
 */
public enum BranchEventType {

   // Local and Remote events
   Deleted,
   Added,
   Renamed,
   Committed,

   // Local event only; Does not get sent Remote
   DefaultBranchChanged;
}
