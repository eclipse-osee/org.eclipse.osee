/*
 * Created on Jul 7, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.model.access;

import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.model.IBasicArtifact;

/**
 * @author Jeff C. Phillips
 * @author Roberto E. Escobar
 * @param <T>
 */
public interface IAcceptFilter<T> {
   boolean accept(T item, IBasicArtifact<?> artifact, PermissionEnum permission);

   T getObject(Object object);
}
