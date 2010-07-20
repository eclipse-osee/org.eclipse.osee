/*
 * Created on Jul 20, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.model.access.exp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.model.IBasicArtifact;

/**
 * @author Jeff C. Phillips
 */
public class AccessCollector {

   private final Collection<IAccessFilter> filters;

   public AccessCollector(Collection<IAccessFilter> filters) {
      this.filters = filters;
   }

   public List<IAttributeType> getAttributeTypes(IBasicArtifact<?> artifact, Collection<IAttributeType> attrs, PermissionEnum permission) {
      List<IAttributeType> validAttrs = new ArrayList<IAttributeType>();
      AccessFilterChain chain = new AccessFilterChain();
      chain.addAll(filters);

      for (IAttributeType type : attrs) {
         if (chain.doFilter(artifact, type, permission, null)) {
            validAttrs.add(type);
         }
      }
      return validAttrs;
   }
}
