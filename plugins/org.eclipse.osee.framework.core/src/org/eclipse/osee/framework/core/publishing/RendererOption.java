/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.framework.core.publishing;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Morgan E. Cook
 * @author Loren K. Ashley
 */

public enum RendererOption {

   /*
    * Boolean
    */

   ADD_MERGE_TAG("Add Merge Tag", OptionType.Boolean),

   ALL_ATTRIBUTES("All Attributes", OptionType.Boolean),

   ATTRIBUTE_NAME("Attribute Name", OptionType.String),

   /**
    * This option is set with the {@link BranchId} of the branch rendered artifacts are to be loaded from. The optional
    * view identifier ({@link ArtifactId}) in the {@link BranchId} should be set to {@link ArtifactId#SENTINEL}.
    */

   BRANCH("Branch", OptionType.BranchId),

   /**
    * This option is set to the name of the branch rendered artifacts are to be loaded from.
    */

   BRANCH_NAME("Branch Name", OptionType.String),

   /**
    * Renderers can do the rendering in the client or on server. Server side renderers will return an input stream from
    * the web server. As the data arrives the FileSystemRenderer base code can write the data to the content file in the
    * workspace. Client side renderes can write either write all of there data into a buffer and create an input stream
    * that reads from that buffer after the render is complete; or they can write there data to a piped output stream
    * that was provided to it.
    */

   CLIENT_RENDERER_CAN_STREAM("Client Renderer Can Stream", OptionType.Boolean),

   COMPARE_BRANCH("Compare Branch", OptionType.BranchId),

   /**
    * A Publishing Template selection option value that may be used with the {@link RenderOption#TEMPLATE_OPTION} key.
    */

   DIFF_NO_ATTRIBUTES_VALUE("DIFF_NO_ATTRIBUTES", OptionType.String),

   DIFF_VALUE("DIFF", OptionType.String),

   EXCLUDE_ARTIFACT_TYPES("Exclude Artifact Types", OptionType.ArtifactTypes),

   EXCLUDE_FOLDERS("Exclude Folders", OptionType.Boolean),

   EXECUTE_VB_SCRIPT("execute.vb.script", OptionType.String),

   /**
    * Flag used to track the number of Publishing Template requests when {@link #USE_TEMPLATE_ONCE} is
    * <code>true</code>.
    */

   FIRST_TIME("First Time", OptionType.Boolean),

   ID("Id", OptionType.String),

   IN_PUBLISH_MODE("In Publish Mode", OptionType.Boolean),

   INCLUDE_UUIDS("Include Uuids", OptionType.Boolean),

   LINK_TYPE("linkType", OptionType.LinkType),

   MAINTAIN_ORDER("Maintain Order", OptionType.Boolean),

   MAX_OUTLINE_DEPTH("Maximum Outlining Depth", OptionType.Integer),

   NAME("Name", OptionType.String),

   NO_DISPLAY("No Display", OptionType.Boolean),

   /**
    * A document display option value that may be used with the {@link RendererOption#OPEN_OPTION} key for opening a
    * document in a markdown editor.
    */

   OPEN_IN_MARKDOWN_EDITOR_VALUE("Markdown Editor", OptionType.String),

   /**
    * A document display option value that may be used with the {@link RendererOption#OPEN_OPTION} key for opening a
    * document in MS Word.
    */

   OPEN_IN_MS_WORD_VALUE("MS Word", OptionType.String),

   /**
    * A key used in a {@link Map} of {@link RendererOption} key value pairs to specify how a rendered document is to be
    * opened for display.
    */

   OPEN_OPTION("open.option", OptionType.String),

   ORIG_PUBLISH_AS_DIFF("Orig Publish As Diff", OptionType.Boolean),

   OUTLINE_TYPE("Outline Type", OptionType.String),

   OUTPUT_STREAM("Output Stream", OptionType.OutputStream),

   OVERRIDE_DATA_RIGHTS("Override Data Rights", OptionType.String),

   PARAGRAPH_NUMBER("Paragraph Number", OptionType.String),

   /**
    * A Publishing Template selection option value that may be used with the {@link RenderOption#TEMPLATE_OPTION} key.
    */

