/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.cluster.admin;

import java.util.concurrent.Callable;

/**
 * @author Roberto E. Escobar
 */
public interface ClusterAdmin {

   /**
    * The name of this cluster instance
    * 
    * @return name of this instance
    */
   String getName();

   /**
    * Returns the distributed executor service to execute <tt>Runnables</tt> and <tt>Callables</tt> on the cluster.
    * 
    * @return distributed executor service of this cluster instance
    */
   DistributedExecutorService getExecutor();

   /**
    * Returns the Cluster that this instance is part of.
    * 
    * @return cluster that this instance is part of
    */
   Cluster getCluster();

   /**
    * Returns the transaction instance associated with the current thread, creates a new one if it wasn't already.
    * <p/>
    * Transaction doesn't start until <tt>transaction.begin()</tt> is called and if a transaction is started then all
    * transactional operations are automatically transactional.
    * 
    * <pre>
    * Map map = getMap(&quot;mymap&quot;);
    * Transaction txn = clusterAdmin.getTransaction();
    * txn.begin();
    * try {
    *    map.put(&quot;key&quot;, &quot;value&quot;);
    *    txn.commit();
    * } catch (Exception e) {
    *    txn.rollback();
    * }
    * </pre>
    * 
    * @return transaction for the current thread
    */
   Transaction getTransaction();

   /**
    * Creates a callable with a default transaction/roll-back implementation.
    * 
    * @return callable with transaction begin, commit, roll-back surrounding work
    */
   <T> Callable<T> createTxCallable(TransactionWork<T> work);
}
