/*********************************************************************
 * Copyright (c) 2022 Boeing
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

/**
 * Class to encapsulate and transfer a publishing template's outlining options.
 *
 * @author Loren K. Ashley
 */

public class OutliningOptions implements ToMessage {

   /**
    * Constant for the special &quot;ContentAttributeType&quot; value used to indicate the main content attribute for
    * the publishing format should be used.
    *
    * @see {@link FormatIndicator}.
    */

   public static final String CONTENT_ATTRIBUTE_TYPE_FORMAT = "<format-content-attribute-type>";

   /**
    * The Constant used for the default Outlining Option value for &quot;ContentAttributeType&quot;.
    */

   private static String CONTENT_ATTRIBUTE_TYPE_DEFAULT = CONTENT_ATTRIBUTE_TYPE_FORMAT;

   /**
    * Constant for the special &quot;HeadingArtifactType&quot; value used to indicate that any artifact type can be used
    * as a heading.
    */

   public static final String HEADING_ARTIFACT_TYPE_ANY = "<any-heading-artifact-type>";

   /**
    * Constant for the special &quot;HeadingArtifactType&quot; value used to indicate that only artifacts of the type or
    * derived from the types {@link CoreArtifactType#Folder} or {@link CoreArtifactType#AbstractHeading} can be used as
    * a heading.
    */

   public static final String HEADING_ARTIFACT_TYPE_FOLDERS_AND_HEADERS_ONLY =
      "<folders-and-headers-only-heading-artifact-type>";

   /**
    * Constant for the special &quot;HeadingArtifactType&quot; value used to indicate that only artifacts of the type or
    * derived from the type {@link CoreArtifactType#Folder} can be used as a heading.
    */

   public static final String HEADING_ARTIFACT_TYPE_FOLDERS_ONLY = "<folders-only-heading-artifact-type>";

   /**
    * Constant for the special &quot;HeadingArtifactType&quot; value used to indicate that only artifact of the type or
    * derived from the heading artifact type for the publishing format can be used as a heading.
    *
    * @see {@link FormatIndicator}.
    */

   public static final String HEADING_ARTIFACT_TYPE_FORMAT = "<format-heading-artifact-type>";

   /**
    * Constant for the special &quot;HeadingArtifactType&quot; value used to indicate that only artifacts of the type or
    * derived from the type {@link CoreArtifactType#AbstractHeading} can be used as a heading.
    */

   public static final String HEADING_ARTIFACT_TYPE_HEADERS_ONLY = "<headers-only-heading-artifact-type>";

   /**
    * Constant used for the default Outlining Option value for &quot;HeadingArtifactType&quot;.
    */

   private static String HEADING_ARTIFACT_TYPE_DEFAULT = HEADING_ARTIFACT_TYPE_ANY;

   /**
    * Constant for the special &quot;HeadingAttributeType&quot; value used to indicate the attribute heading text will
    * be read from is the heading attribute type specified by the publishing format.
    *
    * @see {@link FormatIndicator}.
    */

   public static final String HEADING_ATTRIBUTE_TYPE_FORMAT = "<format-heading-attribute-type>";

   /**
    * Constant used for the default Outlining Option value for &quot;HeadingAttributeType&quot;.
    */

   private static String HEADING_ATTRIBUTE_TYPE_DEFAULT = HEADING_ATTRIBUTE_TYPE_FORMAT;

   /**
    * Sets publishing processor values from {@link OutliningOptions} and renderer options in an {@link RendererMap}.
    *
    * @param outliningOptionsArray an array of {@link OutliningOptions}. When the array is empty an
    * {@link OutliningOptions} structure with default values is created and used. When the array has one or more
    * members, the {@link OutliningOptions} structure from the first array element is used. Array entries beyond the
    * first are ignored.
    * @param formatIndicator a {@link FormatIndicator} indicating the requested format for the publish.
    * @param rendererMap a {@link RendererMap}, possibly empty, of the {@link RendererOptions} for the publish.
    * @param tokenService the {@link OrcsTokenService}. Used to lookup artifact and attribute type tokens by name.
    * @param allowedOutlineTypesSetter a {@link Consumer} used to set the publishing processor's Allowed Outline
    * Types. @see {@link #headingArtifactType}.
    * @param contentAttributeTypeSetter a {@link Consumer} used to set the publishing processor's Content Attribute
    * Type. @see {@link #contentAttributeType}.
    * @param excludeArtifactTypesSetter a {@link Consumer} used to set the publishing processor's list of artifact types
    * to be excluded from the publish. @see {@link #excludeArtifactTypes}.
    * @param headingArtifactTypeSetter a {@link Consumer} used to set the publishing processor's Heading Artifact Type.
    * @param headingAttributeTypeSetter a {@link Consumer} used to set the publishing processor's Heading Attribute
    * Type.
    * @param includeHeadingsSetter a {@link Consumer} used to set the publishing processor's Include Headings indicator.
    * @param initialOutlineNumberSetter a {@link Consumer} used to set the publishing processor's Initial Outline
    * Number.
    * @param overrideOutlineNumberSetter a {@link Consumer} used to set the publishing processor's Override Outline
    * Numbers flag.
    * @param recurseChildrenSetter a {@link Consumer} used to set the publishing processor's Recurse Children flag.
    * @param templateFooterSetter a {@link Consumer} used to set the publishing processor's Template Footer flag.
    * @throws NullPointerException if any of the parameters are <code>null</code>.
    */

