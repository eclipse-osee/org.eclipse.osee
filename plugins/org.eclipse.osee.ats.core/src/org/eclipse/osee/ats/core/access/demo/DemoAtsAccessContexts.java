/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.access.demo;

import org.eclipse.osee.framework.core.access.FrameworkAccessContexts;
import org.eclipse.osee.framework.core.data.AccessContextToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;

/**
 * @author Donald G. Dunne
 */
public class DemoAtsAccessContexts extends FrameworkAccessContexts {

   private DemoAtsAccessContexts() {
      // do nothing
   }

   public static void register() {
      new DemoDefaultAccessContext();
      new DemoRequirementAccessContext();
      new DemoCodeAccessContext();
      new DemoTestAccessContext();
      new DemoSystemsAccessContext();
      new DemoSubSystemsAccessContext();
   }

   /**
    * Deny editing any Soft, Sys and SubSys requirements in hierarchy including default hierarchy
    *
    * @author Donald G. Dunne
    */
   public static class DemoDefaultAccessContext extends DefaultAccessContext {
      public DemoDefaultAccessContext() {
         this(DemoAtsAccessContextTokens.DEMO_DEFAULT);
      }

      public DemoDefaultAccessContext(AccessContextToken accessToken) {
         super(accessToken);

         // Deny editing artifacts under Software Requirements folder
         denyEditArtifactAndChildrenArtifactTypes(CoreArtifactTokens.SoftwareRequirementsFolder, //
            CoreArtifactTypes.Artifact);
         // Deny editing relations under Software Requirements folder where Soft Req on both sides
         denyEditArtifactAndChildrenRelationTypes(CoreArtifactTokens.SoftwareRequirementsFolder,
            CoreRelationTypes.DefaultHierarchical, CoreArtifactTypes.AbstractSoftwareRequirement);

         // Deny editing artifacts under System Requirements folder
         denyEditArtifactAndChildrenArtifactTypes(CoreArtifactTokens.SystemRequirementsFolder, //
            CoreArtifactTypes.Artifact);
         // Deny editing relations under System Requirements folder where Sys Req on both sides
         denyEditArtifactAndChildrenRelationTypes(CoreArtifactTokens.SystemRequirementsFolder,
            CoreRelationTypes.DefaultHierarchical, CoreArtifactTypes.AbstractSystemRequirement);

         // Deny editing artifacts under SubSystem Requirements folder
         denyEditArtifactAndChildrenArtifactTypes(CoreArtifactTokens.SubSystemRequirementsFolder, //
            CoreArtifactTypes.Artifact);
         // Deny editing relations under SubSystem Requirements folder where SubSys Req on both sides
         denyEditArtifactAndChildrenRelationTypes(CoreArtifactTokens.SubSystemRequirementsFolder,
            CoreRelationTypes.DefaultHierarchical, CoreArtifactTypes.AbstractSubsystemRequirement);

      }
   }

   public static class DemoRequirementAccessContext extends DemoDefaultAccessContext {
      public DemoRequirementAccessContext() {
         this(DemoAtsAccessContextTokens.DEMO_REQUIREMENT_CONTEXT);
      }

      public DemoRequirementAccessContext(AccessContextToken accessToken) {
         super(accessToken);

         allowEditArtifactAndChildrenArtifactTypes(CoreArtifactTokens.SoftwareRequirementsFolder, //
            CoreArtifactTypes.Artifact);

         allowEditArtifactType(CoreArtifactTypes.AbstractSoftwareRequirement);

         allowEditArtifactAndChildrenRelationTypes(CoreArtifactTokens.SubSystemRequirementsFolder,
            CoreRelationTypes.RequirementTrace);

         allowEditRelationType(CoreRelationTypes.Design);
      }
   }

   public static class DemoTestAccessContext extends DemoDefaultAccessContext {
      public DemoTestAccessContext() {
         this(DemoAtsAccessContextTokens.DEMO_TEST_CONTEXT);
      }

      public DemoTestAccessContext(AccessContextToken accessToken) {
         super(accessToken);

         allowEditAttributeTypeOfArtifactType(CoreAttributeTypes.QualificationMethod, //
            CoreArtifactTypes.AbstractSoftwareRequirement);

         allowEditRelationType(CoreRelationTypes.Verification);

         allowEditRelationType(CoreRelationTypes.Uses);
      }
   }

   public static class DemoCodeAccessContext extends DemoDefaultAccessContext {
      public DemoCodeAccessContext() {
         this(DemoAtsAccessContextTokens.DEMO_CODE_CONTEXT);
      }

      public DemoCodeAccessContext(AccessContextToken accessToken) {
         super(accessToken);

         allowEditArtifactAndChildrenRelationTypes(CoreArtifactTokens.SoftwareRequirementsFolder, //
            CoreRelationTypes.Allocation, CoreArtifactTypes.AbstractSoftwareRequirement);

      }
   }

   public static class DemoSystemsAccessContext extends DemoDefaultAccessContext {
      public DemoSystemsAccessContext() {
         this(DemoAtsAccessContextTokens.DEMO_SYSTEMS_CONTEXT);
      }

      public DemoSystemsAccessContext(AccessContextToken accessToken) {
         super(accessToken);

         allowEditArtifactAndChildrenArtifactTypes(CoreArtifactTokens.SystemRequirementsFolder, //
            CoreArtifactTypes.Artifact);

         allowEditArtifactAndChildrenRelationTypes(CoreArtifactTokens.SubSystemRequirementsFolder,
            CoreRelationTypes.RequirementTrace);

         allowEditRelationType(CoreRelationTypes.Design);
      }
   }

   public static class DemoSubSystemsAccessContext extends DemoDefaultAccessContext {
      public DemoSubSystemsAccessContext() {
         this(DemoAtsAccessContextTokens.DEMO_SUBSYSTEMS_CONTEXT);
      }

      public DemoSubSystemsAccessContext(AccessContextToken accessToken) {
         super(accessToken);

         allowEditArtifactAndChildrenArtifactTypes(CoreArtifactTokens.SubSystemRequirementsFolder, //
            CoreArtifactTypes.Artifact);

         allowEditRelationType(CoreRelationTypes.Design);
      }
   }

}
