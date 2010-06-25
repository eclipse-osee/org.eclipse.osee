/*
 * Created on Jun 24, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.lifecycle.test.mock.access;

import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.lifecycle.access.IAccessCheckProvider;

public class MockAccessCheckPorovider implements IAccessCheckProvider {

   @Override
   public boolean canEdit(IBasicArtifact<?> user, IBasicArtifact<?> artTcheck) {
      return false;
   }

}