   //@formatter:off
   public static void
      setValues
         (
            @NonNull OutliningOptions[]                      outliningOptionsArray,
            @NonNull FormatIndicator                         formatIndicator,
            @NonNull RendererMap                             rendererMap,
            @NonNull OrcsTokenService                        tokenService,
            @NonNull Consumer<AllowedOutlineTypes>           allowedOutlineTypesSetter,
            @NonNull Consumer<AttributeTypeToken>            contentAttributeTypeSetter,
            @NonNull Consumer<Collection<ArtifactTypeToken>> excludeArtifactTypesSetter,
            @NonNull Consumer<ArtifactTypeToken>             headingArtifactTypeSetter,
            @NonNull Consumer<AttributeTypeToken>            headingAttributeTypeSetter,
            @NonNull Consumer<IncludeHeadings>               includeHeadingsSetter,
            @NonNull Consumer<String>                        initialOutlineNumberSetter,
            @NonNull Consumer<Boolean>                       overrideOutlineNumberSetter,
            @NonNull Consumer<Boolean>                       recurseChildrenSetter,
            @NonNull Consumer<Boolean>                       templateFooterSetter
         ) {

      final var safeOutliningOptionsArray       = Conditions.requireNonNull( outliningOptionsArray,       "outliningOptionsArray"       );
      final var safeFormatIndicator             = Conditions.requireNonNull( formatIndicator,             "formatIndicator"             );
      final var safeRendererMap                 = Conditions.requireNonNull( rendererMap,                 "rendererMap"                 );
      final var safeTokenService                = Conditions.requireNonNull( tokenService,                "tokenService"                );
      final var safeAllowedOutlineTypesSetter   = Conditions.requireNonNull( allowedOutlineTypesSetter,   "allowedOutlineTypesSetter"   );
      final var safeContentAttributeTypeSetter  = Conditions.requireNonNull( contentAttributeTypeSetter,  "contentAttributeTypeSetter"  );
      final var safeExcludeArtifactTypesSetter  = Conditions.requireNonNull( excludeArtifactTypesSetter,  "excludeArtifactTypesSetter"  );
      final var safeHeadingArtifactTypeSetter   = Conditions.requireNonNull( headingArtifactTypeSetter,   "headingArtifactTypeSetter"   );
      final var safeHeadingAttributeTypeSetter  = Conditions.requireNonNull( headingAttributeTypeSetter,  "headingAttributeTypeSetter"  );
      final var safeIncludeHeadingsSetter       = Conditions.requireNonNull( includeHeadingsSetter,       "includeHeadingsSetter"       );
      final var safeInitialOutlineNumberSetter  = Conditions.requireNonNull( initialOutlineNumberSetter,  "initialOutlineNumberSetter"  );
      final var safeOverrideOutlineNumberSetter = Conditions.requireNonNull( overrideOutlineNumberSetter, "overrideOutlineNumberSetter" );
      final var safeRecurseChildrenSetter       = Conditions.requireNonNull( recurseChildrenSetter,       "recurseChildrenSetter"       );
      final var safeTemplateFooterSetter        = Conditions.requireNonNull( templateFooterSetter,        "templateFooterSetter"        );
      //@formatter:on

      //@formatter:off
         final var outliningOptions =
            ( safeOutliningOptionsArray.length >= 1 )
               ? safeOutliningOptionsArray[0]
               : new OutliningOptions().defaults();
         //@formatter:on

      /*
       * "ContentAttributeType"
       */

      //@formatter:off
      final var contentAttributeTypeTokenName =
         safeRendererMap.isRendererOptionSet( RendererOption.OUTLINING_OPTION_OVERRIDE_CONTENT_ATTRIBUTE_TYPE )
            ? (String) safeRendererMap.getRendererOptionValue( RendererOption.OUTLINING_OPTION_OVERRIDE_CONTENT_ATTRIBUTE_TYPE )
            : outliningOptions.getContentAttributeType();
      //@formatter:on

      switch (contentAttributeTypeTokenName) {
         case "*":
         case OutliningOptions.CONTENT_ATTRIBUTE_TYPE_FORMAT:
            safeContentAttributeTypeSetter.accept(safeFormatIndicator.getContentAttributeTypeToken());
            break;
         default:
            safeContentAttributeTypeSetter.accept(safeTokenService.getAttributeType(contentAttributeTypeTokenName));
            break;
      }

      /*
       * "ExcludeArtifactTypes"
       */

      //@formatter:off
      @SuppressWarnings("unchecked")
      final var excludeArtifactTypes =
         safeRendererMap.isRendererOptionSet( RendererOption.OUTLINING_OPTION_OVERRIDE_EXCLUDE_ARTIFACT_TYPES )
            ? (Collection<ArtifactTypeToken>) safeRendererMap.getRendererOptionValue( RendererOption.OUTLINING_OPTION_OVERRIDE_EXCLUDE_ARTIFACT_TYPES )
            : Arrays.stream( outliningOptions.getExcludeArtifactTypes() ).map( safeTokenService::getArtifactType ).collect( Collectors.toList() );
         //@formatter:on

      safeExcludeArtifactTypesSetter.accept(excludeArtifactTypes);

      /*
       * "HeadingArtifactType"
       */

      //@formatter:off
      final var headingArtifactTypeTokenName =
         safeRendererMap.isRendererOptionSet( RendererOption.OUTLINING_OPTION_OVERRIDE_HEADING_ARTIFACT_TYPE )
            ? (String) safeRendererMap.getRendererOptionValue( RendererOption.OUTLINING_OPTION_OVERRIDE_HEADING_ARTIFACT_TYPE )
            : outliningOptions.getHeadingArtifactType();
      //@formatter:on

      switch (headingArtifactTypeTokenName) {
         case OutliningOptions.HEADING_ARTIFACT_TYPE_ANY:
            safeAllowedOutlineTypesSetter.accept(AllowedOutlineTypes.ANYTHING);
            safeHeadingArtifactTypeSetter.accept(null);
            break;
         case OutliningOptions.HEADING_ARTIFACT_TYPE_FOLDERS_AND_HEADERS_ONLY:
            safeAllowedOutlineTypesSetter.accept(AllowedOutlineTypes.HEADERS_AND_FOLDERS_ONLY);
            safeHeadingArtifactTypeSetter.accept(null);
            break;
         case OutliningOptions.HEADING_ARTIFACT_TYPE_FOLDERS_ONLY:
            safeAllowedOutlineTypesSetter.accept(AllowedOutlineTypes.FOLDERS_ONLY);
            safeHeadingArtifactTypeSetter.accept(null);
            break;
         case OutliningOptions.HEADING_ARTIFACT_TYPE_FORMAT:
            safeAllowedOutlineTypesSetter.accept(AllowedOutlineTypes.RESTRICTED);
            safeHeadingArtifactTypeSetter.accept(safeFormatIndicator.getDefaultHeadingArtifactTypeToken());
            break;
         case OutliningOptions.HEADING_ARTIFACT_TYPE_HEADERS_ONLY:
            safeAllowedOutlineTypesSetter.accept(AllowedOutlineTypes.HEADERS_ONLY);
            safeHeadingArtifactTypeSetter.accept(null);
            break;
         default:
            safeAllowedOutlineTypesSetter.accept(AllowedOutlineTypes.RESTRICTED);
            safeHeadingArtifactTypeSetter.accept(safeTokenService.getArtifactType(headingArtifactTypeTokenName));
            break;
      }

      /*
       * "HeadingAttributeType"
       */

      //@formatter:off
      final var headingAttributeTypeTokenName =
         safeRendererMap.isRendererOptionSet( RendererOption.OUTLINING_OPTION_OVERRIDE_HEADING_ATTRIBUTE_TYPE )
            ? (String) safeRendererMap.getRendererOptionValue( RendererOption.OUTLINING_OPTION_OVERRIDE_HEADING_ATTRIBUTE_TYPE )
            : outliningOptions.getHeadingAttributeType();
      //@formatter:on

      switch (headingAttributeTypeTokenName) {
         case OutliningOptions.HEADING_ATTRIBUTE_TYPE_FORMAT:
            safeHeadingAttributeTypeSetter.accept(safeFormatIndicator.getDefaultHeadingContentAttributTypeToken());
            break;
         default:
            safeHeadingAttributeTypeSetter.accept(safeTokenService.getAttributeType(headingAttributeTypeTokenName));
            break;
      }

      /*
       * "IncludeHeadings"
       */

      //@formatter:off
      final var includeHeadings =
         safeRendererMap.isRendererOptionSet( RendererOption.OUTLINING_OPTION_OVERRIDE_INCLUDE_HEADINGS )
            ? (IncludeHeadings) safeRendererMap.getRendererOptionValue( RendererOption.OUTLINING_OPTION_OVERRIDE_INCLUDE_HEADINGS )
            : outliningOptions.getIncludeHeadings();
      //@formatter:on

      safeIncludeHeadingsSetter.accept(includeHeadings);

      /*
       * "OutlineNumber"
       */

      //@formatter:off
      final var initialOutlineNumber =
         safeRendererMap.isRendererOptionSet( RendererOption.OUTLINING_OPTION_OVERRIDE_OUTLINE_NUMBER )
            ? (String) safeRendererMap.getRendererOptionValue( RendererOption.OUTLINING_OPTION_OVERRIDE_OUTLINE_NUMBER )
            : outliningOptions.getOutlineNumber();
      //@formatter:on

      safeInitialOutlineNumberSetter.accept(initialOutlineNumber);

      /*
       * "OverrideOutlineNumber"
       */

      final var overrideOutlineNumber = outliningOptions.isOverrideOutlineNumber();

      safeOverrideOutlineNumberSetter.accept(overrideOutlineNumber);

      /*
       * "RecurseChildren"
       */

      //@formatter:off
      final var recurseChildren =
         safeRendererMap.isRendererOptionSet( RendererOption.RECURSE_ON_LOAD )
            ? (Boolean) safeRendererMap.getRendererOptionValue( RendererOption.RECURSE_ON_LOAD )
            : outliningOptions.isRecurseChildren();
      //@formatter:on

      safeRecurseChildrenSetter.accept(recurseChildren);

      /*
       * "TemplateFooter"
       */

      final var templateFooter = outliningOptions.isTemplateFooter();

      safeTemplateFooterSetter.accept(templateFooter);

   }

