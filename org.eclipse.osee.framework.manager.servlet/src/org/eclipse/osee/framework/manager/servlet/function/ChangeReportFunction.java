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
import org.eclipse.osee.framework.core.data.ChangeItem;
import org.eclipse.osee.framework.core.data.ChangeReportRequest;
import org.eclipse.osee.framework.core.data.ChangeReportResponse;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.services.IDataTranslationService;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.manager.servlet.MasterServletActivator;

/**
 * @author Jeff C. Phillips
 */
public class ChangeReportFunction {

   public void getChanges(HttpServletRequest req, HttpServletResponse resp) throws Exception {
      IDataTranslationService service = MasterServletActivator.getInstance().getTranslationService();
      ChangeReportRequest data = service.convert(req.getInputStream(), CoreTranslatorId.CHANGE_REPORT_REQUEST);
      ArrayList<ChangeItem> changes = new ArrayList<ChangeItem>();

      MasterServletActivator.getInstance().getChangeReportService().getChanges(new NullProgressMonitor(),
            data.getToTransactionRecord(), data.getFromTransactionRecord(), data.isHistorical(), changes);

      //      ArrayList<ChangeItem> changes = new ArrayList<ChangeItem>();
      //      changes.add(new ArtifactChangeItem(45L, ModificationType.MODIFIED, 567, 45));
      ChangeReportResponse changeReportResponseData = new ChangeReportResponse(changes);
      resp.setStatus(HttpServletResponse.SC_ACCEPTED);
      resp.setContentType("text/xml");
      resp.setCharacterEncoding("UTF-8");
      InputStream inputStream =
            service.convertToStream(changeReportResponseData, CoreTranslatorId.CHANGE_REPORT_RESPONSE);
      Lib.inputStreamToOutputStream(inputStream, resp.getOutputStream());
   }
}
