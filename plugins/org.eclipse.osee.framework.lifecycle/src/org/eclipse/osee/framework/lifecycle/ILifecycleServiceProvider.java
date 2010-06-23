/*
 * Created on Jun 21, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.lifecycle;

import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Jeff C. Phillips
 */
public interface ILifecycleServiceProvider {
   public ILifecycleService getLifecycleServices() throws OseeCoreException;
}
