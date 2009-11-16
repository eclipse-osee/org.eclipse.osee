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

import org.eclipse.osee.framework.manager.servlet.MasterServletActivator;
import org.eclipse.osee.framework.manager.servlet.data.HttpBranchCreationInfo;

/**
 * @author Jeff C. Phillips
 */
public class CreateBranchFunction {
   
   public void createBranch(HttpServletRequest req, HttpServletResponse resp)throws Exception {
      HttpBranchCreationInfo info = new HttpBranchCreationInfo(req);
      int branchId = -1;
      branchId =
            MasterServletActivator.getInstance().getBranchCreation().createBranch(info.getBranch(), info.getAuthorId(),
                  info.getCreationComment(), info.getPopulateBaseTxFromAddressingQueryId(),
                  info.getDestinationBranchId());
      if (branchId == -1) {
         resp.setStatus(HttpURLConnection.HTTP_INTERNAL_ERROR);
         resp.setContentType("text/plain");
         resp.getWriter().write("Unknown Error during branch creation.");
      } else {
         resp.setStatus(HttpServletResponse.SC_ACCEPTED);
         resp.setContentType("text/plain");
         resp.getWriter().write(Integer.toString(branchId));
      }
   }
}
