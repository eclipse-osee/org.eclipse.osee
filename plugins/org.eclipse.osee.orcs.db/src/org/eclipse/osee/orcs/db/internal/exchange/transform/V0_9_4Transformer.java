/*********************************************************************
 * Copyright (c) 2010 Boeing
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
public class V0_9_4Transformer implements IOseeExchangeVersionTransformer {
   private static final Version MAX_VERSION = new Version("0.9.4");

   @Override
   public Version applyTransform(ExchangeDataProcessor processor, Log logger) {
      return getMaxVersion();
   }

   @Override
   public Version getMaxVersion() {
      return MAX_VERSION;
   }

   @Override
   public void finalizeTransform(Log logger, OrcsSession session, JdbcClient jdbcClient, ExchangeDataProcessor processor) {
      //
   }
}