   /**
    * This outlining option is no longer used.
    *
    * @deprecated
    */

   @Deprecated
   @JsonProperty("ArtifactName")
   private String artifactName;

   /**
    * Specifies the attribute type to be used for the main content of each artifact in the publish. The following are
    * allowed values:
    * <dl>
    * <dt>&quot;*&quot;</dt>
    * <dd>The default main content attribute for the publishing format will be used.</dd>
    * <dt>&quot;&lt;format-content-attribute-type&gt;&quot;</dt>
    * <dd>The default main content attribute for the publishing format will be used.</dd>
    * <dt>An attribute type name.</dt>
    * <dd>The named attribute type will be used to obtain the main content for each artifact in the publish.</dd>
    * <dl>
    * The default value is:
    * <ul>
    * <li>&quot;&lt;format-content-attribute-type&gt;&quot;</li>
    * </ul>
    */

   @JsonProperty("ContentAttributeType")
   private String contentAttributeType;

   /**
    * Specifies the artifact types by {@link ArtifactTypeToken} to be excluded from the publish. The descendants of
    * excluded artifacts will still be considered for the publish.
    * <p>
    * The default value is:
    * <ul>
    * <li>an empty array.</li>
    * </ul>
    */

   @JsonProperty("ExcludeArtifactTypes")
   private String[] excludeArtifactTypes;

