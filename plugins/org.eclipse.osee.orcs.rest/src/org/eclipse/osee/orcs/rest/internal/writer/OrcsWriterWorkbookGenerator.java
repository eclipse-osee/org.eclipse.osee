/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.internal.writer;

import java.io.IOException;
import java.io.Writer;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ISheetWriter;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.rest.model.writer.reader.OwArtifactToken;
import org.eclipse.osee.orcs.rest.model.writer.reader.OwArtifactType;
import org.eclipse.osee.orcs.rest.model.writer.reader.OwAttributeType;
import org.eclipse.osee.orcs.rest.model.writer.reader.OwBranch;
import org.eclipse.osee.orcs.rest.model.writer.reader.OwCollector;
import org.eclipse.osee.orcs.rest.model.writer.reader.OwRelationType;

/**
 * @author Donald G. Dunne
 */
public class OrcsWriterWorkbookGenerator {

   // @formatter:off
   private final String[] instructionHeadings = {"Instructions and Settings", " "};
   private final String[] deleteSheetHeadings = {"Artifact Token - List Artifact Tokens for artifacts to delete"};
   private final String[] branchSheetHeadings = {"Branch Token - Copy one to Settings Sheet"};
   private final String[] artifactTokenSheetHeadings = {"Artifact Token - Copy for relations columns"};
   private final String[] artifactTypeSheetHeadings = {"Artifact Type - Copy for Artifact Type Columns"};
   private final String[] attributeTypeSheetHeadings = {"Attribute Type - Copy for Attribute Columns"};
   private final String[] relationTypeSheetHeadings = {"Relation Type - Copy for Relation Columns"};
   private final OwCollector collector;
   private final OrcsApi orcsApi;
   // @formatter:on

   public OrcsWriterWorkbookGenerator(OwCollector collector, OrcsApi orcsApi) {
      this.collector = collector;
      this.orcsApi = orcsApi;
   }

   public void runOperation(OrcsApi providedOrcs, Writer providedWriter) throws IOException {
      ISheetWriter writer = new ExcelXmlWriter(providedWriter);
      createInstructionSheet(writer);
      createCreateSheet(writer);
      createUpdateSheet(writer);
      createDeleteSheet(writer);
      createBranchSheet(writer);
      createArtifactTokenSheet(writer);
      createArtifactTypeSheet(writer);
      createAttributeTypeSheet(writer);
      createRelationTypeSheet(writer);
      writer.endWorkbook();
   }

   private void createInstructionSheet(ISheetWriter writer) throws IOException {
      writer.startSheet(OrcsWriterUtil.INSTRUCTIONS_AND_SETTINGS_SHEET_NAME, instructionHeadings.length);
      writer.writeRow((Object[]) instructionHeadings);
      writer.writeCell(null);
      writer.endRow();
      writer.writeCell("Instructions");
      writer.endRow();
      for (String line : collector.getInstructions().split("\n")) {
         writer.writeCell(line);
         writer.endRow();
      }
      writer.writeCell(null);
      writer.endRow();

      // create settings
      writer.writeCell("Settings");
      writer.endRow();
      writer.writeCell(OrcsWriterUtil.BRANCH_TOKEN_SETTING);
      writer.writeCell(OwFactory.createBranchToken(CoreBranches.COMMON).getData());
      writer.endRow();
      writer.writeCell(OrcsWriterUtil.AS_USER_ID_SETTING);
      writer.writeCell(SystemUser.OseeSystem.getUserId());
      writer.endRow();
      writer.writeCell(OrcsWriterUtil.PERSIST_COMMENT_SETTING);
      writer.writeCell("Enter Persist Comment Here");
      writer.endRow();

      writer.endSheet();
   }

