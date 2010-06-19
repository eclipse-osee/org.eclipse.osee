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
import org.eclipse.osee.framework.branch.management.IOseeBranchServiceProvider;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.message.ChangeBranchStateRequest;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.translation.IDataTranslationService;
import org.eclipse.osee.framework.core.translation.IDataTranslationServiceProvider;
import org.eclipse.osee.framework.manager.servlet.internal.Activator;

/**
 * @author Megumi Telles
 */
public class ChangeBranchStateFunction extends AbstractOperation {
   private final HttpServletRequest req;
   private final HttpServletResponse resp;
   private final IOseeBranchServiceProvider branchServiceProvider;
   private final IDataTranslationServiceProvider dataTransalatorProvider;

   public ChangeBranchStateFunction(HttpServletRequest req, HttpServletResponse resp, IOseeBranchServiceProvider branchServiceProvider, IDataTranslationServiceProvider dataTransalatorProvider) {
      super("Update Branch State", Activator.PLUGIN_ID);
      this.req = req;
      this.resp = resp;
      this.branchServiceProvider = branchServiceProvider;
      this.dataTransalatorProvider = dataTransalatorProvider;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      IDataTranslationService service = dataTransalatorProvider.getTranslationService();
      ChangeBranchStateRequest request = service.convert(req.getInputStream(), CoreTranslatorId.CHANGE_BRANCH_STATE);
      branchServiceProvider.getBranchService().updateBranchState(new NullProgressMonitor(), request);

      resp.setStatus(HttpServletResponse.SC_ACCEPTED);
      resp.setContentType("text/plain");
      resp.setCharacterEncoding("UTF-8");
      resp.getWriter().write("Purge was successful");
      resp.getWriter().flush();
   }
}