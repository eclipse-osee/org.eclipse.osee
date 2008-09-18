/*
 * Created on Sep 17, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.artifact;

/**
 * @author Donald G. Dunne
 */
public enum ArtifactModType {

   Deleted, Added, Changed,

   // Dirtied artifact was reloaded to last saved state (not propagated remotely)
   // This is a Local modification type only and will not get transmitted as part of the transaction event
   Reverted

}
