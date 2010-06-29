/*
 * Created on Jun 29, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.services;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.AccessData;
import org.eclipse.osee.framework.core.model.IBasicArtifact;

public interface IAccessControlService {

   boolean hasPermission(Object object, PermissionEnum permission) throws OseeCoreException;

   void removePermissions(IOseeBranch branch) throws OseeCoreException;

   AccessData getAccessData(IBasicArtifact<?> userArtifact, Collection<IBasicArtifact<?>> itemsToCheck) throws OseeCoreException;
}
