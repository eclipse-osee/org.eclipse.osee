/*
 * Created on Jul 20, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.model.access.exp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.model.IBasicArtifact;

/**
 * @author Jeff C. Phillips
 * @param <T>
 */
public class AccessCollector<T> {

   private final Collection<IAccessFilter> filters;

   public AccessCollector(Collection<IAccessFilter> filters) {
      this.filters = filters;
   }

   public List<T> getAttributeTypesToMatch(IBasicArtifact<?> artifact, Collection<T> toCheck, PermissionEnum permission) {
      List<T> validObjects = new ArrayList<T>();
      AccessFilterChain chain = new AccessFilterChain();
      chain.addAll(filters);

      for (T object : toCheck) {
         if (chain.doFilter(artifact, object, permission, null)) {
            validObjects.add(object);
         }
      }
      return validObjects;
   }
}
