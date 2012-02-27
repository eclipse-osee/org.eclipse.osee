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
package org.eclipse.osee.orcs.db.internal.exchange.export;

import java.io.Writer;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.db.internal.exchange.ExchangeUtil;
import org.eclipse.osee.orcs.db.internal.exchange.ExportImportXml;
import org.eclipse.osee.orcs.db.internal.exchange.handler.ExportItem;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractXmlExportItem extends AbstractExportItem {

   public AbstractXmlExportItem(Log logger, ExportItem id) {
      super(logger, id);
   }

   @Override
   public final void executeWork() throws Exception {
      checkForCancelled();
      Writer writer = null;
      try {
         writer = ExchangeUtil.createXmlWriter(getWriteLocation(), getFileName(), getBufferSize());
         ExportImportXml.openXmlNode(writer, ExportImportXml.DATA);

         try {
            checkForCancelled();
            doWork(writer);
         } finally {
            ExportImportXml.closeXmlNode(writer, ExportImportXml.DATA);
         }
      } finally {
         Lib.close(writer);
      }
   }

   protected abstract void doWork(Appendable appendable) throws Exception;

}
