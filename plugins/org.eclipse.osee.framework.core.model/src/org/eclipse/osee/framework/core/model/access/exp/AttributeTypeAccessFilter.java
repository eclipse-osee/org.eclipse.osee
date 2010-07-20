/*
 * Created on Jul 15, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.model.access.exp;

import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.model.IBasicArtifact;

public class AttributeTypeAccessFilter extends AbstractAccessFilter {
   private final PermissionEnum permission;
   private final IBasicArtifact<?> artifact;
   private final IAttributeType attribute;

   public AttributeTypeAccessFilter(PermissionEnum permission, IBasicArtifact<?> artifact, IAttributeType attribute) {
      super();
      this.permission = permission;
      this.artifact = artifact;
      this.attribute = attribute;
   }

   @Override
   public int getRank() {
      return 10;
   }

   @Override
   public boolean acceptToObject(Object object) {
      return true;
   }

   @Override
   public PermissionEnum filter(Object object, PermissionEnum toPermission, PermissionEnum agrPermission, AccessFilterChain filterChain) {
      return null;
   }

}
