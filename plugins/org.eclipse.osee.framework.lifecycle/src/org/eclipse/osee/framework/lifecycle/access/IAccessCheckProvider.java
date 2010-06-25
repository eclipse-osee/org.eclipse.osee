/*
 * Created on Jun 24, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.lifecycle.access;

import org.eclipse.osee.framework.core.model.IBasicArtifact;

public interface IAccessCheckProvider {
   public boolean canEdit(IBasicArtifact<?> user, IBasicArtifact<?> artTcheck);

}
