/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.transaction;

import org.eclipse.osee.framework.skynet.core.transaction.TxMonitorImpl.MonitoredTx;

/**
 * @author Roberto E. Escobar
 */
public interface TxMonitor<K> {

   void checkForComodification(K key, MonitoredTx tx, Object object);

   void createTx(K key, MonitoredTx tx);

   void beginTx(K key, MonitoredTx tx);

   void endTx(K key, MonitoredTx tx);

   void rollbackTx(K key, MonitoredTx tx);

   void cancel(K key, MonitoredTx tx);

}