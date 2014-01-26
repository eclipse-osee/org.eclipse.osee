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
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.message.PurgeBranchRequest;
import org.eclipse.osee.framework.core.model.BranchReadable;
import org.eclipse.osee.framework.core.translation.IDataTranslationService;
import org.eclipse.osee.orcs.ApplicationContext;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Roberto E. Escobar
 */
public class PurgeBranchCallable extends AbstractBranchCallable<PurgeBranchRequest, Object> {

   public PurgeBranchCallable(ApplicationContext context, HttpServletRequest req, HttpServletResponse resp, IDataTranslationService translationService, OrcsApi orcsApi) {
      super(context, req, resp, translationService, orcsApi, "text/plain", CoreTranslatorId.PURGE_BRANCH_REQUEST, null);
   }

   @Override
   public Object executeCall(PurgeBranchRequest request) throws Exception {
      IOseeBranch toPurge = getBranchOps().getBranchFromId(request.getBranchId());
      boolean recurse = request.isRecursive();

      Callable<List<BranchReadable>> callable = getBranchOps().purgeBranch(toPurge, recurse);
      callAndCheckForCancel(callable);

      return Boolean.TRUE;
   }

}
