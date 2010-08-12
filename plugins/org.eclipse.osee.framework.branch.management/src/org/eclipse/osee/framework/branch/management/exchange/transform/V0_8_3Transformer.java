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

import java.util.regex.Pattern;
import org.eclipse.osee.framework.branch.management.exchange.handler.ExportItem;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.text.rules.ReplaceAll;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.osgi.framework.Version;

/**
 * @author Ryan D. Brooks
 */
public class V0_8_3Transformer implements IOseeExchangeVersionTransformer {
   private static final Version MAX_VERSION = new Version("0.8.3");

   @Override
   public Version applyTransform(ExchangeDataProcessor processor) throws OseeCoreException {

      HashCollection<String, String> tableToColumns = new HashCollection<String, String>();
      tableToColumns.put("osee_txs", "<column id=\"branch_id\" type=\"INTEGER\" />\n");
      tableToColumns.put("osee_branch", "<column id=\"branch_guid\" type=\"VARCHAR\" />\n");
      tableToColumns.put("osee_branch", "<column id=\"branch_state\" type=\"INTEGER\" />\n");
      processor.transform(ExportItem.EXPORT_DB_SCHEMA, new DbSchemaRuleAddColumn(tableToColumns));

      processor.transform(ExportItem.EXPORT_DB_SCHEMA,
         new ReplaceAll(Pattern.compile("\\s+<table name=\"osee_\\w+_type\".*?</table>", Pattern.DOTALL), ""));

      processor.transform(ExportItem.OSEE_BRANCH_DATA, new V0_8_3_BranchRule());
      return getMaxVersion();
   }

   @Override
   public void finalizeTransform(ExchangeDataProcessor ruleProcessor) throws Exception {
   }

   @Override
   public Version getMaxVersion() {
      return MAX_VERSION;
   }
}
