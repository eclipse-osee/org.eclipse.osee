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
import org.eclipse.osee.framework.core.data.ArtifactChangeItem;
import org.eclipse.osee.framework.core.data.ChangeItem;
import org.eclipse.osee.framework.core.data.ChangeReportResponse;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.services.IDataTranslationService;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.manager.servlet.MasterServletActivator;

/**
 * @author Jeff C. Phillips
 */
public class ChangeReportFunction {

   public void getChanges(HttpServletRequest req, HttpServletResponse resp) throws Exception {
      IDataTranslationService service = MasterServletActivator.getInstance().getTranslationService();
      //      ChangeReportRequest data = service.convert(req.getInputStream(), ChangeReportRequest.class);
      //      ArrayList<ChangeItem> changes = new ArrayList<ChangeItem>();
      //
      //      MasterServletActivator.getInstance().getChangeReportService().getChanges(new NullProgressMonitor(),
      //            data.getToTransactionRecord(), data.getFromTransactionRecord(), data.isHistorical(), changes);

      ArrayList<ChangeItem> changes = new ArrayList<ChangeItem>();
      changes.add(new ArtifactChangeItem(1L, ModificationType.MODIFIED, 45, 1));
      changes.add(new ArtifactChangeItem(2L, ModificationType.NEW, 77, 2));
      changes.add(new ArtifactChangeItem(3L, ModificationType.DELETED, 66, 3));

      ChangeReportResponse changeReportResponseData = new ChangeReportResponse(changes);
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
