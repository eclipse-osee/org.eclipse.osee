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
package org.eclipse.osee.framework.branch.management.exchange.export;

import java.io.Writer;
import org.eclipse.osee.framework.branch.management.exchange.ExchangeUtil;
import org.eclipse.osee.framework.branch.management.exchange.ExportImportXml;
import org.eclipse.osee.framework.branch.management.exchange.handler.ExportItem;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractXmlExportItem extends AbstractExportItem {

   public AbstractXmlExportItem(ExportItem id) {
      super(id);
   }

   @Override
   public final void executeWork() throws Exception {
      Writer writer = null;
      try {
         writer = ExchangeUtil.createXmlWriter(getWriteLocation(), getFileName(), getBufferSize());
         ExportImportXml.openXmlNode(writer, ExportImportXml.DATA);
         if (!isCancel()) {
            try {
               doWork(writer);
            } catch (Exception ex) {
               notifyOnExportException(ex);
            }
         }
         ExportImportXml.closeXmlNode(writer, ExportImportXml.DATA);
      } finally {
         Lib.close(writer);
      }
   }

   protected abstract void doWork(Appendable appendable) throws Exception;

}
