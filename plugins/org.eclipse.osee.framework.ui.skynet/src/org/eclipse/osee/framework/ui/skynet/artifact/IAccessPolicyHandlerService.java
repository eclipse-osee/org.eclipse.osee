/*
 * Created on Aug 9, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.artifact;

import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.core.model.RelationTypeSide;
import org.eclipse.osee.framework.core.model.access.PermissionStatus;
import org.eclipse.osee.framework.core.model.type.ArtifactType;

public interface IAccessPolicyHandlerService {
   PermissionStatus hasAttributeTypePermission(Collection<? extends IBasicArtifact<?>> artifacts, IAttributeType attributeType, PermissionEnum permission, Level level) throws OseeCoreException;

   PermissionStatus hasRelationSidePermission(Collection<RelationTypeSide> relationTypeSides, PermissionEnum permission, Level level) throws OseeCoreException;

   PermissionStatus hasArtifactTypePermission(IOseeBranch branch, Collection<ArtifactType> artifactTypes, PermissionEnum permission, Level level) throws OseeCoreException;
}
