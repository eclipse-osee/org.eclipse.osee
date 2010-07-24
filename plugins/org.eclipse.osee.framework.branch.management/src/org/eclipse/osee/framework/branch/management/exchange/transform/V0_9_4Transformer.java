/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.branch.management.exchange.transform;

import org.eclipse.osee.framework.branch.management.exchange.handler.ExportItem;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.text.rules.ReplaceAll;
import org.osgi.framework.Version;

/**
 * @author Ryan D. Brooks
 */
public class V0_9_4Transformer implements IOseeExchangeVersionTransformer {
   private static final Version MAX_VERSION = new Version("0.9.4");

   @Override
   public String applyTransform(ExchangeDataProcessor processor) throws OseeCoreException {

      processor.transform(ExportItem.EXPORT_DB_SCHEMA, new ReplaceAll("\\s+<table name=\"osee_\\w+_type\".*?</table>",
         ""));
      return getMaxVersion().toString();
   }

   @Override
   public Version getMaxVersion() {
      return MAX_VERSION;
   }

   @Override
   public void finalizeTransform(ExchangeDataProcessor processor) throws Exception {
   }
}