   /**
    * Specifies the artifact type to be used for document headings when the value of &quot;AllowedOutlineTypes&quot; is
    * &quot;Restricted&quot;. Any artifact of the type or derived from the specified artifact type is eligible to be
    * used as a document heading. The following are allowed values:
    * <dl>
    * <dt>&quot;&lt;any-heading-artifact-type&gt;&quot;</dt>
    * <dd>indicates that any artifact type can be used as a heading.</dd>
    * <dt>&quot;&lt;folders-only-heading-artifact-type&gt;&quot;</dt>
    * <dd>Only artifact types of or derived from {@link CoreArtifactTypes#Folder} can be rendered as outline
    * headings.</dd>
    * <dt>&quot;&lt;folders-and-headers-only-artifact-type&gt;&quot;</dt>
    * <dd>Only artifact types of or derived from {@link CoreArtifactTypes#Folder} or
    * {@link CoreArtifactTypes#AbstractHeading} can be rendered as outline headings.</dd>
    * <dt>&quot;&lt;format-heading-artifact-type&gt;&quot;</dt>
    * <dd>indicates to use the default heading artifact type for the publishing format.</dd>
    * <dt>&quot;&lt;headers-only-artifact-type&gt;&quot;</dt>
    * <dd>Only artifact types of or derived from {@link CoreArtifactTypes#AbstractHeading} can be rendered as outline
    * headings.</dd>
    * <dt>An artifact type name.</dt>
    * <dd>When &quot;HeadingArtifactType&quot; is specified as an artifact type name, only artifacts of the specified
    * type can be rendered as outline headings.</dd>
    * </dl>
    * The default value is:
    * <ul>
    * <li>&quot;&lt;any-heading-artifact-type&gt;&quot;</li>
    * </ul>
    */

