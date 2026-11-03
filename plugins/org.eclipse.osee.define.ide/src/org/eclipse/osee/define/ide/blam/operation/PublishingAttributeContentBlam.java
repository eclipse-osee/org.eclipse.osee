/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.define.ide.blam.operation;

import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.OseeData;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.util.CoreImage;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.swt.program.Program;
import org.osgi.service.component.annotations.Component;

/**
 * BLAM to extract the contents of an Artifact's attribute to a text editor. This BLAM can be used to extract Word ML
 * content from an Artifact for publishing debug.
 *
 * @author Loren K. Ashley
 */

@Component(service = AbstractBlam.class, immediate = true)
public class PublishingAttributeContentBlam extends AbstractBlam {

   private static final String BLAM_NAME = "Publishing Attribute Content";
   private static final String MESSAGE_BLAM_FAILURE = BLAM_NAME + " failed";
   private static final String BLAM_DESC = "Extracts the contents of an Artifact's attribute to a text editor.";
   private static final String MESSAGE_ARTIFACT_IDENTIFIER_MUST_ENTER_PREFIX = "Must enter Artifact Identifier as ";
   private static final String MESSAGE_ATTRIBUTE_IDENTIFIER_NOT_VALID_FOR_ARTIFACT =
      "Attribute Identifier is not valid for the type of Artifact.";
   private static final String MESSAGE_ATTRIBUTE_TYPE_IDENTIFIER_MUST_ENTER =
      "Must enter Attribute Type Identifier as a long greater than zero.";
   private static final String MESSAGE_BRANCH_IDENTIFIER_MUST_ENTER =
      "Must enter Branch Identifier as a long greater than zero.";
   private static final String MESSAGE_ID32_POSITIVE = "an int greater than zero";
   private static final String MESSAGE_ID64_POSITIVE = "a long greater than zero";
   private static final String MESSAGE_TRANSACTION_IDENTIFIER_MUST_ENTER =
      "Must enter Transaction Identifier as a long greater than zero or as \"SENTINEL\".";
   static final String VARIABLE_ARTIFACT_IDENTIFIER = "Artifact Identifier";
   static final String VARIABLE_ATTRIBUTE_TYPE_IDENTIFIER = "Attribute Type Identifier";
   static final String VARIABLE_BRANCH_IDENTIFIER = "Branch Identifier";
   static final String VARIABLE_TRANSACTION_IDENTIFIER = "Transaction Identifier";

   private final String messageArtifactIdentifierMustEnter;
   private final boolean useLongIds;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public PublishingAttributeContentBlam() {
      super(BLAM_NAME, CoreImage.PUBLISH);

      this.useLongIds = ArtifactToken.USE_LONG_IDS;

      this.messageArtifactIdentifierMustEnter = this.useLongIds ? MESSAGE_ARTIFACT_IDENTIFIER_MUST_ENTER_PREFIX.concat(
         MESSAGE_ID64_POSITIVE) : MESSAGE_ARTIFACT_IDENTIFIER_MUST_ENTER_PREFIX.concat(MESSAGE_ID32_POSITIVE);
   }

   //////////////////////////////////////////
   // Widgets
   //////////////////////////////////////////

   @Override
   public List<XWidgetData> getXWidgetItems() {
      createWidgetBuilder();
      wb.andTextWidget(VARIABLE_BRANCH_IDENTIFIER);
      wb.andTextWidget(VARIABLE_ARTIFACT_IDENTIFIER);
      wb.andTextWidget(VARIABLE_ATTRIBUTE_TYPE_IDENTIFIER);
      wb.andTextWidget(VARIABLE_TRANSACTION_IDENTIFIER);
      return wb.getXWidgetDatas();
   }

   //////////////////////////////////////////
   // Operation
   //////////////////////////////////////////

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {

      this.variableMap = variableMap;

      try {
         StringBuilder message = new StringBuilder(1024);

         long branchIdentifier =
            this.getIdentifier(VARIABLE_BRANCH_IDENTIFIER, MESSAGE_BRANCH_IDENTIFIER_MUST_ENTER, true, message);

         long artifactIdentifier =
            this.getIdentifier(VARIABLE_ARTIFACT_IDENTIFIER, this.messageArtifactIdentifierMustEnter, false, message);

         long attributeTypeIdentifier = this.getIdentifier(VARIABLE_ATTRIBUTE_TYPE_IDENTIFIER,
            MESSAGE_ATTRIBUTE_TYPE_IDENTIFIER_MUST_ENTER, true, message);

         long transactionIdentifier = this.getIdentifier(VARIABLE_TRANSACTION_IDENTIFIER,
            MESSAGE_TRANSACTION_IDENTIFIER_MUST_ENTER, true, message);

         if (message.length() > 0) {
            AWorkbench.popup(message.toString());
            return;
         }

         Artifact artifact = (transactionIdentifier == -1L) ? ArtifactQuery.getArtifactFromId(artifactIdentifier,
            BranchId.valueOf(branchIdentifier)) : ArtifactQuery.getHistoricalArtifactFromId(
               ArtifactId.valueOf(artifactIdentifier),
               TransactionToken.valueOf(TransactionId.valueOf(transactionIdentifier),
                  BranchId.valueOf(branchIdentifier)),
               DeletionFlag.EXCLUDE_DELETED);

         if (!artifact.isAttributeTypeValid(AttributeTypeId.valueOf(attributeTypeIdentifier))) {
            AWorkbench.popup(MESSAGE_ATTRIBUTE_IDENTIFIER_NOT_VALID_FOR_ARTIFACT);
            return;
         }

         List<Attribute<Object>> attributeList =
            artifact.getAttributes(AttributeTypeId.valueOf(attributeTypeIdentifier));

         String attributeExtractionMessage = attributeList.stream().map((attribute) -> {
            Object value = attribute.getValue();
            if (value instanceof String) {
               return (String) value;
            }
            return attribute.toString();
         }).collect(Collectors.joining("\n\n"));

         String fileName = String.format("PUBLISHING_ATTRIBUTE_CONTENT_BLAM_%d-%d-%d-%d-%s.txt", artifactIdentifier,
            branchIdentifier, attributeTypeIdentifier, transactionIdentifier, Lib.getDateTimeString());

         File file = OseeData.getFile(fileName);

         try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(attributeExtractionMessage);
         }

         Program.launch(file.getAbsolutePath());

      } catch (Exception e) {
         AWorkbench.popup(new Message().title(MESSAGE_BLAM_FAILURE).reasonFollows(e).toString());
      }
   }

   //////////////////////////////////////////
   // Helpers
   //////////////////////////////////////////

   private long getIdentifier(String blamVariable, String mustEnterMessage, boolean forceLong, StringBuilder message) {

      String identifierString = this.variableMap.getString(blamVariable);

      if (Objects.isNull(identifierString)) {
         message.append(mustEnterMessage).append("\n");
         return -2l;
      }

      if ("SENTINEL".equals(identifierString)) {
         return -1;
      }

      Long identifier;

      try {
         identifier = this.useLongIds || forceLong ? Long.valueOf(identifierString) : Long.valueOf(
            Integer.valueOf(identifierString));
      } catch (Exception e) {
         message.append(mustEnterMessage).append("\n");
         return -2l;
      }

      if (identifier <= 0) {
         message.append(mustEnterMessage).append("\n");
         return -2l;
      }

      return identifier;
   }

   //////////////////////////////////////////
   // Categories & Description
   //////////////////////////////////////////

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(XNavigateItem.DEFINE_HEALTH);
   }

   @Override
   public String getDescriptionUsage() {
      return BLAM_DESC;
   }

}
