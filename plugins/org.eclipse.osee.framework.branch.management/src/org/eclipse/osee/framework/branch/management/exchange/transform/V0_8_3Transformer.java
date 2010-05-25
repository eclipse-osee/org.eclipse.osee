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
package org.eclipse.osee.framework.branch.management.exchange.transform;

import org.eclipse.osee.framework.branch.management.exchange.handler.ExportItem;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.osgi.framework.Version;

/**
 * @author Ryan D. Brooks
 */
public class V0_8_3Transformer implements IOseeExchangeVersionTransformer {
   private static final Version MAX_VERSION = new Version("0.8.3");

   @Override
   public String applyTransform(ExchangeDataProcessor ruleProcessor) throws OseeCoreException {
      ruleProcessor.transform(ExportItem.EXPORT_DB_SCHEMA, new V0_8_3_DbSchemaRule());
      ruleProcessor.transform(ExportItem.OSEE_BRANCH_DATA, new V0_8_3_BranchRule());
      return getMaxVersion().toString();
   }

   @Override
   public void finalizeTransform(ExchangeDataProcessor ruleProcessor) throws Exception {
   }

   @Override
   public Version getMaxVersion() {
      return MAX_VERSION;
   }
}
