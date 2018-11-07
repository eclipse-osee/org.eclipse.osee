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
package org.eclipse.osee.framework.core.dsl.ui.integration.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.dsl.integration.ArtifactDataProvider;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Roberto E. Escobar
 */
public final class ArtifactDataProviderImpl implements ArtifactDataProvider {

   @Override
   public boolean isApplicable(Object object) {
      boolean result = false;
      try {
         result = asCastedObject(object) != null;
      } catch (OseeCoreException ex) {
         OseeLog.log(DslUiIntegrationConstants.class, Level.SEVERE, ex);
      }
      return result;
   }

   @Override
   public ArtifactProxy asCastedObject(Object object) {
      XArtifactProxy proxy = null;
      if (object instanceof Artifact) {
         final Artifact artifact = (Artifact) object;
         proxy = new XArtifactProxy(artifact);
      } else if (object instanceof BranchId) {
         BranchId branch = (BranchId) object;
         final Artifact artifact = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(branch);
         proxy = new XArtifactProxy(artifact);
      }
      return proxy;
   }

   private final class XArtifactProxy implements ArtifactProxy {
      private final Artifact self;

      public XArtifactProxy(Artifact self) {
         this.self = self;
      }

      @Override
      public String getGuid() {
         return self.getGuid();
      }

      @Override
      public ArtifactType getArtifactType() {
         return self.getArtifactType();
      }

      @Override
      public boolean isAttributeTypeValid(AttributeTypeId attributeType) {
         return self.isAttributeTypeValid(attributeType);
      }

      @Override
      public Collection<RelationType> getValidRelationTypes() {
         return self.getValidRelationTypes();
      }

      @Override
      public ArtifactToken getObject() {
         return self;
      }

      @Override
      public Collection<ArtifactProxy> getHierarchy() {
         Collection<ArtifactProxy> hierarchy = new HashSet<>();
         try {
            Artifact artifactPtr = self.getParent();
            while (artifactPtr != null) {
               if (!hierarchy.add(new XArtifactProxy(artifactPtr))) {
                  OseeLog.log(DslUiIntegrationConstants.class, Level.SEVERE,
                     String.format("Cycle detected with artifact: %s", artifactPtr));
                  return Collections.emptyList();
               }
               artifactPtr = artifactPtr.getParent();
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(DslUiIntegrationConstants.class, Level.SEVERE, ex);
         }
         return hierarchy;
      }

      @Override
      public boolean matches(Id... identities) {
         return self.matches(identities);
      }

      @Override
      public BranchId getBranch() {
         return self.getBranch();
      }

      @Override
      public IOseeBranch getBranchToken() {
         return self.getBranchToken();
      }

      @Override
      public String getName() {
         return self.getName();
      }

      @Override
      public int hashCode() {
         return self.hashCode();
      }

      @Override
      public boolean equals(Object obj) {
         return self.equals(obj);
      }

      @Override
      public String toString() {
         return self.toString();
      }

      @Override
      public Long getId() {
         return self.getId();
      }
   }
}
