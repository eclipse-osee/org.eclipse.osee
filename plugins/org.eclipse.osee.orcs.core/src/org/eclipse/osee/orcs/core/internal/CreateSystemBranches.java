/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal;

import static org.eclipse.osee.framework.core.data.ApplicabilityToken.BASE;
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.OrcsTypesData;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreTupleTypes;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.util.OseeInf;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.OrcsTopicEvents;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.eclipse.osee.orcs.transaction.TransactionFactory;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

/**
 * @author Ryan D. Brooks
 */
public class CreateSystemBranches {
   private final OrcsApi orcsApi;
   private final EventAdmin eventAdmin;
   private final TransactionFactory txFactory;
   private final QueryBuilder query;

   public CreateSystemBranches(OrcsApi orcsApi, EventAdmin eventAdmin) {
      this.orcsApi = orcsApi;
      this.eventAdmin = eventAdmin;
      txFactory = orcsApi.getTransactionFactory();
      query = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON);
   }

   public void create(String typeModel) {
      orcsApi.getKeyValueOps().putByKey(BASE, BASE.getName());

      populateSystemBranch();

      orcsApi.getBranchOps().createTopLevelBranch(COMMON, SystemUser.OseeSystem);

      populateCommonBranch(typeModel);
   }

   private void populateSystemBranch() {
      TransactionBuilder tx = txFactory.createTransaction(CoreBranches.SYSTEM_ROOT, SystemUser.OseeSystem,
         "Add System Root branch artifacts");
      tx.createArtifact(CoreArtifactTokens.DefaultHierarchyRoot);
      tx.createArtifact(CoreArtifactTokens.UniversalGroupRoot);
      tx.commit();
   }

   private void populateCommonBranch(String typeModel) {
      TransactionBuilder tx = txFactory.createTransaction(COMMON, SystemUser.OseeSystem, "Add Common branch artifacts");
      ArtifactReadable rootArtifact = query.andIsHeirarchicalRootArtifact().getResults().getExactlyOne();

      ArtifactId userGroupsFolder = tx.createArtifact(rootArtifact, CoreArtifactTokens.UserGroups);
      ArtifactId everyOne = tx.createArtifact(userGroupsFolder, CoreArtifactTokens.Everyone);
      tx.setSoleAttributeValue(everyOne, CoreAttributeTypes.DefaultGroup, true);

      tx.createArtifact(CoreArtifactTokens.OseeAdmin);
      tx.createArtifact(CoreArtifactTokens.OseeAccessAdmin);

      orcsApi.getAdminOps().createUsers(tx, SystemUser.values(), query);

      ArtifactId globalPreferences = tx.createArtifact(CoreArtifactTokens.GlobalPreferences);
      tx.setSoleAttributeValue(globalPreferences, CoreAttributeTypes.GeneralStringData, JSON_ATTR_VALUE);

      tx.createArtifact(CoreArtifactTokens.XViewerGlobalCustomization);

      ArtifactId dataRightsArt = tx.createArtifact(CoreArtifactTokens.DataRightsFooters);
      tx.createAttribute(dataRightsArt, CoreAttributeTypes.GeneralStringData,
         OseeInf.getResourceContents("Unspecified.xml", getClass()));
      tx.createAttribute(dataRightsArt, CoreAttributeTypes.GeneralStringData,
         OseeInf.getResourceContents("Default.xml", getClass()));
      tx.createAttribute(dataRightsArt, CoreAttributeTypes.GeneralStringData,
         OseeInf.getResourceContents("GovernmentPurposeRights.xml", getClass()));
      tx.createAttribute(dataRightsArt, CoreAttributeTypes.GeneralStringData,
         OseeInf.getResourceContents("RestrictedRights.xml", getClass()));

      createOrcsTypesArtifacts(typeModel);

      addFrameworkAccessModel(tx);

      tx.commit();
   }

   private void addFrameworkAccessModel(TransactionBuilder tx) {
      InputStream inputStream = OseeInf.getResourceAsStream("access/OseeAccess_FrameworkAccess.osee", getClass());

      try (InputStream stream = new BufferedInputStream(inputStream)) {
         ArtifactId accessModel = tx.createArtifact(CoreArtifactTokens.FrameworkAccessModel);
         tx.setSoleAttributeFromStream(accessModel, CoreAttributeTypes.GeneralStringData, stream);
      } catch (IOException ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   private void createOrcsTypesArtifacts(String typeModel) {
      TransactionBuilder tx = txFactory.createTransaction(COMMON, SystemUser.OseeSystem, "Add Types to Common Branch");
      ArtifactId typesFolder = orcsApi.getQueryFactory().fromBranch(COMMON).andId(
         CoreArtifactTokens.OseeTypesFolder).getResults().getAtMostOneOrDefault(ArtifactReadable.SENTINEL);
      if (typesFolder.isInvalid()) {
         ArtifactId rootArt = orcsApi.getQueryFactory().fromBranch(COMMON).andId(
            CoreArtifactTokens.DefaultHierarchyRoot).getResults().getExactlyOne();
         typesFolder = tx.createArtifact(rootArt, CoreArtifactTokens.OseeTypesFolder);
      }
      ArtifactId types = tx.createArtifact(typesFolder, CoreArtifactTypes.OseeTypeDefinition, "OSEE Types");
      tx.setSoleAttributeValue(types, CoreAttributeTypes.Active, true);
      tx.setSoleAttributeFromString(types, CoreAttributeTypes.UriGeneralStringData, typeModel);
      tx.commit();

      tx = txFactory.createTransaction(COMMON, SystemUser.OseeSystem, "Add OseeTypeDef Tuples to Common Branch");
      for (ArtifactReadable artifact : query.andTypeEquals(CoreArtifactTypes.OseeTypeDefinition).getResults()) {
         tx.addTuple2(CoreTupleTypes.OseeTypeDef, OrcsTypesData.OSEE_TYPE_VERSION,
            artifact.getAttributes(CoreAttributeTypes.UriGeneralStringData).iterator().next());
      }
      tx.commit();

      Event event = new Event(OrcsTopicEvents.DBINIT_IMPORT_TYPES, (Map<String, ?>) null);
      eventAdmin.postEvent(event);
   }

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
}