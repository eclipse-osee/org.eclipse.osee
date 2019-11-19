/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.branch;

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.BranchView;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.model.access.PermissionStatus;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.AccessPolicy;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
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
   private static AccessPolicy policy;

   public static boolean changeApplicability(List<? extends ArtifactToken> artifacts) {
      BranchId branch = artifacts.iterator().next().getBranch();
      ApplicabilityEndpoint applEndpoint = ServiceUtil.getOseeClient().getApplicabilityEndpoint(branch);
      Iterable<ApplicabilityToken> applicabilityTokens = applEndpoint.getApplicabilityTokens();

      ViewApplicabilityFilterTreeDialog dialog =
         new ViewApplicabilityFilterTreeDialog("Select View Applicability", "Select View Applicability");
      dialog.setInput(applicabilityTokens);
      dialog.setMultiSelect(false);
      int result = dialog.open();
      if (result == Window.OK) {
         applEndpoint.setApplicability(ApplicabilityId.valueOf(dialog.getSelection().getId()), artifacts);
         return true;
      }
      return false;
   }

   private static AccessPolicy getAccessPolicy() {
      if (policy == null) {
         policy = ServiceUtil.getAccessPolicy();
      }
      return policy;
   }

   public static boolean isChangeApplicabilityValid(Collection<Artifact> artifacts) {
      try {
         for (Artifact artifact : artifacts) {
            if (artifact.isReadOnly()) {
               return false;
            }
         }
         PermissionStatus permissionStatus = new PermissionStatus();
         permissionStatus = getAccessPolicy().hasArtifactPermission(artifacts, PermissionEnum.WRITE, Level.FINE);
         boolean isWriteable = permissionStatus.matched();
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
         List<Artifact> branchViews = ArtifactQuery.getArtifactListFromType(BranchView, branch);
         for (Artifact art : branchViews) {
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