   private void createCreateSheet(ISheetWriter writer) throws IOException {
      // @formatter:off
      String[] createSheetHeadings = {null, "New Art Token (for refrence in relations, if needed)", "Name", "Attribute", "Attribute", "Relation"};
      // @formatter:on
      writer.startSheet(OrcsWriterUtil.CREATE_SHEET_NAME, createSheetHeadings.length);
      writer.writeRow((Object[]) createSheetHeadings);

      // row 2
      writer.writeCell("Artifact Type Token");
      writer.writeCell(null);
      writer.writeCell(CoreAttributeTypes.Name.getName() + " (required)");
      writer.writeCell(OwFactory.createAttributeType(CoreAttributeTypes.StaticId).getData());
      writer.writeCell(OwFactory.createAttributeType(CoreAttributeTypes.Partition).getData());
      writer.writeCell(OwFactory.createRelationType(orcsApi, CoreRelationTypes.DefaultHierarchical_Parent).getData());
      writer.endRow();

      // row 3 - New Folder rooted at Default Hierarchy Root
      writer.writeCell(OwFactory.createArtifactType(CoreArtifactTypes.Folder).getData());
      Long folderUuid = Lib.generateArtifactIdAsInt();
      OwArtifactToken folderToken = OwFactory.createArtifactToken("Orcs Writer Import Folder", folderUuid);
      writer.writeCell(folderToken.getData());
      writer.writeCell("Orcs Writer Import Folder");
      writer.writeCell(null);
      writer.writeCell(null);
      writer.writeCell(OwFactory.createArtifactToken(CoreArtifactTokens.DefaultHierarchyRoot).getData());
      writer.endRow();

      // row 4 - New Software Requirement 1 under folder
      writer.writeCell(OwFactory.createArtifactType(CoreArtifactTypes.SoftwareRequirement).getData());
      writer.writeCell(null);
      writer.writeCell("Software Requirement 1");
      writer.writeCell("static id field 1");
      writer.writeCell("Communication");
      writer.writeCell(folderToken.getData());
      writer.endRow();

      // row 5 - New Software Requirement 2 under folder
      writer.writeCell(OwFactory.createArtifactType(CoreArtifactTypes.SoftwareRequirement).getData());
      writer.writeCell(null);
      writer.writeCell("Software Requirement 2");
      writer.writeCell("static id field 2");
      writer.writeCell("Flight Control");
      writer.writeCell(folderToken.getData());
      writer.endRow();

      writer.endSheet();
   }

   private void createUpdateSheet(ISheetWriter writer) throws IOException {
      // @formatter:off
      String[] updateSheetHeadings = {null, "Name", "Attribute", "Attribute"};
      // @formatter:on
      writer.startSheet(OrcsWriterUtil.UPDATE_SHEET_NAME, updateSheetHeadings.length);
      writer.writeRow((Object[]) updateSheetHeadings);

      // row 2
      writer.writeCell("Artifact Token");
      writer.writeCell(CoreAttributeTypes.Name.getName());
      writer.writeCell(OwFactory.createAttributeType(CoreAttributeTypes.StaticId).getData());
      writer.writeCell(OwFactory.createAttributeType(CoreAttributeTypes.Annotation).getData());
      writer.endRow();

      // row 3 - Add static id and annotation to User Groups folder
      writer.writeCell(OwFactory.createArtifactToken(CoreArtifactTokens.UserGroups).getData());
      writer.writeCell(CoreArtifactTokens.UserGroups.getName());
      writer.writeCell("test static id");
      writer.writeCell("test annotation");
      writer.endRow();

      writer.endSheet();
   }

   private void createDeleteSheet(ISheetWriter writer) throws IOException {
      writer.startSheet(OrcsWriterUtil.DELETE_SHEET_NAME, deleteSheetHeadings.length);
      writer.writeRow((Object[]) deleteSheetHeadings);
      writer.endSheet();
   }

   private void createBranchSheet(ISheetWriter writer) throws IOException {
      writer.startSheet("Branch Token", branchSheetHeadings.length);
      writer.writeRow((Object[]) branchSheetHeadings);
      for (OwBranch token : collector.getBranches()) {
         writer.writeCell(token.getData());
         writer.endRow();
      }
      writer.endSheet();
   }

   private void createArtifactTokenSheet(ISheetWriter writer) throws IOException {
      writer.startSheet("Artifact Token", artifactTokenSheetHeadings.length);
      writer.writeRow((Object[]) artifactTokenSheetHeadings);
      for (OwArtifactToken token : collector.getArtTokens()) {
         writer.writeCell(token.getData());
         writer.endRow();
      }
      writer.endSheet();
   }

   private void createArtifactTypeSheet(ISheetWriter writer) throws IOException {
      writer.startSheet("Artifact Types", artifactTypeSheetHeadings.length);
      writer.writeRow((Object[]) artifactTypeSheetHeadings);
      for (OwArtifactType type : collector.getArtTypes()) {
         writer.writeCell(type.getData());
         writer.endRow();
      }
      writer.endSheet();
   }

   private void createAttributeTypeSheet(ISheetWriter writer) throws IOException {
      writer.startSheet("Attribute Types", attributeTypeSheetHeadings.length);
      writer.writeRow((Object[]) attributeTypeSheetHeadings);
      for (OwAttributeType type : collector.getAttrTypes()) {
         writer.writeCell(type.getData());
         writer.endRow();
      }
      writer.endSheet();
   }

   private void createRelationTypeSheet(ISheetWriter writer) throws IOException {
      writer.startSheet("Relation Types", relationTypeSheetHeadings.length);
      writer.writeRow((Object[]) relationTypeSheetHeadings);
      for (OwRelationType relation : collector.getRelTypes()) {
         writer.writeCell(relation.getData());
         writer.endRow();
      }
      writer.endSheet();
   }

}