   PREVIEW_ALL_NO_ATTRIBUTES_VALUE("PREVIEW_ALL_NO_ATTRIBUTES", OptionType.String),

   /**
    * A Publishing Template selection option value that may be used with the {@link RenderOption#TEMPLATE_OPTION} key.
    */

   PREVIEW_ALL_RECURSE_NO_ATTRIBUTES_VALUE("PREVIEW_WITH_RECURSE_NO_ATTRIBUTES", OptionType.String),

   /**
    * A Publishing Template selection option value that may be used with the {@link RenderOption#TEMPLATE_OPTION} key.
    */

   PREVIEW_ALL_RECURSE_VALUE("PREVIEW_ALL_RECURSE", OptionType.String),

   /**
    * A Publishing Template selection option value that may be used with the {@link RenderOption#TEMPLATE_OPTION} key.
    */

   PREVIEW_ALL_VALUE("PreviewAll", OptionType.String),

   /**
    * A Publishing Template selection option value that may be used with the {@link RenderOption#TEMPLATE_OPTION} key.
    */

   PREVIEW_WITH_RECURSE_VALUE("PREVIEW_WITH_RECURSE", OptionType.String),

   PROGRESS_MONITOR("Progress Monitor", OptionType.ProgressMonitor),

   PUBLISH_DIFF("Publish Diff", OptionType.Boolean),

   /**
    * When <code>false</code>, "Heading - MS Word" artifacts that do not have children that are publishable will be
    * excluded from the publish.
    */

   PUBLISH_EMPTY_HEADERS("Push Empty Headers", OptionType.Boolean),

   /**
    * This option is used to specify an identifier for the publish.
    */

   PUBLISH_IDENTIFIER("Publish Identifier", OptionType.String),

   /**
    * This option is used to specify the output format of the publish.
    */

   PUBLISHING_FORMAT("Publishing Format", OptionType.FormatIndicator),

   /**
    * This option is used to specify the Publishing Template Manager's identifier for a Publishing Template to the
    * renderer. The {@link MSWordTemplateClientRenderer} will use the specified template when:
    * <ul>
    * <li>{@link RendererOption#USE_TEMPLATE_ONCE} is unset or <code>false</code>;</li>
    * <li>{@link RendererOption#FIRST_TIME} is set and <code>true</code>; or</li>
    * <li>{@link RendererOption#SECOND_TIME} is set and <code>true</code>.</li>
    * </ul>
    */

   PUBLISHING_TEMPLATE_IDENTIFIER("Publishing Template Identifier", OptionType.String),

   RECURSE("Recurse", OptionType.Boolean),

   RECURSE_ON_LOAD("Recurse On Load", OptionType.Boolean),

   RESULT_PATH_RETURN("resultPath", OptionType.String),

   /**
    * Flag used to track the number of Publishing Template requests when {@link #USE_TEMPLATE_ONCE} is
    * <code>true</code>.
    */

   SECOND_TIME("SecondTime", OptionType.Boolean),

   /*
    * For client side streaming renderers, this option is set with the output stream for the render to use.
    */

   SKIP_DIALOGS("Skip Dialogs", OptionType.Boolean),

   /*
    * Open Option and Values
    */

   SKIP_ERRORS("Skip Errors", OptionType.Boolean),

   TEMPLATE_ONLY("Template Only", OptionType.Boolean),

   /*
    * Template Option and Values
    */

   /**
    * A key used in a {@link Map} of {@link RendererOption} key value pairs to specify the Publishing Template selection
    * option.
    */

   TEMPLATE_OPTION("Template", OptionType.String),

   /**
    * A Publishing Template selection option value that may be used with the {@link RenderOption#TEMPLATE_OPTION} key.
    */

   THREE_WAY_MERGE("THREE_WAY_MERGE", OptionType.String),

   TRANSACTION_OPTION("Transaction Option", OptionType.Transaction),

   UPDATE_PARAGRAPH_NUMBERS("Update Paragraph Numbers", OptionType.Boolean),

   USE_ARTIFACT_NAMES("Use Artifact Names", OptionType.Boolean),

   USE_PARAGRAPH_NUMBERS("Use Paragraph Numbers", OptionType.Boolean),

