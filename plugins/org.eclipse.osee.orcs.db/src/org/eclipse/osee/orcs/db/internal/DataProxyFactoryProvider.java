/*
 * Created on Sep 30, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.orcs.db.internal;

import org.eclipse.osee.orcs.core.ds.DataProxyFactory;

public interface DataProxyFactoryProvider {

   DataProxyFactory getProxy(String factoryAlias);

}