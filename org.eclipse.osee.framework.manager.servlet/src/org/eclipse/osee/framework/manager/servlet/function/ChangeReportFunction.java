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

import java.net.HttpURLConnection;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.core.IDataTranslationService;
import org.eclipse.osee.framework.core.data.BranchCommitData;
import org.eclipse.osee.framework.core.data.ChangeItem;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.manager.servlet.MasterServletActivator;

/**
 * @author Jeff C. Phillips
 */
public class ChangeReportFunction {
   
   public void processRequest(HttpServletRequest req, HttpServletResponse resp) throws Exception{
      PropertyStore propertyStore = new PropertyStore();
      propertyStore.load(req.getInputStream());

      IDataTranslationService service = MasterServletActivator.getInstance().getTranslationService();
      BranchCommitData data = service.convert(propertyStore, BranchCommitData.class);
      ArrayList<ChangeItem> changes = new ArrayList<ChangeItem>();
      IStatus status =
            MasterServletActivator.getInstance().getChangeReportService().getChanges(null, null, new NullProgressMonitor(), false, changes);
      if (status.isOK()) {
         resp.setStatus(HttpServletResponse.SC_ACCEPTED);
         resp.setContentType("text/plain");
         resp.getWriter().write(
               String.format("Commit of [%s] into [%s] was successful.", data.getSourceBranch(),
                     data.getDestinationBranch()));
      } else {
         resp.setStatus(HttpURLConnection.HTTP_INTERNAL_ERROR);
         resp.setContentType("text/plain");
         resp.getWriter().write("Unknown Error during branch creation.");
      }
   }
}
