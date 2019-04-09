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
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
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
   private static String EDIT_RENDERER_OPTIONS =
      "{\"ElementType\" : \"Artifact\", \"OutliningOptions\" : [ {\"Outlining\" : true, \"RecurseChildren\" : false, \"HeadingAttributeType\" : \"Name\", \"ArtifactName\" : \"Default\", \"OutlineNumber\" : \"\" }], \"AttributeOptions\" : [{\"AttrType\" : \"Word Template Content\",  \"Label\" : \"\", \"FormatPre\" : \"\", \"FormatPost\" : \"\"}]}";
   private static String RECURSIVE_RENDERER_OPTIONS =
      "{\"ElementType\" : \"Artifact\", \"OutliningOptions\" : [ {\"Outlining\" : true, \"RecurseChildren\" : true, \"HeadingAttributeType\" : \"Name\", \"ArtifactName\" : \"Default\", \"OutlineNumber\" : \"\" }], \"AttributeOptions\" : [{\"AttrType\" : \"*\",  \"Label\" : \"\", \"FormatPre\" : \"\", \"FormatPost\" : \"\"}]}";

   public CreateSystemBranches(OrcsApi orcsApi, EventAdmin eventAdmin) {
      this.orcsApi = orcsApi;
      this.eventAdmin = eventAdmin;
      txFactory = orcsApi.getTransactionFactory();
      query = orcsApi.getQueryFactory().fromBranch(COMMON);
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
      ArtifactReadable root = query.andIsHeirarchicalRootArtifact().getResults().getExactlyOne();

      ArtifactId oseeConfig = query.andId(CoreArtifactTokens.OseeConfiguration).getArtifactOrSentinal();
      if (oseeConfig.isInvalid()) {
         oseeConfig = tx.createArtifact(root, CoreArtifactTokens.OseeConfiguration);
      }

      ArtifactId userGroupsFolder = tx.createArtifact(oseeConfig, CoreArtifactTokens.UserGroups);
      ArtifactId everyOne = tx.createArtifact(userGroupsFolder, CoreUserGroups.Everyone);
      tx.setSoleAttributeValue(everyOne, CoreAttributeTypes.DefaultGroup, true);

      tx.createArtifact(userGroupsFolder, CoreUserGroups.OseeAdmin);
      tx.createArtifact(userGroupsFolder, CoreUserGroups.OseeAccessAdmin);
      tx.commit();

      oseeConfig = query.andId(CoreArtifactTokens.OseeConfiguration).getArtifactOrSentinal();
      tx = txFactory.createTransaction(COMMON, SystemUser.OseeSystem, "Add Common branch artifacts");
      orcsApi.getAdminOps().createUsers(tx, SystemUser.values());

      ArtifactId globalPreferences = tx.createArtifact(oseeConfig, CoreArtifactTokens.GlobalPreferences);
      tx.setSoleAttributeValue(globalPreferences, CoreAttributeTypes.GeneralStringData, JSON_ATTR_VALUE);

      tx.createArtifact(oseeConfig, CoreArtifactTokens.XViewerCustomization);

      ArtifactId documentTemplateFolder = tx.createArtifact(oseeConfig, CoreArtifactTokens.DocumentTemplates);

      createWordTemplates(tx, documentTemplateFolder);

      createDataRights(tx, documentTemplateFolder);

      ArtifactId typesAccessFolder = createOrcsTypesArtifacts(typeModel, oseeConfig);

      addFrameworkAccessModel(tx, typesAccessFolder);

      tx.commit();
   }

   private void createWordTemplates(TransactionBuilder tx, ArtifactId documentTemplateFolder) {
      ArtifactId templateArtWe =
         tx.createArtifact(documentTemplateFolder, CoreArtifactTypes.RendererTemplate, "WordEditTemplate");

      tx.setSoleAttributeValue(templateArtWe, CoreAttributeTypes.RendererOptions, EDIT_RENDERER_OPTIONS);
      tx.setSoleAttributeValue(templateArtWe, CoreAttributeTypes.WholeWordContent,
         OseeInf.getResourceContents("templates/Word Edit Template.xml", getClass()));
      tx.createAttribute(templateArtWe, CoreAttributeTypes.TemplateMatchCriteria,
         "org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer SPECIALIZED_EDIT");
      tx.createAttribute(templateArtWe, CoreAttributeTypes.TemplateMatchCriteria,
         "org.eclipse.osee.framework.ui.skynet.render.TisRenderer SPECIALIZED_EDIT");
      tx.createAttribute(templateArtWe, CoreAttributeTypes.TemplateMatchCriteria,
         "org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer MERGE");
      tx.createAttribute(templateArtWe, CoreAttributeTypes.TemplateMatchCriteria,
         "org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer MERGE_EDIT");

      ArtifactId templateArtPrev =
         tx.createArtifact(documentTemplateFolder, CoreArtifactTypes.RendererTemplate, "PreviewAll");
      tx.setSoleAttributeValue(templateArtPrev, CoreAttributeTypes.WholeWordContent,
         OseeInf.getResourceContents("templates/PREVIEW_ALL.xml", getClass()));
      tx.createAttribute(templateArtPrev, CoreAttributeTypes.TemplateMatchCriteria,
         "org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer PREVIEW PREVIEW_ARTIFACT");
      tx.createAttribute(templateArtPrev, CoreAttributeTypes.TemplateMatchCriteria,
         "org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer PREVIEW");
      tx.createAttribute(templateArtPrev, CoreAttributeTypes.TemplateMatchCriteria,
         "org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer DIFF");

      // must match name used in client integration tests
      ArtifactId templateArtPar =
         tx.createArtifact(documentTemplateFolder, CoreArtifactTypes.RendererTemplate, "PREVIEW_ALL_RECURSE");
      tx.setSoleAttributeValue(templateArtPar, CoreAttributeTypes.RendererOptions, RECURSIVE_RENDERER_OPTIONS);
      tx.setSoleAttributeValue(templateArtPar, CoreAttributeTypes.WholeWordContent,
         OseeInf.getResourceContents("templates/PREVIEW_ALL_RECURSE.xml", getClass()));
      tx.createAttribute(templateArtPar, CoreAttributeTypes.TemplateMatchCriteria,
         "org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer PREVIEW PREVIEW_WITH_RECURSE");
   }

   private void createDataRights(TransactionBuilder tx, ArtifactId documentTemplateFolder) {
      ArtifactId dataRightsArt = tx.createArtifact(documentTemplateFolder, CoreArtifactTokens.DataRightsFooters);
      try {
         Thread.sleep(5000);
         String resourceContents = OseeInf.getResourceContents("Unspecified.xml", getClass());
         System.out.println("Resource Contents: " + resourceContents);
      } catch (Exception ex) {
         System.out.println("Exception: " + ex);
      }

      tx.createAttribute(dataRightsArt, CoreAttributeTypes.GeneralStringData,
         OseeInf.getResourceContents("Unspecified.xml", getClass()));
      tx.createAttribute(dataRightsArt, CoreAttributeTypes.GeneralStringData,
         OseeInf.getResourceContents("Default.xml", getClass()));
      tx.createAttribute(dataRightsArt, CoreAttributeTypes.GeneralStringData,
         OseeInf.getResourceContents("GovernmentPurposeRights.xml", getClass()));
      tx.createAttribute(dataRightsArt, CoreAttributeTypes.GeneralStringData,
         OseeInf.getResourceContents("RestrictedRights.xml", getClass()));
   }

   private void addFrameworkAccessModel(TransactionBuilder tx, ArtifactId typesAccessFolder) {
      InputStream inputStream = OseeInf.getResourceAsStream("access/OseeAccess_FrameworkAccess.osee", getClass());

      try (InputStream stream = new BufferedInputStream(inputStream)) {
         ArtifactId accessModel = tx.createArtifact(typesAccessFolder, CoreArtifactTokens.FrameworkAccessModel);
         tx.setSoleAttributeFromStream(accessModel, CoreAttributeTypes.GeneralStringData, stream);
      } catch (IOException ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   private ArtifactId createOrcsTypesArtifacts(String typeModel, ArtifactId oseeConfig) {
      TransactionBuilder tx = txFactory.createTransaction(COMMON, SystemUser.OseeSystem, "Add Types to Common Branch");
      ArtifactId typesFolder =
         query.andId(CoreArtifactTokens.OseeTypesAndAccessFolder).getResults().getAtMostOneOrDefault(
            ArtifactReadable.SENTINEL);
      if (typesFolder.isInvalid()) {
         typesFolder = tx.createArtifact(oseeConfig, CoreArtifactTokens.OseeTypesAndAccessFolder);
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
      return typesFolder;
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