   @JsonProperty("HeadingArtifactType")
   private String headingArtifactType;

   /**
    * Specifies the attribute type name of the attribute the text for the heading will be read from. The following are
    * allowed values:
    * <dl>
    * <dt>&quot;&lt;format-heading-attribute-type&gt;&quot;</dt>
    * <dd>indicates to use the default heading attribute type for the publishing format.</dd>
    * <dt>An attribute type name.</dt>
    * <dd>When &quot;HeadingArtifactType&quot; is specified as an attribute type name, the heading text will be read
    * from the named attribute.
    * </dl>
    * The default value is:
    * <ul>
    * <li>&quot;&lt;format-heading-attribute-type&gt;&quot;</li>
    * </ul>
    */

   @JsonProperty("HeadingAttributeType")
   private String headingAttributeType;

   /**
    * When &quot;IncludeHeadings&quot; is set this deprecated option is ignored. When &quot;IncludeHeadings&quot; is not
    * set this option will be used to set the default value of &quot;HeadingAttributeType&quot; as follows:
    * <dl>
    * <dt><code>true</code></dt>
    * <dd>&quot;IncludeEmptyHeaders&quot; will be set to &quot;Always&quot;</dd>
    * <dt><code>false</code></dt>
    * <dd>&quot;IncludeEmptyHeaders&quot; will be set to &quot;OnlyWithNonHeadingDescandants&quot;</dd>
    * </dl>
    *
    * @deprecated use &quot;IncludeHeadings&quot;. This option will be ignored when &quot;IncludeHeadings&quot; is
    * specified.
    */

   @Deprecated
   @JsonProperty("IncludeEmptyHeaders")
   private Boolean includeEmptyHeaders;

   /**
    * Indicates when to generate an outline heading for an artifact based upon the artifact's descendants.
    * <dl>
    * <dt>&quot;Always&quot;</dt>
    * <dd>All headings are included.</dd>
    * <dt>&quot;Never&quot;</dt>
    * <dd>Outline headings will not be generated.</dd>
    * <dt>&quot;OnlyWithNonHeadingDescandants&quot;</dt>
    * <dd>Only headings with hierarchical descendants that are not headings are included.</dd>
    * <dt>&quot;OnlyWithMainContent&quot;</dt>
    * <dd>Only headings with main content or a hierarchical descendant with main content are included.</dd>
    * </dl>
    * The default value is found as follows:
    * <p>
    * <dl>
    * <dt>&quot;IncludeEmptyHeaders&quot; is not set:</dt>
    * <dd>&quot;Always&quot;</dd>
    * <dt>&quot;IncludeEmptyHeaders&quot; is set:</dt>
    * <dd>
    * <dl>
    * <dt><code>true</code>:</dt>
    * <dd>&quot;Always&quot;</dd>
    * <dt><code>false</code>:</dt>
    * <dd>&quot;OnlyWithNonHeadingDescandants&quot;</dd></dd>
    * </dl>
    */

   @JsonProperty("IncludeHeadings")
   private IncludeHeadings includeHeadings;

   /**
    * The outlining number to be used as the first heading number for the document.
    */

   @JsonProperty("OutlineNumber")
   private String outlineNumber;

   /**
    * When <code>true</code>, only artifacts of the types or derived from the types
    * {@link CoreArtifactTypes.AbstractHeading} and {@link CoreArtifactTypes.Folder} are eligible to be used as a
    * document heading. When <code>false</code> artifacts of any type are eligible to be used as a document heading.
    *
    * @deprecated use &quot;AllowedOutlineTypes&quot;. This option will be ignored when &quot;AllowedOutlineTypes&quot;
    * is specified.
    */

   @Deprecated
   @JsonProperty("OutlineOnlyHeaderFolders")
   private Boolean outlineOnlyHeaderFolders;

   /**
    * When the option &quot;IncludeHeadings&quot; is not specified and this option is specified outlining will behave as
    * follows:
    * <dl>
    * <dt><code>true</code></dt>
    * <dd>Outlining will be generated as though &quot;IncludeHeadings&quot; was set to &quot;Always&quot;.</dd>
    * <dt><code>false</code></dt>
    * <dd>Outlining will be generated as though &quot;IncludeHeadings&quot; was set to &quot;Never&quot;.</dd>
    * </dl>
    *
    * @deprecated use &quot;IncludeHeadings&quot;. This option will be ignored when &quot;IncludeHeadings&quot; is
    * specified.
    */

