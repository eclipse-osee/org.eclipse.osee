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

import java.io.FileWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.OseeData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.widgets.builder.XWidgetBuilder;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetRendererItem;
import org.eclipse.swt.program.Program;

/**
 * BLAM to extract the contents of an Artifact's attribute to a text editor. This BLAM can be used to extract Word ML
 * content from an Artifact for publishing debug.
 *
 * @author Loren K. Ashley
 */

public class PublishingAttributeContentBlam extends AbstractBlam {

   /**
    * Description string for the BLAM
    */

   private static String blamDescription = "Extracts the contents of an Artifact's attribute to a text editor.";

   /**
    * Name string for the BLAM
    */

   private static String blamName = "Publishing Attribute Content BLAM";

   /**
    * Artifact identifier entry error message prefix.
    */

   private static String messageArtifactIdentifierMustEnterPrefix = "Must enter Artifact Identifier as ";

   /**
    * BLAM error message for an invalid attribute identifier.
    */

   private static String messageAttributeIdentifierNotValidForArtifact =
      "Attribute Identifier is not valid for the type of Artifact.";

   /**
    * Message for attribute type identifier entry errors.
    */

   private static final String messageAttributeTypeIdentifierMustEnter =
      "Must enter Attribute Type Identifier as a long greater than zero.";

   /**
    * User message title for a general BLAM failure.
    */

   private static String messageBlamFailure = blamName + " failed";

   /**
    * Message for branch identifier entry errors.
    */

   private static final String messageBranchIdentifierMustEnter =
      "Must enter Branch Identifier as a long greater than zero.";

   /**
    * Artifact identifier entry error message suffix for short identifiers.
    */

   private static String messageId32Positive = "an int greater than zero";

   /**
    * Artifact identifier entry error message suffix for long identifiers.
    */

   private static String messageId64Positive = "a long greater than zero";

   /**
    * BLAM XWidget variable name and title for the template selection parameter &quot;Artifact Identifier&quot;.
    */

   private static String variableArtifactIdentifier = "Artifact Identifier";

   /**
    * BLAM XWidget variable name and title for the template selection parameter &quot;Presentation Type&quot;.
    */

   private static String variableAttributeTypeIdentifier = "Attribute Type Identifier";

   /**
    * BLAM XWidget variable name and title for the template selection parameter &quot;Branch Identifier&quot;.
    */

   private static String variableBranchIdentifier = "Branch Identifier";

   /**
    * Message built based upon {@link #useLongIds} toggle for artifact identifier entry errors.
    */

   private final String messageArtifactIdentifierMustEnter;

   /**
    * Flag to indicate when 64 bit identifiers are enabled.
    */

   private final boolean useLongIds;

   /**
    * Creates a new {@link PublishingAttributeContentBlam} instance for extracting the contents of an Artifact's
    * Attribute to a text editor.
    */

   public PublishingAttributeContentBlam() {
      super(PublishingAttributeContentBlam.blamName, PublishingAttributeContentBlam.blamDescription, null);

      this.useLongIds = ArtifactToken.USE_LONG_IDS;

      //@formatter:off
      this.messageArtifactIdentifierMustEnter =
         this.useLongIds
            ? PublishingAttributeContentBlam.messageArtifactIdentifierMustEnterPrefix
                 .concat( PublishingAttributeContentBlam.messageId64Positive )
            : PublishingAttributeContentBlam.messageArtifactIdentifierMustEnterPrefix
                 .concat( PublishingAttributeContentBlam.messageId32Positive );
      //@formatter:off
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(XNavigateItem.DEFINE_HEALTH);
   }

   /**
    * Reads an identifier from a BLAM variable. It is parsed as a 64 bit long when the member {@link #useLongIds} is
    * <code>true</code>. Otherwise, it is parsed as a 32 bit integer.
    *
    * @param blamVariable the name of the BLAM variable to be read.
    * @param mustEnterMessage user message if the variable is not a valid value.
    * @param forceLong when <code>true</code> the variable is read as a long.
    * @param message errors parsing the BLAM variable are appended to this {@link StringBuilder}.
    * @return on success the parsed identifier as a <code>long</code> greater than or equal to 0; otherwise, -2.
    */