   /**
    * When <code>true</code> the first two Publishing Template requests will be by match criteria and the remaining will
    * be by the Publishing Template artifact identifier.
    */

   USE_TEMPLATE_ONCE("Use Template Once", OptionType.Boolean),

   VIEW("View", OptionType.ArtifactId),

   WAS_BRANCH("Was Branch", OptionType.BranchId);

   /**
    * Saves an {@link ObjectMapper} for JSON serialization and deserialization of Renderer Option values.
    */

   private static ObjectMapper objectMapper = RendererOption.createObjectMapper();

   /**
    * A reverse lookup map of {@link RendererOption} members by their associated key names.
    *
    * @implNote Ensure that all key names are unique.
    */

   private static final Map<String, RendererOption> rendererOptions;

   static {
      rendererOptions = new HashMap<>();

      for (var rendererOption : RendererOption.values()) {
         rendererOptions.put(rendererOption.getKey(), rendererOption);
      }
   }

   /**
    * Adds a {@link JsonDeserializer} to a {@link SimpleModule} for the <code>objectClass</code>.
    *
    * @param <T> The class type the deserializer is for.
    * @param simpleModule the {@link SimpleModule} to add the deserializer to.
    * @param objectClass the class type the deserializer is for.
    * @param jsonDeserializer the {@link JsonDeserializer} to be added.
    * @implNote This method is to deal with casting issues for a {@link JsonDeserializer} for an unknown (?) type.
    */

   @SuppressWarnings("unchecked")
   private static <T> void addDeserializer(SimpleModule simpleModule, Class<T> objectClass, JsonDeserializer<?> jsonDeserializer) {
      simpleModule.addDeserializer(objectClass, (JsonDeserializer<T>) jsonDeserializer);
   }

   /**
    * Creates the {@link ObjectMapper} for JSON processing of {@link RendererOption} value types.
    *
    * @return an {@link ObjectMapper} for JSON processing of {@link RendererOption} value types.
    */

   private static ObjectMapper createObjectMapper() {
      var simpleModule = RendererOption.createSimpleModule();
      var objectMapper = new ObjectMapper();
      objectMapper.registerModule(simpleModule);
      return objectMapper;
   }

   /**
    * Creates the Jackson {@link SimpleModule} used to configure the {@link ObjectMapper} for JSON processing of
    * {@link RendererOption} value types.
    *
    * @return the created {@link SimpleModule}.
    */

   private static SimpleModule createSimpleModule() {

      var version = new Version(1, 0, 0, Strings.EMPTY_STRING, Strings.EMPTY_STRING, Strings.EMPTY_STRING);

      var simpleModule = new SimpleModule(RendererOption.class.getName(), version);

      for (var rendererOption : RendererOption.values()) {
         var optionType = rendererOption.getType();
         var jsonDeserializer = optionType.getJsonDeserializer();
         if (Objects.isNull(jsonDeserializer)) {
            continue;
         }
         var optionClass = optionType.getImplementationClass();
         RendererOption.addDeserializer(simpleModule, optionClass, jsonDeserializer);
      }

      return simpleModule;
   }

   public static Optional<RendererOption> ofKey(String key) {
      //@formatter:off
      return
         Objects.nonNull( key )
            ? Optional.ofNullable( RendererOption.rendererOptions.get( key ) )
            : Optional.empty();
      //@formatter:on
   }

   @JsonCreator
   public static RendererOption valueOfKey(String key) {

      return RendererOption.rendererOptions.get(key);

   }

   private final String key;

   private final OptionType type;

   RendererOption(String key, OptionType type) {
      this.key = key;
      this.type = type;
   }

   public String getKey() {
      return key;
   }

   public OptionType getType() {
      return type;
   }

   /**
    * Deserializes a {@link RendererOption} value from a {@link JsonNode}.
    *
    * @param jsonNode the {@link JsonNode} containing the serialized {@link RendererOption} value.
    * @return the deserialized {@link RendererOption} value as an {@link Object}.
    */

   public Object readValue(JsonNode jsonNode) {

      try {
         return RendererOption.objectMapper.treeToValue(jsonNode, this.getType().getImplementationClass());
      } catch (Exception e) {
         return null;
      }

   }

}