   @Deprecated
   @JsonProperty("Outlining")
   private Boolean outlining;

   /**
    * When <code>true</code>, the outline number will be read from the artifact's
    * {@link CoreAttributeTypes#ParagraphNumber} attribute when the attribute is present and set; otherwise, the outline
    * number will be determined from artifact's hierarchical position. When <code>false</code>, the current outline
    * number will be obtained from the document outline number tracking object.
    */

   @JsonProperty("OverrideOutlineNumber")
   private Boolean overrideOutlineNumber;

   /**
    * When <code>true</code>, the hierarchical descendants of the artifacts specified for the publish will be included
    * in the published document. When <code>false</code>, only the artifacts specified for the publish will be included
    * in the published document. <code>false</code> is generally used in publishing templates for editing an artifact or
    * for previewing a single artifact.
    */

   @JsonProperty("RecurseChildren")
   private Boolean recurseChildren;

   /**
    * When <code>true</code>, the footer in the publishing template will be used for the document. When
    * <code>false</code>, the footer determined for each artifact's data rights will be used.
    */

   @JsonProperty("TemplateFooter")
   private Boolean templateFooter;

   public OutliningOptions() {

      this.contentAttributeType = null;
      this.excludeArtifactTypes = null;
      this.headingArtifactType = null;
      this.headingAttributeType = null;
      this.includeHeadings = null;
      this.outlineNumber = null;
      this.overrideOutlineNumber = null;
      this.recurseChildren = null;
      this.templateFooter = null;

      /*
       * Deprecated
       */

      this.artifactName = null;
      this.includeEmptyHeaders = null;
      this.outlineOnlyHeaderFolders = null;
      this.outlining = null;
   }

   public OutliningOptions defaults() {

      if (this.contentAttributeType == null) {
         this.contentAttributeType = OutliningOptions.CONTENT_ATTRIBUTE_TYPE_DEFAULT;
      }

      if (this.excludeArtifactTypes == null) {
         this.excludeArtifactTypes = new String[0];
      }

      if (this.headingArtifactType == null) {
         //@formatter:off
         this.headingArtifactType =
            ( Objects.nonNull( this.outlineOnlyHeaderFolders ) && this.outlineOnlyHeaderFolders )
               ? OutliningOptions.HEADING_ARTIFACT_TYPE_FOLDERS_AND_HEADERS_ONLY
               : OutliningOptions.HEADING_ARTIFACT_TYPE_DEFAULT;
         //@formatter:on
      }

      if (this.headingAttributeType == null) {
         this.headingAttributeType = OutliningOptions.HEADING_ATTRIBUTE_TYPE_DEFAULT;
      }

      if (this.includeHeadings == null) {
         //@formatter:off
         this.includeHeadings =
            ( this.includeEmptyHeaders == null )
               ? IncludeHeadings.ALWAYS
               : this.includeEmptyHeaders
                    ? IncludeHeadings.ALWAYS
                    : IncludeHeadings.ONLY_WITH_NON_HEADING_DESCENDANTS;
         //@formatter:on
      }

      if (this.outlineNumber == null) {
         this.outlineNumber = Strings.EMPTY_STRING;
      }

      if (Objects.isNull(this.overrideOutlineNumber)) {
         this.overrideOutlineNumber = false;
      }

      if (Objects.isNull(this.recurseChildren)) {
         this.recurseChildren = false;
      }

      if (Objects.isNull(this.templateFooter)) {
         this.templateFooter = false;
      }

      /*
       * Deprecated
       */

      if (this.artifactName == null) {
         this.artifactName = Strings.EMPTY_STRING;
      }

      if (this.includeEmptyHeaders == null) {
         this.includeEmptyHeaders = false;
      }

      if (this.outlineOnlyHeaderFolders == null) {
         this.outlineOnlyHeaderFolders = false;
      }

      if (this.outlining == null) {
         this.outlining = false;
      }

      return this;
   }

   @Deprecated
   public String getArtifactName() {

      Conditions.requireMemberSet(this.artifactName, "artifactName");

      return this.artifactName;
   }

   public String getContentAttributeType() {

      Conditions.requireMemberSet(this.contentAttributeType, "contentAttributeType");

      return this.contentAttributeType;
   }

   public String[] getExcludeArtifactTypes() {

      Conditions.requireMemberSet(this.excludeArtifactTypes, "excludeArtifactTypes");

      return this.excludeArtifactTypes;
   }

   public String getHeadingArtifactType() {

      Conditions.requireMemberSet(this.headingArtifactType, "headingArtifactType");

      return this.headingArtifactType;
   }

