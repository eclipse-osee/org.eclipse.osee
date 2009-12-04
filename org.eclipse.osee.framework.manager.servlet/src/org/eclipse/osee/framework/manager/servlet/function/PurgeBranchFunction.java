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

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.core.data.PurgeBranchRequest;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.services.IDataTranslationService;
import org.eclipse.osee.framework.core.services.IOseeBranchServiceProvider;
import org.eclipse.osee.framework.core.services.IOseeDataTranslationProvider;

/**
 * @author Megumi Telles
 * @author Jeff C. Phillips
 */
public class PurgeBranchFunction {

   public void purge(HttpServletRequest req, HttpServletResponse resp, IOseeBranchServiceProvider branchServiceProvider, IOseeDataTranslationProvider dataTransalatorProvider) throws Exception {
      IDataTranslationService service = dataTransalatorProvider.getTranslatorService();
      PurgeBranchRequest request = service.convert(req.getInputStream(), CoreTranslatorId.PURGE_BRANCH_REQUEST);
      branchServiceProvider.getBranchService().purge(new NullProgressMonitor(), request);

      resp.setStatus(HttpServletResponse.SC_ACCEPTED);
      resp.setContentType("text/plain");
      resp.setCharacterEncoding("UTF-8");
      resp.getWriter().write("Purge was successful");
      resp.getWriter().flush();
   }
}
