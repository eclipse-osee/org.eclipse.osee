/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
