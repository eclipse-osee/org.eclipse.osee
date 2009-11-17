/*******************************************************************************
 * Copyright(c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.manager.servlet.function;

import java.io.InputStream;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.core.IDataTranslationService;
import org.eclipse.osee.framework.core.data.ChangeItem;
import org.eclipse.osee.framework.core.data.ChangeReportRequestData;
import org.eclipse.osee.framework.core.data.ChangeReportResponseData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.manager.servlet.MasterServletActivator;

/**
 * @author Jeff C. Phillips
 */
public class ChangeReportFunction {

   public void getChanges(HttpServletRequest req, HttpServletResponse resp) throws Exception {
      IDataTranslationService service = MasterServletActivator.getInstance().getTranslationService();
      ChangeReportRequestData data = service.convert(req.getInputStream(), ChangeReportRequestData.class);
      ArrayList<ChangeItem> changes = new ArrayList<ChangeItem>();
      
            MasterServletActivator.getInstance().getChangeReportService().getChanges(data.getToTransactionRecord(),
                  data.getFromTransactionRecord(), new NullProgressMonitor(), data.isHistorical(), changes);
      ChangeReportResponseData changeReportResponseData = new ChangeReportResponseData(changes);
      resp.setStatus(HttpServletResponse.SC_ACCEPTED);
      resp.setContentType("text/xml");
      InputStream inputStream = service.convertToStream(changeReportResponseData);
      try {
         Lib.inputStreamToOutputStream(inputStream, resp.getOutputStream());
      } finally {
         Lib.close(inputStream);
      }
   }
}
