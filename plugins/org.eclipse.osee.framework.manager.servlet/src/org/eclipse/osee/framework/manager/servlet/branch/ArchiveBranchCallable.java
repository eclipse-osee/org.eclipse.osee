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

import java.util.concurrent.Callable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.message.ChangeBranchArchiveStateRequest;
import org.eclipse.osee.framework.core.translation.IDataTranslationService;
import org.eclipse.osee.orcs.ApplicationContext;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArchiveOperation;

/**
 * @author Roberto E. Escobar
 */
public class ArchiveBranchCallable extends AbstractBranchCallable<ChangeBranchArchiveStateRequest, Object> {

   public ArchiveBranchCallable(ApplicationContext context, HttpServletRequest req, HttpServletResponse resp, IDataTranslationService translationService, OrcsApi orcsApi) {
      super(context, req, resp, translationService, orcsApi, "text/plain",
         CoreTranslatorId.CHANGE_BRANCH_ARCHIVE_STATE, null);
   }

   @Override
   protected Object executeCall(ChangeBranchArchiveStateRequest request) throws Exception {
      IOseeBranch toArchive = getBranchFromUuid(request.getBranchId());
      ArchiveOperation archiveOp =
         request.getState().isArchived() ? ArchiveOperation.ARCHIVE : ArchiveOperation.UNARCHIVE;

      Callable<Void> callable = getBranchOps().archiveUnarchiveBranch(toArchive, archiveOp);
      callAndCheckForCancel(callable);

      return Boolean.TRUE;
   }

}
