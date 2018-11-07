/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.access.internal;

import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.osee.framework.access.IAccessProvider;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.model.access.AccessData;
import org.eclipse.osee.framework.core.model.access.AccessDetail;
import org.eclipse.osee.framework.core.model.access.Scope;
import org.eclipse.osee.framework.core.services.IAccessControlService;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * @author Roberto E. Escobar
 */
public final class ObjectAccessProviderProxy implements IAccessProvider {

   private BundleContext bundleContext;

   public void start(BundleContext bundleContext) {
      this.bundleContext = bundleContext;
   }

   public void stop() {
      bundleContext = null;
   }

   private AccessControlService getAccessService() {
      AccessControlService toReturn = null;

      ServiceReference<IAccessControlService> reference =
         bundleContext.getServiceReference(IAccessControlService.class);
      IAccessControlService service = bundleContext.getService(reference);
      if (service instanceof AccessControlServiceProxy) {
         AccessControlServiceProxy proxy = (AccessControlServiceProxy) service;
         toReturn = proxy.getProxiedObject();
      } else {
         OseeLog.log(AccessControlHelper.class, Level.SEVERE, "Error initializing ObjectAccessProvider");
      }
      return toReturn;
   }

   @Override
   public void computeAccess(ArtifactToken userArtifact, Collection<?> objToCheck, AccessData accessData) {
      for (Object object : objToCheck) {
         if (object instanceof Artifact) {
            setArtifactAccessData(userArtifact, (Artifact) object, accessData);
         } else if (object instanceof BranchId) {
            setBranchAccessData(userArtifact, (BranchId) object, accessData);
         } else if (object instanceof RelationLink) {
            RelationLink relation = (RelationLink) object;
            Artifact artifactA = relation.getArtifactA();
            Artifact artifactB = relation.getArtifactB();
            setArtifactAccessData(userArtifact, artifactA, accessData);
            setArtifactAccessData(userArtifact, artifactB, accessData);
         }
      }
   }

   private void setArtifactAccessData(ArtifactToken userArtifact, Artifact artifact, AccessData accessData) {
      setBranchAccessData(userArtifact, artifact.getBranch(), accessData);
      String reason = "Legacy Artifact Permission";
      PermissionEnum userPermission = getAccessService().getArtifactPermission(userArtifact, artifact);

      if (userPermission == null) {
         reason = "User Permission was null in setArtifactAccessData  - artifact is read only";
         userPermission = PermissionEnum.READ;
      } else if (artifact.isHistorical()) {
         userPermission = PermissionEnum.READ;
         reason = "User Permission set to Read - artifact is historical  - artifact is read only";
      } else if (!BranchManager.isEditable(artifact.getBranch())) {
         userPermission = PermissionEnum.READ;
         reason = "User Permission set to Read - artifact's branch is not editable - artifact is read only";
      }
      //artifact.isDeleted()
      accessData.add(artifact,
         new AccessDetail<ArtifactToken>(artifact, userPermission, Scope.createLegacyScope(), reason));
   }

   private void setBranchAccessData(ArtifactToken userArtifact, BranchId branch, AccessData accessData) {
      String reason = "Legacy Branch Permission";
      PermissionEnum userPermission = getAccessService().getBranchPermission(userArtifact, branch);
      accessData.add(branch, new AccessDetail<BranchId>(branch, userPermission, Scope.createLegacyScope(), reason));
   }

}
