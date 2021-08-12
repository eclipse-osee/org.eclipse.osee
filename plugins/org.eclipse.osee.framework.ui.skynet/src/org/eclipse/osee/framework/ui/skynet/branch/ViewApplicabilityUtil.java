/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.framework.ui.skynet.branch;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.ViewApplicabilityFilterTreeDialog;
import org.eclipse.osee.orcs.rest.model.ApplicabilityEndpoint;

/**
 * @author Donald G. Dunne
 */
public class ViewApplicabilityUtil {

   public static String CHANGE_APPLICABILITY_INVAILD = "User does not have permissions to change View Applicability";
   public static String SAVE_OTHER_CHANGES = "Save all other changes before making View Applicability update";

   public static Pair<Boolean, String> changeApplicability(List<? extends ArtifactToken> artifacts) {
      BranchId branch = artifacts.iterator().next().getBranch();
      ApplicabilityEndpoint applEndpoint = ServiceUtil.getOseeClient().getApplicabilityEndpoint(branch);
      Iterable<String> possibleApplicabilities = applEndpoint.getPossibleApplicabilities();
      ViewApplicabilityFilterTreeDialog dialog = new ViewApplicabilityFilterTreeDialog("Select View Applicability",
         "Select View Applicability.  Saves immediately.");
      dialog.setInput(possibleApplicabilities);
      dialog.setMultiSelect(false);
      int result = dialog.open();
      if (result == Window.OK) {
         String value = dialog.getSelection();
         TransactionToken transaction = applEndpoint.setApplicabilityByString(value, artifacts);
         Boolean success = transaction.isValid();
         return new Pair<Boolean, String>(success, value);
      }
      return new Pair<>(false, "");
   }

   public static boolean isChangeApplicabilityValid(Collection<Artifact> artifacts) {
      try {
         for (Artifact artifact : artifacts) {
            if (artifact.isReadOnly()) {
               return false;
            }
         }
         XResultData rd = ServiceUtil.accessControlService().hasArtifactPermission(UserManager.getUser(), artifacts,
            PermissionEnum.WRITE, null);
         boolean isWriteable = rd.isSuccess();
         if (!isWriteable) {
            return false;
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         return false;
      }
      return true;
   }

   //##########################
   // Support for branch views
   //##########################

   public static ApplicabilityEndpoint getApplicabilityEndpoint(BranchId branch) {
      if (branch != null) {
         ApplicabilityEndpoint applEndpoint = ServiceUtil.getOseeClient().getApplicabilityEndpoint(branch);
         return applEndpoint;
      }
      return null;
   }

   public static Map<Long, String> getBranchViews(BranchId branch) {
      Map<Long, String> viewsToBranchData = new HashMap<>();
      if (branch != null && branch.isValid()) {
         List<ArtifactToken> branchViews = getApplicabilityEndpoint(branch).getViews();
         for (ArtifactToken art : branchViews) {
            viewsToBranchData.put(art.getId(), art.getName());
         }
      }
      return viewsToBranchData;
   }

   public static boolean isBranchOfProductLine(BranchId branch) {
      return !getBranchViews(branch).isEmpty();
   }

   public static BranchId getParentBranch(BranchId branch) {
      if (BranchManager.getType(branch).isMergeBranch()) {
         branch = BranchManager.getParentBranch(branch);
      }
      return branch;
   }
}