   public String getHeadingAttributeType() {
      if (Objects.isNull(this.headingAttributeType)) {
         throw new IllegalStateException(
            "OutliningOptions::getHeadingAttributeType, the member \"headingAttributeType\" has not been set.");
      }
      return this.headingAttributeType;
   }

   public IncludeHeadings getIncludeHeadings() {
      Conditions.requireMemberSet(this.includeHeadings, "includeHeadings");
      return this.includeHeadings;
   }

   public String getOutlineNumber() {
      if (Objects.isNull(this.outlineNumber)) {
         throw new IllegalStateException(
            "OutliningOptions::getOutlineNumber, the member \"outlineNumber\" has not been set.");
      }
      return this.outlineNumber;
   }

   @Deprecated
   public boolean isIncludeEmptyHeaders() {

      Conditions.requireMemberSet(this.includeEmptyHeaders, "includeEmptyHeaders");

      return this.includeEmptyHeaders;
   }

   @Deprecated
   public boolean isOutlineOnlyHeaderFolders() {
      if (Objects.isNull(this.outlineOnlyHeaderFolders)) {
         throw new IllegalStateException(
            "OutliningOptions::isOutlineOnlyHeaderFolders, the member \"outlineOnlyHeaderFolders\" has not been set.");
      }
      return this.outlineOnlyHeaderFolders;
   }

   /*
    * Deprecated getters
    */

   @Deprecated
   public boolean isOutlining() {
      if (Objects.isNull(this.outlining)) {
         throw new IllegalStateException("OutliningOptions::isOutlining, the member \"outlining\" has not been set.");
      }
      return this.outlining;
   }

   public boolean isOverrideOutlineNumber() {
      if (Objects.isNull(this.overrideOutlineNumber)) {
         throw new IllegalStateException(
            "OutliningOptions::isOverrideOutlineNumber, the member \"overrideOutlineNumber\" has not been set.");
      }
      return this.overrideOutlineNumber;
   }

   public boolean isRecurseChildren() {
      if (Objects.isNull(this.recurseChildren)) {
         throw new IllegalStateException(
            "OutliningOptions::isRecurseChildren, the member \"recurseChildren\" has not been set.");
      }
      return this.recurseChildren;
   }

   public boolean isTemplateFooter() {
      if (Objects.isNull(this.templateFooter)) {
         throw new IllegalStateException(
            "OutliningOptions::isTemplateFooter, the member \"templateFooter\" has not been set.");
      }
      return this.templateFooter;
   }

   @JsonIgnore
   public boolean isValid() {
      //@formatter:off
      return
            Objects.nonNull( this.contentAttributeType )
         && Objects.nonNull( this.excludeArtifactTypes )
         && Objects.nonNull( this.headingArtifactType )
         && Objects.nonNull( this.headingAttributeType )
         && Objects.nonNull( this.includeHeadings )
         && Objects.nonNull( this.outlineNumber )
         && Objects.nonNull( this.overrideOutlineNumber )
         && Objects.nonNull( this.recurseChildren )
         && Objects.nonNull( this.templateFooter )
         && Objects.nonNull( this.artifactName )
         && Objects.nonNull( this.includeEmptyHeaders )
         && Objects.nonNull( this.outlineOnlyHeaderFolders )
         && Objects.nonNull( this.outlining )
         ;
      //@formatter:on
   }

   /**
    * Deprecated Setters
    */

   @Deprecated
   public void setArtifactName(String artifactName) {

      if (Objects.nonNull(this.artifactName)) {
         throw new IllegalStateException(
            "OutliningOptions::setArtifactName, member \"artifactName\" has already been set.");
      }

      this.artifactName = Conditions.requireNonNull(artifactName, "artifactName");
   }

   public void setContentAttributeType(String contentAttributeType) {

      Conditions.requireNull(this.contentAttributeType, "contentAttributeType");

      this.contentAttributeType = Conditions.requireNonNull(contentAttributeType, "contentAttributeType");
   }

   public void setExcludeArtifactTypes(String[] excludeArtifactTypes) {

      Conditions.requireNull(this.excludeArtifactTypes, "excludeArtifactTypes");

      this.excludeArtifactTypes = Conditions.requireNonNull(excludeArtifactTypes, "excludeArtifactTypes ");
   }

   public void setHeadingArtifactType(String headingArtifactType) {

      if (Objects.nonNull(this.headingArtifactType)) {
         throw new IllegalStateException(
            "OutliningOptions::setHeadingArtifactType, member \"headingArtifactType\" has already been set.");
      }

      this.headingArtifactType = Conditions.requireNonNull(headingArtifactType, "headingArtifactType");
   }

