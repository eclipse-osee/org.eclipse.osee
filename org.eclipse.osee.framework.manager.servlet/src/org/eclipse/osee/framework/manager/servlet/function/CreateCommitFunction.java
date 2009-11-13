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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.core.IDataTranslationService;
import org.eclipse.osee.framework.core.data.BranchCommitData;
import org.eclipse.osee.framework.core.data.CommitTransactionRecordResponse;
import org.eclipse.osee.framework.core.exchange.BranchCommitDataResponder;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.manager.servlet.MasterServletActivator;

/**
 * @author Megumi Telles
 */
public class CreateCommitFunction {

   public void commitBranch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
      PropertyStore propertyStore = new PropertyStore();
      propertyStore.load(req.getInputStream());

      IDataTranslationService service = MasterServletActivator.getInstance().getTranslationService();
      BranchCommitData data = service.convert(propertyStore, BranchCommitData.class);
      CommitTransactionRecordResponse responseData = new CommitTransactionRecordResponse();
      IStatus status =
            MasterServletActivator.getInstance().getBranchCommit().commitBranch(new NullProgressMonitor(), data);
      if (status.isOK()) {
         resp.setStatus(HttpServletResponse.SC_ACCEPTED);
         resp.setContentType("text/plain");
         resp.getOutputStream().write(new BranchCommitDataResponder().convertToReponse(responseData));
      } else {
         resp.setStatus(HttpURLConnection.HTTP_INTERNAL_ERROR);
         resp.setContentType("text/plain");
         resp.getWriter().write("Unknown Error during branch creation.");
      }
   }
}
