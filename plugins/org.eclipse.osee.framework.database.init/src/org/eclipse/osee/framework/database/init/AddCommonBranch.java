/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.framework.database.init;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.util.List;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.data.OrcsTypeSheet;
import org.eclipse.osee.framework.core.data.OrcsTypesData;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.database.init.internal.OseeTypesSetup;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.GlobalXViewerSettings;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.SystemGroup;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * This class creates the common branch and imports the appropriate skynet types. Class should be extended for plugins
 * that require extra skynet types to be added to common.
 *
 * @author Donald G. Dunne
 */
public abstract class AddCommonBranch implements IDbInitializationTask {
   private final boolean initializeRootArtifacts;
   private static final String JSON_ATTR_VALUE = "{ \"WCAFE\" : [" + //
      "{\"TypeId\" : 204509162766372, \"BranchId\" : 1, \"Range\" : [{\"Min\" : 1, \"Max\" : 99}, {\"Min\" : 1001, \"Max\" : 1009}]}," + //
      "{\"TypeId\" : 204509162766372, \"BranchId\" : 61, \"Range\" : [{\"Min\" : 1, \"Max\" : 49}]}," + //
     "{\"TypeId\" : 204509162766372, \"BranchId\" : 714, \"Range\" : [{\"Min\" : 1, \"Max\" : 99}, {\"Min\" : 1001, \"Max\" : 1009}]}," + //
      "{\"TypeId\" : 204509162766373, \"BranchId\" : 1, \"Range\" : [{\"Min\" : 100, \"Max\" : 199}, {\"Min\" : 1100, \"Max\" : 1199}]}," + //
      "{\"TypeId\" : 204509162766373, \"BranchId\" : 61, \"Range\" : [{\"Min\" : 50, \"Max\" : 199}]}," + //
     "{\"TypeId\" : 204509162766373, \"BranchId\" : 714, \"Range\" : [{\"Min\" : 100, \"Max\" : 199}, {\"Min\" : 1100, \"Max\" : 1199}]}," + //
      "{\"TypeId\" : 204509162766374, \"BranchId\" : 1, \"Range\" : [{\"Min\" : 200, \"Max\" : 1000}, {\"Min\" : 1200, \"Max\" : 2000}]}," + //
     "{\"TypeId\" : 204509162766374, \"BranchId\" : 61, \"Range\" : [{\"Min\" : 200, \"Max\" : 1000}, {\"Min\" : 1200, \"Max\" : 2000}]}," + //
     "{\"TypeId\" : 204509162766374, \"BranchId\" : 714, \"Range\" : [{\"Min\" : 200, \"Max\" : 1000}, {\"Min\" : 1200, \"Max\" : 2000}]}," + //
      "{\"TypeId\" : 204509162766370, \"BranchId\" : 1, \"Range\" : [{\"Min\" : 1, \"Max\" : 8191}]}," + //
     "{\"TypeId\" : 204509162766370, \"BranchId\" : 61, \"Range\" : [{\"Min\" : 1, \"Max\" : 8191}]}," + //
     "{\"TypeId\" : 204509162766370, \"BranchId\" : 714, \"Range\" : [{\"Min\" : 1, \"Max\" : 8191}]}," + //
      "{\"TypeId\" : 204509162766371, \"BranchId\" : 1, \"Range\" : [{\"Min\" : 400}]}," + //
     "{\"TypeId\" : 204509162766371, \"BranchId\" : 61, \"Range\" : [{\"Min\" : 400}]}," + //
      "{\"TypeId\" : 204509162766371, \"BranchId\" : 714, \"Range\" : [{\"Min\" : 1}]}]}";

   public AddCommonBranch() {
      this(true);
   }

   public AddCommonBranch(boolean initializeRootArtifacts) {
      this.initializeRootArtifacts = initializeRootArtifacts;
   }

   @Override
   public void run()  {

      if (initializeRootArtifacts) {
         ArtifactTypeManager.addArtifact(CoreArtifactTokens.DefaultHierarchyRoot, CoreBranches.SYSTEM_ROOT).persist(
            getClass().getSimpleName());
         ArtifactTypeManager.addArtifact(CoreArtifactTokens.UniversalGroupRoot, CoreBranches.SYSTEM_ROOT).persist(
            getClass().getSimpleName());

         BranchManager.createTopLevelBranch(CoreBranches.COMMON);

         OseeTypesSetup types = new OseeTypesSetup();
         List<OrcsTypeSheet> sheets = types.getOseeTypeExtensions();
         OrcsTypesData typesData = new OrcsTypesData();
         typesData.getSheets().addAll(sheets);
         ArtifactTypeManager.importOrcsTypes(typesData);

         SkynetTransaction transaction = TransactionManager.createTransaction(COMMON, "Add Common Branch");

         //create everyone group
         Artifact everyonGroup = SystemGroup.Everyone.getArtifact();
         everyonGroup.setSoleAttributeValue(CoreAttributeTypes.DefaultGroup, true);
         everyonGroup.persist(transaction);

         // Create Default Users
         for (UserToken userToken : SystemUser.values()) {
            UserManager.createUser(userToken, transaction);
         }
         // Create Global Preferences artifact that lives on common branch
         Artifact globalPrefArt = OseeSystemArtifacts.createGlobalPreferenceArtifact();
         globalPrefArt.addAttribute(CoreAttributeTypes.GeneralStringData, JSON_ATTR_VALUE);

         globalPrefArt.persist(transaction);

         // Create XViewer Customization artifact that lives on common branch
         GlobalXViewerSettings.createCustomArtifact().persist(transaction);

         // Create OseeAdmin group
         SystemGroup.OseeAdmin.getArtifact().persist(transaction);

         // Need to set some Test Unit Table data
         Artifact art =
            ArtifactQuery.getOrCreate("Bs+PvSVQf3R5EHSTcyQA", CoreArtifactTypes.Artifact, CoreBranches.COMMON);
         art.persist(transaction);

         transaction.execute();
      }
   }
}
