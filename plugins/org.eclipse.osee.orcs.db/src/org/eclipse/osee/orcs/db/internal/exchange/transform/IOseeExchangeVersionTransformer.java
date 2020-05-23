/*********************************************************************
 * Copyright (c) 2009 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.orcs.db.internal.exchange.transform;

import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.osgi.framework.Version;

/**
 * @author Ryan D. Brooks
 */
public interface IOseeExchangeVersionTransformer {

   public abstract Version applyTransform(ExchangeDataProcessor processor, Log logger);

   public abstract Version getMaxVersion();

   public abstract void finalizeTransform(Log logger, OrcsSession session, JdbcClient jdbcClient, ExchangeDataProcessor processor);
}
