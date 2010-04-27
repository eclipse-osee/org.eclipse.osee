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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.BranchCreationRequest;
import org.eclipse.osee.framework.core.data.BranchCreationResponse;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.LogProgressMonitor;
import org.eclipse.osee.framework.core.services.IDataTranslationService;
import org.eclipse.osee.framework.core.services.IOseeBranchServiceProvider;
import org.eclipse.osee.framework.core.services.IOseeDataTranslationProvider;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.manager.servlet.internal.Activator;

/**
 * @author Jeff C. Phillips
 */
public class CreateBranchFunction extends AbstractOperation {
   private final HttpServletRequest req;
   private final HttpServletResponse resp;
   private final IOseeBranchServiceProvider branchServiceProvider;
   private final IOseeDataTranslationProvider dataTransalatorProvider;

   public CreateBranchFunction(HttpServletRequest req, HttpServletResponse resp, IOseeBranchServiceProvider branchServiceProvider, IOseeDataTranslationProvider dataTransalatorProvider) {
      super("Create Branch", Activator.PLUGIN_ID);
      this.req = req;
      this.resp = resp;
      this.branchServiceProvider = branchServiceProvider;
      this.dataTransalatorProvider = dataTransalatorProvider;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      IDataTranslationService service = dataTransalatorProvider.getTranslatorService();

      BranchCreationRequest creationRequest =
            service.convert(req.getInputStream(), CoreTranslatorId.BRANCH_CREATION_REQUEST);

      BranchCreationResponse creationResponse = new BranchCreationResponse(-1);

      branchServiceProvider.getBranchService().createBranch(new LogProgressMonitor(), creationRequest, creationResponse);

      resp.setStatus(HttpServletResponse.SC_ACCEPTED);
      resp.setContentType("text/xml");
      resp.setCharacterEncoding("UTF-8");
      InputStream inputStream = service.convertToStream(creationResponse, CoreTranslatorId.BRANCH_CREATION_RESPONSE);
      Lib.inputStreamToOutputStream(inputStream, resp.getOutputStream());
   }
}