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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.core.data.ChangeBranchArchiveStateRequest;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.services.IDataTranslationService;
import org.eclipse.osee.framework.core.services.IOseeBranchServiceProvider;
import org.eclipse.osee.framework.core.services.IOseeDataTranslationProvider;
import org.eclipse.osee.framework.manager.servlet.internal.Activator;

/**
 * @author Megumi Telles
 */
public class ChangeBranchArchiveStateFunction extends AbstractOperation {
   private final HttpServletRequest req;
   private final HttpServletResponse resp;
   private final IOseeBranchServiceProvider branchServiceProvider;
   private final IOseeDataTranslationProvider dataTransalatorProvider;

   public ChangeBranchArchiveStateFunction(HttpServletRequest req, HttpServletResponse resp, IOseeBranchServiceProvider branchServiceProvider, IOseeDataTranslationProvider dataTransalatorProvider) {
      super("Update Branch Archived State", Activator.PLUGIN_ID);
      this.req = req;
      this.resp = resp;
      this.branchServiceProvider = branchServiceProvider;
      this.dataTransalatorProvider = dataTransalatorProvider;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      IDataTranslationService service = dataTransalatorProvider.getTranslatorService();
      ChangeBranchArchiveStateRequest request =
            service.convert(req.getInputStream(), CoreTranslatorId.CHANGE_BRANCH_ARCHIVE_STATE);
      branchServiceProvider.getBranchService().updateBranchArchiveState(new NullProgressMonitor(), request);

      resp.setStatus(HttpServletResponse.SC_ACCEPTED);
      resp.setContentType("text/plain");
      resp.setCharacterEncoding("UTF-8");
      resp.getWriter().write("Purge was successful");
      resp.getWriter().flush();
   }
}