   public void setHeadingAttributeType(String headingAttributeType) {

      if (Objects.nonNull(this.headingAttributeType)) {
         throw new IllegalStateException(
            "OutliningOptions::setHeadingAttributeType, member \"headingAttributeType\" has already been set.");
      }

      this.headingAttributeType = Conditions.requireNonNull(headingAttributeType, "headingAttributeType");
   }

   public void setIncludeEmptyHeaders(Boolean includeEmptyHeaders) {

      if (Objects.nonNull(this.includeEmptyHeaders)) {
         throw new IllegalStateException(
            "OutliningOptions::setIncludeEmptyHeaders, member \"includeEmptyHeaders\" has already been set.");
      }

      this.includeEmptyHeaders = Conditions.requireNonNull(includeEmptyHeaders, "includeEmptyHeaders");
   }

   public void setIncludeHeadings(IncludeHeadings includeHeadings) {

      Conditions.requireNull(this.includeHeadings, "includeEmptyHeaders");

      this.includeHeadings = Conditions.requireNonNull(includeHeadings, "includeHeadings");
   }

   public void setOutlineNumber(String outlineNumber) {

      if (Objects.nonNull(this.outlineNumber)) {
         throw new IllegalStateException(
            "OutliningOptions::setOutlineNumber, member \"outlineNumber\" has already been set.");
      }

      this.outlineNumber = Conditions.requireNonNull(outlineNumber, "outlineNumber");
   }

   public void setOutlineOnlyHeaderFolders(Boolean outlineOnlyHeaderFolders) {

      if (Objects.nonNull(this.outlineOnlyHeaderFolders)) {
         throw new IllegalStateException(
            "OutliningOptions::setOutlineOnlyHeaderFolders, member \"outlineOnlyHeaderFolders\" has already been set.");
      }

      this.outlineOnlyHeaderFolders = Conditions.requireNonNull(outlineOnlyHeaderFolders, "outlineOnlyHeaderFolders");
   }

   public void setOutlining(Boolean outlining) {

      if (Objects.nonNull(this.outlining)) {
         throw new IllegalStateException("OutliningOptions::setOutlining, member \"outlining\" has already been set.");
      }

      this.outlining = Conditions.requireNonNull(outlining, "outlining");
   }

   public void setOverrideOutlineNumber(Boolean overrideOutlineNumber) {

      if (Objects.nonNull(this.overrideOutlineNumber)) {
         throw new IllegalStateException(
            "OutliningOptions::setOverrideOutlineNumber, member \"overrideOutlineNumber\" has already been set.");
      }

      this.overrideOutlineNumber = Conditions.requireNonNull(overrideOutlineNumber, "overrideOutlineNumber");
   }

   public void setRecurseChildren(Boolean recurseChildren) {

      if (Objects.nonNull(this.recurseChildren)) {
         throw new IllegalStateException(
            "OutliningOptions::setRecurseChildren, member \"recurseChildren\" has already been set.");
      }

      this.recurseChildren = Conditions.requireNonNull(recurseChildren, "recurseChildren");
   }

   public void setTemplateFooter(Boolean templateFooter) {
      if (Objects.nonNull(this.templateFooter)) {
         throw new IllegalStateException(
            "OutliningOptions::setTemplateFooter, member \"templateFooter\" has already been set.");
      }

      this.templateFooter = Conditions.requireNonNull(templateFooter, "templateFooter");
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Message toMessage(int indent, Message message) {
      var outMessage = Objects.nonNull(message) ? message : new Message();

      //@formatter:off
      outMessage
         .indent( indent )
         .title( "Outlining Options" )
         .indentInc()
         .segment( "Content Attribute Type",      this.contentAttributeType     )
         .segment( "Heading Artifact Type",       this.headingArtifactType      )
         .segment( "Heading Attribute Type",      this.headingAttributeType     )
         .segment( "Include Headings",            this.includeHeadings          )
         .segment( "Outline Number",              this.outlineNumber            )
         .segment( "Override Outline Number",     this.overrideOutlineNumber    )
         .segment( "Recurse Children",            this.recurseChildren          )
         .segment( "Template Footers",            this.templateFooter           )
         .indentDec()
         .title( "Deprecated Outlining Options")
         .indentInc()
         .segment( "Artifact Name",               this.artifactName             )
         .segment( "Include Empty Headers",       this.includeEmptyHeaders      )
         .segment( "Outline Only Header Folders", this.outlineOnlyHeaderFolders )
         .segment( "Outlining",                   this.outlining                )
         .indentDec()
         ;
      //@formatter:on

      return outMessage;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String toString() {
      return this.toMessage(0, (Message) null).toString();
   }
}

/* EOF */
