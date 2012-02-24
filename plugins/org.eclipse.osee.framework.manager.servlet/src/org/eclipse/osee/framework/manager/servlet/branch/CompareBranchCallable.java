/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.manager.servlet.branch;

import java.util.List;
import java.util.concurrent.Callable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.osee.framework.core.data.ITransaction;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.message.ChangeReportRequest;
import org.eclipse.osee.framework.core.message.ChangeReportResponse;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.core.translation.IDataTranslationService;
import org.eclipse.osee.orcs.ApplicationContext;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Roberto E. Escobar
 */
public class CompareBranchCallable extends AbstractBranchCallable<ChangeReportRequest, ChangeReportResponse> {

   public CompareBranchCallable(ApplicationContext context, HttpServletRequest req, HttpServletResponse resp, IDataTranslationService translationService, OrcsApi orcsApi) {
      super(context, req, resp, translationService, orcsApi, "text/xml", CoreTranslatorId.CHANGE_REPORT_REQUEST,
         CoreTranslatorId.CHANGE_REPORT_RESPONSE);
   }

   @Override
   protected ChangeReportResponse executeCall(ChangeReportRequest request) throws Exception {
      ITransaction sourceTx = TokenFactory.createTransaction(request.getSourceTx());
      ITransaction destinationTx = TokenFactory.createTransaction(request.getDestinationTx());

      Callable<List<ChangeItem>> callable = getBranchOps().compareBranch(sourceTx, destinationTx);
      List<ChangeItem> items = callAndCheckForCancel(callable);

      ChangeReportResponse response = new ChangeReportResponse();
      response.setChangeItems(items);

      return response;
   }
}