   private long getIdentifier(String blamVariable, String mustEnterMessage, boolean forceLong, StringBuilder message) {
      var identifierString = this.variableMap.getString(blamVariable);

      if (Objects.isNull(identifierString)) {
         message.append(mustEnterMessage).append("\n");
         return -2l;
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

   /**
    * {@inheritDoc}
    */

   @Override
   public List<XWidgetRendererItem> getXWidgetItems() {
      //@formatter:off
      return
         new XWidgetBuilder()
                .andWidget( PublishingAttributeContentBlam.variableBranchIdentifier,        "XText" ).endWidget()
                .andWidget( PublishingAttributeContentBlam.variableArtifactIdentifier,      "XText" ).endWidget()
                .andWidget( PublishingAttributeContentBlam.variableAttributeTypeIdentifier, "XText" ).endWidget()
                .getItems();
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {

      this.variableMap = variableMap;

      try {
         var message = new StringBuilder(1024);

         //@formatter:off
         var branchIdentifier =
            this.getIdentifier
               (
                  PublishingAttributeContentBlam.variableBranchIdentifier,
                  PublishingAttributeContentBlam.messageBranchIdentifierMustEnter,
                  true,
                  message
               );

         var artifactIdentifier =
            this.getIdentifier
               (
                  PublishingAttributeContentBlam.variableArtifactIdentifier,
                  this.messageArtifactIdentifierMustEnter,
                  false,
                  message
               );

         var attributeTypeIdentifier =
            this.getIdentifier
               (
                  PublishingAttributeContentBlam.variableAttributeTypeIdentifier,
                  PublishingAttributeContentBlam.messageAttributeTypeIdentifierMustEnter,
                  true,
                  message
               );
         //@formatter:on

         if (message.length() > 0) {
            AWorkbench.popup(message.toString());
            return;
         }

         var artifact = ArtifactQuery.getArtifactFromId(artifactIdentifier, BranchId.valueOf(branchIdentifier));

         if (!artifact.isAttributeTypeValid(AttributeTypeId.valueOf(attributeTypeIdentifier))) {
            AWorkbench.popup(PublishingAttributeContentBlam.messageAttributeIdentifierNotValidForArtifact);
            return;
         }

         var attributeList = artifact.getAttributes(AttributeTypeId.valueOf(attributeTypeIdentifier));

         //@formatter:off
         var attributeExtractionMessage =
            attributeList.stream()
               .map
                  (
                     ( attribute ) ->
                     {
                        var value = attribute.getValue();
                        if( value instanceof String ) {
                           return (String) value;
                        }
                        return attribute.toString();
                     }
                  )
               .collect(Collectors.joining("\n\n"));
         //@formatter:on

         //@formatter:off
         var fileName =
            new StringBuilder( 1024 )
               .append( "PUBLISHING_ATTRIBUTE_CONTENT_BLAM_" )
               .append( artifactIdentifier )
               .append( "-" )
               .append( branchIdentifier )
               .append( "-" )
               .append( attributeTypeIdentifier )
               .append( "-" )
               .append( Lib.getDateTimeString() )
               .append( ".txt" )
               .toString()
               ;
         //@formatter:off

         var file = OseeData.getFile(fileName);

         try( var fileWriter = new FileWriter(file) ) {
            fileWriter.write(attributeExtractionMessage);
         }

         Program.launch(file.getAbsolutePath());

      } catch (Exception e) {
         //@formatter:off
         AWorkbench.popup
            (
               new Message()
                      .title( PublishingAttributeContentBlam.messageBlamFailure )
                      .reasonFollows( e )
                      .toString()
               );
         //@formatter:on
      }
   }

}

/* EOF */