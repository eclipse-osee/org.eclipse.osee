/*
 * Created on Mar 24, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.event.artifact;

import org.eclipse.osee.framework.core.data.IBasicGuidArtifact;

/**
 * @author Donald G. Dunne
 */
public interface IEventBasicGuidArtifact extends IBasicGuidArtifact {

   public EventModType getModType();
}
