/*
 * Created on Jun 24, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.access.test.internal;

import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.lifecycle.IAccessCheckProvider;

public class MockAccessCheckProvider implements IAccessCheckProvider {

   @Override
   public boolean canEdit(IBasicArtifact<?> user, IBasicArtifact<?> artTcheck) {
      return false;
   }

}
