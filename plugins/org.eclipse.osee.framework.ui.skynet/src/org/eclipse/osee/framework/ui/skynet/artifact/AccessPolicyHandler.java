/*
 * Created on Aug 3, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.artifact;

import java.util.Collection;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.core.model.access.AccessDataQuery;
import org.eclipse.osee.framework.core.model.access.PermissionStatus;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.core.services.IAccessControlService;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;

/**
 * @author Jeff C. Phillips
 */
public final class AccessPolicyHandler {
   private final IBasicArtifact<?> user;
   private final IAccessControlService accessControlService;
   private final Collection<? extends IBasicArtifact<?>> artifacts;

   public AccessPolicyHandler(IBasicArtifact<?> user, IAccessControlService accessControlService, Collection<? extends IBasicArtifact<?>> artifacts) {
      this.user = user;
      this.accessControlService = accessControlService;
      this.artifacts = artifacts;
   }

   public PermissionStatus hasAttributeTypePermission(AttributeType attributeType, PermissionEnum permission, boolean displayMessage) throws OseeCoreException {
      AccessDataQuery query = accessControlService.getAccessData(user, artifacts);
      PermissionStatus permissionStatus = new PermissionStatus();

      if (true) {
         return permissionStatus;
      }
      if (artifacts != null) {
         for (IBasicArtifact<?> artifact : artifacts) {
            query.attributeTypeMatches(PermissionEnum.WRITE, artifact, attributeType, permissionStatus);

            if (!permissionStatus.matched()) {
               if (displayMessage) {
                  AWorkbench.popup(
                     "No Permission Error",
                     "Artifacts: " + Collections.toString(artifacts, ",") + " does not have permissions becuase: " + permissionStatus.getReason());
               }
               break;
            }
         }
      }
      return permissionStatus;
   }
}
