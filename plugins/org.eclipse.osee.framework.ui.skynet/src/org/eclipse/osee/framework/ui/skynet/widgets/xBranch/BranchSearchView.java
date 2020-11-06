/*********************************************************************
 * Copyright (c) 2020 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets.xBranch;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.orcs.rest.client.OseeClient;
import org.eclipse.osee.orcs.rest.model.BranchEndpoint;
import org.eclipse.osee.orcs.rest.model.BranchQueryData;

/**
 * This is a beta replacement for BranchManager that uses searches instead of always loading every branch. BranchView
 * will eventually go away once this view is vetted.
 *
 * @author Donald G. Dunne
 */
public class BranchSearchView extends BranchView {

   public static final String VIEW_ID = "org.eclipse.osee.framework.ui.skynet.widgets.xBranch.BranchSearchView";

   public BranchSearchView() {
      super(VIEW_ID, "Branch Search Manager");
   }

   @Override
   protected boolean isBranchSearchView() {
      return true;
   }

   protected boolean isInitialized() {
      return xBranchWidget != null;
   }

   @Override
   public void loadData() {
      xBranchWidget.loadData();
   }

   @Override
   public void loadData(List<Branch> branches) {
      xBranchWidget.loadData(branches);
   }

   @Override
   public void handleQuerySearch() {
      OseeClient oseeClient = ServiceUtil.getOseeClient();
      BranchEndpoint endpoint = oseeClient.getBranchEndpoint();
      if (branchData.isAsIds()) {
         if (Strings.isNumeric(branchData.getNamePattern())) {
            // Only want to search by id and ignore rest of criteria
            BranchQueryData idBranchData = new BranchQueryData();
            idBranchData.setAsIds(true);
            idBranchData.setBranchIds(Arrays.asList(BranchId.valueOf(branchData.getNamePattern())));
            if (UserManager.getUser().isOseeAdmin()) {
               idBranchData.setIncludeArchived(true);
               idBranchData.setIncludeDeleted(true);
            }
            List<Branch> branches = endpoint.getFromQuery(idBranchData);
            loadData(branches);
            return;
         } else {
            AWorkbench.popup("No numeric id to search.");
            loadData(Collections.emptyList());
         }
      }
      List<Branch> branches = endpoint.getFromQuery(branchData);
      loadData(branches);
   }

}
