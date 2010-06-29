/*
 * Created on Mar 23, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.event2.filter;

import org.eclipse.osee.framework.core.model.event.IBasicGuidArtifact;
import org.eclipse.osee.framework.core.model.event.IBasicGuidRelation;

/**
 * @author Donald G. Dunne
 */
public interface IEventFilter {

   /**
    * return true if events for this this guid artifact should be passed through to listeners
    */
   public boolean isMatch(IBasicGuidArtifact guidArt);

   /**
    * return true if events for this guid relation should be passed through to listeners
    */
   public boolean isMatch(IBasicGuidRelation relArt);

}
