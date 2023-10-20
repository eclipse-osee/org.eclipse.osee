/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.core.enums;

import static org.eclipse.osee.framework.core.enums.CoreTypeTokenProvider.osee;
import static org.eclipse.osee.framework.core.enums.FileExtension.XML;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.AttributeTypeArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeBoolean;
import org.eclipse.osee.framework.core.data.AttributeTypeDate;
import org.eclipse.osee.framework.core.data.AttributeTypeDouble;
import org.eclipse.osee.framework.core.data.AttributeTypeInputStream;
import org.eclipse.osee.framework.core.data.AttributeTypeInteger;
import org.eclipse.osee.framework.core.data.AttributeTypeJoin;
import org.eclipse.osee.framework.core.data.AttributeTypeLong;
import org.eclipse.osee.framework.core.data.AttributeTypeMapEntry;
import org.eclipse.osee.framework.core.data.AttributeTypeString;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.DisplayHint;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.data.computed.ComputedSoftwareCriticalityIndex;
import org.eclipse.osee.framework.core.enums.token.ComponentAttributeType;
import org.eclipse.osee.framework.core.enums.token.CsciAttributeType;
import org.eclipse.osee.framework.core.enums.token.CuiLimitedDisseminationControlIndicatorAttributeType;
import org.eclipse.osee.framework.core.enums.token.DataClassificationAttributeType;
import org.eclipse.osee.framework.core.enums.token.DataClassificationRationaleType;
import org.eclipse.osee.framework.core.enums.token.DataRightsBasisType;
import org.eclipse.osee.framework.core.enums.token.DataRightsClassificationAttributeType;
import org.eclipse.osee.framework.core.enums.token.FACEOSSProfileAttributeType;
import org.eclipse.osee.framework.core.enums.token.FACESegmentAttributeType;
import org.eclipse.osee.framework.core.enums.token.FACETechStandardVersionAttributeType;
import org.eclipse.osee.framework.core.enums.token.FdalAttributeType;
import org.eclipse.osee.framework.core.enums.token.FeatureValueAttributeType;
import org.eclipse.osee.framework.core.enums.token.FileExtensionAttributeType;
import org.eclipse.osee.framework.core.enums.token.FunctionalGroupingAttributeType;
import org.eclipse.osee.framework.core.enums.token.GfeCfeAttributeType;
import org.eclipse.osee.framework.core.enums.token.HttpMethodAttributeType;
import org.eclipse.osee.framework.core.enums.token.IdalAttributeType;
import org.eclipse.osee.framework.core.enums.token.InterfaceLogicalTypeAttribute;
import org.eclipse.osee.framework.core.enums.token.InterfaceMessagePeriodicityAttributeType;
import org.eclipse.osee.framework.core.enums.token.InterfaceMessageRateAttributeType;
import org.eclipse.osee.framework.core.enums.token.InterfaceMessageTypeAttributeType;
import org.eclipse.osee.framework.core.enums.token.InterfacePlatformTypeUnitsAttribute;
import org.eclipse.osee.framework.core.enums.token.InterfaceStructureCategoryAttribute;
import org.eclipse.osee.framework.core.enums.token.LegacyDalAttributeType;
import org.eclipse.osee.framework.core.enums.token.PageOrientationAttributeType;
import org.eclipse.osee.framework.core.enums.token.PartitionAttributeType;
import org.eclipse.osee.framework.core.enums.token.ProducesMediaTypeAttributeType;
import org.eclipse.osee.framework.core.enums.token.QualificationMethodAttributeType;
import org.eclipse.osee.framework.core.enums.token.RequiredIndicatorFrequencyIndicatorAttributeType;
import org.eclipse.osee.framework.core.enums.token.SafetySeverityAttributeType;
import org.eclipse.osee.framework.core.enums.token.SeverityCategoryAttributeType;
import org.eclipse.osee.framework.core.enums.token.SoftwareControlCategoryAttributeType;
import org.eclipse.osee.framework.core.enums.token.SoftwareCriticalityIndexAttributeType;
import org.eclipse.osee.framework.core.enums.token.SubsystemAttributeType;
import org.eclipse.osee.framework.core.enums.token.TestProcedureStatusAttributeType;
import org.eclipse.osee.framework.core.enums.token.TisTestCategoryAttributeType;
import org.eclipse.osee.framework.core.enums.token.TisTestTypeAttributeType;
import org.eclipse.osee.framework.core.enums.token.TrigraphCountryCodeIndicatorAttributeType;
import org.eclipse.osee.framework.core.enums.token.VerificationEventAttributeType;
import org.eclipse.osee.framework.core.enums.token.VerificationLevelAttributeType;
import org.eclipse.osee.framework.core.publishing.CuiCategoryIndicator;
import org.eclipse.osee.framework.core.publishing.CuiTypeIndicator;
import org.eclipse.osee.framework.core.util.toggles.CuiNamesConfiguration;

/**
 * @author Roberto E. Escobar
 */

public interface CoreAttributeTypes {

   // @formatter:off

   AttributeTypeString AccessContextId = osee.createString(1152921504606847102L, "Access Context Id", MediaType.TEXT_PLAIN, "", DisplayHint.SingleLine);

   AttributeTypeString Acronym = osee.createString(4723834159825897915L, "Acronym", MediaType.TEXT_PLAIN, "");

   AttributeTypeBoolean Active = osee.createBoolean(1152921504606847065L, "Active", MediaType.TEXT_PLAIN, "");

   AttributeTypeString Actual = osee.createString(1152921504606847371L, "Actual", MediaType.TEXT_PLAIN, "Actual Value");

   AttributeTypeString Expected = osee.createString(1152921504606847370L, "Expected", MediaType.TEXT_PLAIN, "Expected Value");

   AttributeTypeString ApplicationName = osee.createString(2728059764602429474L, "Application Name", MediaType.TEXT_PLAIN, "Name of the application the artifact belongs to");

   AttributeTypeString Annotation = osee.createString(1152921504606847094L, "Annotation", MediaType.TEXT_PLAIN, "");

   AttributeTypeString AtsActionSearch = osee.createString(72063457009467643L, "ATS Action Search", MediaType.TEXT_PLAIN, "Saved ATS Quick Searches.");

   AttributeTypeString AtsUserConfig = osee.createString(2348752981434455L, "ATS User Config", MediaType.TEXT_PLAIN, "Saved ATS Configures");

   AttributeTypeString ArgumentType = osee.createString(5717501448114952990L, "Argument Type", MediaType.TEXT_PLAIN, "Argument Type");

   AttributeTypeArtifactId BaselinedBy = osee.createArtifactIdNoTag(1152921504606847247L, "Baselined By", MediaType.TEXT_PLAIN, "");

   AttributeTypeDate BaselinedTimestamp = osee.createDateNoTag(1152921504606847244L, "Baselined Timestamp", AttributeTypeToken.TEXT_CALENDAR, "");

   AttributeTypeString BranchDiffData = osee.createString(1152921504606847921L, "Branch Diff Data", MediaType.APPLICATION_JSON, "Json results from change report");

   CsciAttributeType CSCI = osee.createEnum(new CsciAttributeType());

   AttributeTypeString Category = osee.createString(1152921504606847121L, "Category", MediaType.TEXT_PLAIN, "");

   AttributeTypeInteger CircuitBreakerId = osee.createIntegerNoTag(188458869981238L, "Circuit Breaker Id", MediaType.TEXT_PLAIN, "");

   AttributeTypeString City = osee.createString(1152921504606847068L, "City", MediaType.TEXT_PLAIN, "");

   AttributeTypeDate CommandTimestamp = osee.createDateNoTag(6908130616864675217L, "Command Timestamp",  MediaType.TEXT_PLAIN, "Timestamp of command execution");

   AttributeTypeString Company = osee.createString(1152921504606847066L, "Company", MediaType.TEXT_PLAIN, "");

   AttributeTypeString CompanyTitle = osee.createString(1152921504606847067L, "Company Title", MediaType.TEXT_PLAIN, "");

   ComponentAttributeType Component = osee.createEnum(new ComponentAttributeType());

   AttributeTypeBoolean ConnectionRequired = osee.createBoolean(2323113524155051455L, "Connection Required", MediaType.TEXT_PLAIN, "Specifies if an import requires a connection to be provided");

   AttributeTypeString ContentUrl = osee.createString(1152921504606847100L, "Content URL", MediaType.TEXT_PLAIN, "");

   AttributeTypeString Country = osee.createString(1152921504606847072L, "Country", MediaType.TEXT_PLAIN, "");

   AttributeTypeBoolean CustomCommand = osee.createBoolean(317394570332960L, "Custom Command", MediaType.TEXT_PLAIN, "");

   AttributeTypeString CrossReferenceValue = osee.createString(1761323951115447407L, "Cross Reference Value", MediaType.TEXT_PLAIN, "");

   AttributeTypeString CrossReferenceAdditionalContent = osee.createString(6645243569977378792L, "Cross Reference Additional Content", MediaType.TEXT_PLAIN, "");

   AttributeTypeString CrossReferenceArrayValues = osee.createString(1395395257321371828L, "Cross Reference Array Values", MediaType.TEXT_PLAIN, "");

   /**
    * Attribute type for CUI Category and CUI Type pairs.
    * <p>
    * This attribute type is used by the following artifact types:
    * <dl>
    * <dt>{@link CoreArtifactTypes#Controlled}:</dt>
    * <dd>Attributes of this attribute type specify a CUI Category and CUI Type of the data contained in the controlled artifact.</dd>
    * <dt>{@link CoreArtifactTypes#DataRightsConfiguration}:</dt>
    * <dd>Attributes of this attribute type specify an allowed CUI Category and CUI Type for a publish.</dd>
    * </dl>
    * <dl>
    * <dt>Display Name:</dt>
    * <dd>
    * <dl>
    * <dt>{@link CuiNamesConfiguration#STANDARD}:</dt>
    * <dd>CUI Category And CUI Type Pairs</dd>
    * <dt>{@link CuiNamesConfiguration#VERSION_ONE}:</dt>
    * <dd>Data Classification</dd>
    * </dl>
    * <dt>Attribute Type Identifier:</dt>
    * <dd>4024614255972662076L</dd>
    * </dl>
    * The enumeration members are created from all permutations of the {@link CuiCategoryIndicator} and {@link CuiTypeIndicator} enumerations. The allowable CUI Categories are defined by the
    * <a href="https://www.archives.gov/cui/registry/category-list" target="_blank">National Archives CUI Registry Category List</a>
    */

   DataClassificationAttributeType DataClassification =
      osee.createEnum
           (
              DataClassificationAttributeType::new,
              DataClassificationAttributeType::getConfiguredNameAndDescription,
              4024614255972662076L,
              MediaType.TEXT_PLAIN,
              NamespaceToken.OSEE,
              TaggerTypeToken.SENTINEL
           );

   /**
    * Attribute type for the CUI Category and CUI Type Rational.
    * <p>
    * This attribute is used by the {@link CoreArtifactTypes#Controlled} artifact to document the rationale for the selected CUI Category and CUI Type pairs in
    * the {@link CoreAttributeTypes#DataClassification} attribute for the artifact with controlled data.
    * <dl>
    * <dt>Display Name:</dt>
    * <dd>
    * <dl>
    * <dt>{@link CuiNamesConfiguration#STANDARD}:</dt>
    * <dd>CUI Category CUI Type Rationale</dd>
    * <dt>{@link CuiNamesConfiguration#VERSION_ONE}:</dt>
    * <dd>Data Classification Rationale</dd>
    * </dl>
    * <dt>Attribute Type Identifier:</dt>
    * <dd>6697327397016528458L</dd>
    * </dd>
    * </dl>
    */

   AttributeTypeString DataClassificationRationale =
      osee.createString
           (
              DataClassificationRationaleType::getConfiguredNameAndDescription,
              6697327397016528458L,
              MediaType.TEXT_PLAIN,
              TaggerTypeToken.SENTINEL,
              NamespaceToken.OSEE,
              org.eclipse.osee.framework.core.enums.FileExtension.TXT.getFileExtension(),
              DisplayHint.MultiLine
           );

   /**
    * Attribute type for the Required Indicators Rational.
    * <p>
    * This attribute is used by the {@link CoreArtifactTypes#Controlled} artifact to document the rationale for the selected Required Indicators in the
    * {@link CoreAttributeTypes#DataRightsClassification} attribute for the artifact with controlled data.
    * <dl>
    * <dt>Display Name:</dt>
    * <dd>
    * <dl>
    * <dt>{@link CuiNamesConfiguration#STANDARD}:</dt>
    * <dd>Required Indicators Rational</dd>
    * <dt>{@link CuiNamesConfiguration#VERSION_ONE}:</dt>
    * <dd>Data Rights Basis</dd>
    * </dl>
    * <dt>Attribute Type Identifier:</dt>
    * <dd>72057594037928276L</dd>
    * </dd>
    * </dl>
    */

   AttributeTypeString DataRightsBasis =
      osee.createString
         (
            DataRightsBasisType::getConfiguredNameAndDescription,
            72057594037928276L,
            MediaType.TEXT_PLAIN,
            TaggerTypeToken.SENTINEL,
            NamespaceToken.OSEE,
            org.eclipse.osee.framework.core.enums.FileExtension.TXT.getFileExtension(),
            DisplayHint.MultiLine
         );

   /**
    * This attribute type for Required Indicators.
    * <p>
    * This attribute type is used by the following artifact types:
    * <dl>
    * <dt>{@link CoreArtifactTypes#Controlled}</dt>
    * <dd>Attributes of this attribute type specify the Required Indicator statements for a publishing containing the controlled artifact.</dd>
    * <dt>{@link CoreArtifactTypes#DataRightsConfiguration}</dt>
    * <dd>Attributes of this attribute type specify globally required Required Indicator statements for the publish.</dd>
    * </dt>
    * <dl>
    * <dt>Display Name:</dt>
    * <dl>
    * <dt>{@link CuiNameConfiguration#STANDARD}:</dt>
    * <dd>Required Indicators</dd>
    * <dt>{@link CuiNameConfiguration#VERSION_ONE}:</dt>
    * <dd>Data Rights Classification</dd>
    * </dl>
    * </dd>
    * <dt>Attribute Type Identifier:</dt>
    * <dd>1152921504606847317L</dt>
    * </dl>
    */

   DataRightsClassificationAttributeType DataRightsClassification =
      osee.createEnum
         (
            DataRightsClassificationAttributeType::new,
            DataRightsClassificationAttributeType::getConfiguredNameAndDescription,
            1152921504606847317L,
            MediaType.TEXT_PLAIN,
            NamespaceToken.OSEE,
            TaggerTypeToken.SENTINEL
         );

   /**
    * This attribute type is used by the following artifact types:
    * <dl>
    * <dt>{@link CoreArtifactTypes#Controlled}:</dt>
    * <dd>Attributes of this attribute type specify a CUI Limited Dissemination Control for the data contained in the controlled artifact.</dd>
    * <dt>{@link CoreArtifactTypes#DataRightsConfiguration}:</dt>
    * <dd>Attributes of this attribute type specify an allowed CUI Limited Dissemination Control for artifacts in a publish.</dd>
    * </dl>
    * <dl>
    * <dt>Display Name:</dt>
    * <dd>CUI Limited Dissemination Control</dd>
    * <dt>Attribute Type Identifier:</dt>
    * <dd>6036586745962781830L</dd>
    * </dl>
    */

   CuiLimitedDisseminationControlIndicatorAttributeType DataRightsCuiLimitedDisseminationControl =
      osee.createEnum
         (
            new CuiLimitedDisseminationControlIndicatorAttributeType()
         );


   /**
    * Attribute type for mapping Required Indicators to CUI Category and CUI Type pairs.
    * <p>
    * This attribute is used by the {@link CoreArtifactTypes#DataRightsConfiguration} artifact to associated the Required Indicators to the allowed CUI Category
    * and CUI Type pairs for the publish.
    * <dl>
    * <dt>Display Name:</dt>
    * <dd>CUI Category CUI Type pair to Required Indicator Map Entry.</dd>
    * <dt>Attribute Type Identifier:</dt>
    * <dd>428007622831294440L</dd>
    * </dl>
    */

   AttributeTypeMapEntry DataRightsRequiredIndicatorByCuiCategoryCuiTypeMapEntry =
      osee.createMapEntry
         (
            428007622831294440L,
            "CUI Category CUI Type pairs to Required Indicators Map Entry",
            "JSON that defines a key (CUI Category and CUI Type) value (Required Indicator) map entry.",
            "defaultKey",
            "defaultValue"
         );

   /**
    * Attribute type for specifying the Required Indicator Configuration to be used for a Required Indicator.
    * <p>
    * This attribute is used by the {@link CoreArtifactTypes#DataRightsConfigurationArtifact} to map Required Indicators for a
    * publish to the Required Indicator Configuration to be used for each Required Indicator. The keys must be the name of a
    * {@link RequiredIndicatorIndicator} enumeration member. The values must be the name of a
    * {@link CoreArtifactTypes#DataRightsRequiredIndicatorConfiguration} artifact.
    * <dl>
    * <dt>Display Name:<dt>
    * <dd>Required Indicator Configuration Entry</dd>
    * <dt>Attribute Type Identifier:</dt>
    * <dd>194620179570089921L</dd>
    * </dl>
    */

   AttributeTypeMapEntry DataRightsRequiredIndicatorConfigurationByRequiredIndicatorMapEntry =
      osee.createMapEntry
         (
            194620179570089921L,
            "Required Indicator Configuration Entry",
            "JSON that defines a Required Indicator (key) and the value Required Indicator Configuration (value) to use for the Required Indicator.",
            "defaultKey",
            "defaultValue"
         );

   /**
    * Attribute type for specifying the Footer Statement for a Required Indicator.
    * <p>
    * This attribute is used by the {@link CoreArtifactTypes#DataRightsRequiredIndicatorConfiguration} artifact
    * to specify the Footer Statement for a Required Indicator. The value of this attribute should be the name of an
    * {@link CoreArtifactTypes#DataRightsRequiredIndicatorStatement} artifact.
    * <dl>
    * <dt>Display Name:</dt>
    * <dd>Footer Statement</dd>
    * <dt>Attribute Type Identifier:</dt>
    * <dd>3591530711997647778L</dd>
    * </dl>
    */

   AttributeTypeString DataRightsRequiredIndicatorFooterStatement =
      osee.createString
         (
            3591530711997647778L,
            "Footer Statement",
            MediaType.TEXT_PLAIN,
            "Specifies the name of a \"Required Indicator Statement\" artifact containing the Statement to be used for the Required Indicator's footer.",
            DisplayHint.MultiLine
         );

   /**
    * Attribute type for specifying the frequency of a Required Indicator.
    * <p>
    * This attribute is used by the {@link CoreArtifactTypes#DataRightsRequiredIndicatorConfiguration} artifact to
    * specify the location (title page, header, footer) and frequency (every page, containing pages) for a Required
    * Indicator.
    * <dl>
    * <dt>Display Name:</dt>
    * <dd>Required Indicator Frequency</dd>
    * <dt>Attribute Type Identifier:</dt>
    * <dd>1722628299042865586L</dd>
    * </dl>
    */

   RequiredIndicatorFrequencyIndicatorAttributeType DataRightsRequiredIndicatorFrequencyIndicator =
      osee.createEnum
         (
            new RequiredIndicatorFrequencyIndicatorAttributeType( NamespaceToken.OSEE )
         );

   /**
    * Attribute type for specifying the Header Statement for a Required Indicator.
    * <p>
    * This attribute is used by the {@link CoreArtifactTypes#DataRightsRequiredIndicatorConfiguration} artifact
    * to specify the Header Statement for a Required Indicator. The value of this attribute should be the name of an
    * {@link CoreArtifactTypes#DataRightsRequiredIndicatorStatement} artifact.
    * <dl>
    * <dt>Display Name:</dt>
    * <dd>Header Statement</dd>
    * <dt>Attribute Type Identifier:</dt>
    * <dd>3199211351892189226L</dd>
    * </dl>
    */

   AttributeTypeString DataRightsRequiredIndicatorHeaderStatement =
      osee.createString
         (
            3199211351892189226L,
            "Header Statement",
            MediaType.TEXT_PLAIN,
            "Specifies the name of a \"Required Indicator Statement\" artifact containing the Statement to be used for the Required Indicator's header.",
            DisplayHint.MultiLine
         );

   /**
    * Attribute type for specifying a Required Indicator Statement in a particular format.
    * <p>
    * This attribute is used by the {@link CoreArtifactTypes#DataRightsRequiredIndicatorStatement} to specify a
    * Format Indicator (key) and a statement (value) in the specified format.
    * <dl>
    * <dt>Display Name:</dt>
    * <dd>Required Indicator Statement Entry</dd>
    * <dt>Attribute Type Identifier:</dt>
    * <dd>4689764541412006527L</dd>
    * <dl>
    */

   AttributeTypeMapEntry DataRightsRequiredIndicatorStatementEntry =
      osee.createMapEntry
         (
            4689764541412006527L,
            "Required Indicator Statement Entry",
            "JSON that defines a key (Statement Format) value (Statement Text) map entry.",
            "defaultKey",
            "defaultValue"
         );

   /**
    * Attribute type for specifying the Statement to be used on the title page for a Required Indicator.
    * <p>
    * This attribute is used by the {@link CoreArtifactTypes#DataRightsRequiredIndicatorConfiguration} artifact
    * to specify the Statement to be used on the title page for a Required Indicator. The value of this attribute
    *  should be the name of an {@link CoreArtifactTypes#DataRightsRequiredIndicatorStatement} artifact.
    * <dl>
    * <dt>Display Name:</dt>
    * <dd>Title Statement</dd>
    * <dt>Attribute Type Identifier:</dt>
    * <dd>4700977507184459371L</dd>
    * </dl>
    */

   AttributeTypeString DataRightsRequiredIndicatorTitleStatement =
      osee.createString
         (
            4700977507184459371L,
            "Title Statement",
            MediaType.TEXT_PLAIN,
            "Specifies the name of a \"Required Indicator Statement\" artifact containing the Statement to be used on the title page for the Required Indicator.",
            DisplayHint.MultiLine
         );

   /**
    * Attribute type for specifying the allowed countries for a CUI limited distribution list.
    * <p>
    * This attribute is used by the following artifact types:
    * <dl>
    * <dt>{@link CoreArtifactTypes#Controlled}:</dt>
    * <dd>Attributes of this type specify the countries the data in a controlled artifact are allowed to be distributed to.</dd>
    * <dt>{@link CoreArtifactTypes#DataRightsConfiguration}:</dt>
    * <dd>Attributes of this type specify the countries a publish is intended to be distributed to.</dd>
    * </dl>
    * <dl>
    * <dt>Display Name:</dt>
    * <dd>CUI Release List</dd>
    * <dt>Attribute Type Identifier:</dt>
    * <dd>8003115831873458510L</dd>
    * </dl>
    */

   TrigraphCountryCodeIndicatorAttributeType DataRightsTrigraphCountryCode =
      osee.createEnum
         (
            new TrigraphCountryCodeIndicatorAttributeType()
         );

   AttributeTypeBoolean DefaultGroup = osee.createBoolean(1152921504606847086L, "Default Group", MediaType.TEXT_PLAIN, "Specifies whether to automatically add new users into this group");

   AttributeTypeString DefaultMailServer = osee.createString(1152921504606847063L, "osee.Default Mail Server", MediaType.TEXT_PLAIN, "fully qualified name of the machine running the SMTP server which will be used by default for sending email");

   AttributeTypeString DefaultTrackingBranch = osee.createString(1152921504606847709L, "Default Tracking Branch", MediaType.TEXT_PLAIN, "");

   AttributeTypeString DefaultValue = osee.createString(2221435335730390044L, "Default Value", MediaType.TEXT_PLAIN, "");

   AttributeTypeString Description = osee.createString(1152921504606847090L, "Description", MediaType.TEXT_PLAIN, "");

   AttributeTypeBoolean Developmental = osee.createBooleanNoTag(1152921504606847137L, "Developmental", MediaType.TEXT_PLAIN, "");

   AttributeTypeString Dictionary = osee.createString(1152921504606847083L, "Dictionary", MediaType.TEXT_PLAIN, "");

   AttributeTypeBoolean DiffAvailable = osee.createBoolean(2822557585371250116L, "Diff Available", MediaType.TEXT_PLAIN, "Specifies if a report can show differences");

   AttributeTypeString DisplayText = osee.createStringNoTag(188458869981237L, "Display Text", MediaType.TEXT_PLAIN, "");

   AttributeTypeString DoorsHierarchy = osee.createString(1873562488122323009L, "Doors Hierarchy", MediaType.TEXT_PLAIN, "");

   AttributeTypeString DoorsId = osee.createString(8243262488122393232L, "Doors ID", MediaType.TEXT_PLAIN, "External doors id for import support", DisplayHint.SingleLine);

   AttributeTypeString DoorsModId = osee.createString(5326122488147393161L, "Doors Mod ID", MediaType.TEXT_PLAIN, "Modified External doors id for import support", DisplayHint.SingleLine );

   AttributeTypeString Effectivity = osee.createStringNoTag(1152921504606847108L, "Effectivity", MediaType.TEXT_PLAIN, "");

   AttributeTypeString ElapsedDate = osee.createString(1152921504606847296L, "Elapsed Date", MediaType.TEXT_PLAIN, "Time Elapsed from the start to the end of the script");

   AttributeTypeInteger ElapsedTime = osee.createInteger(6083892673634294710L, "Elapsed Time", MediaType.TEXT_PLAIN, "Elapsed Time in Milliseconds");

   AttributeTypeString Email = osee.createString(1152921504606847082L, "Email", MediaType.TEXT_PLAIN, "");

   AttributeTypeString EndpointUrl = osee.createString(1103659738810857581L, "Endpoint URL", MediaType.TEXT_PLAIN, "");

   AttributeTypeString ExcludePath = osee.createString(1152921504606847708L, "Exclude Path", MediaType.TEXT_PLAIN, "");

   AttributeTypeString ExecutedBy = osee.createString(1152921504606847377L, "Executed By", MediaType.TEXT_PLAIN, "");

   AttributeTypeString ExecutionEnvironment = osee.createString(8528756029132740740L, "Execution Environment", MediaType.TEXT_PLAIN, "Execution Environment");

   AttributeTypeDate ExecutionDate = osee.createDate(1152921504606847365L, "Execution Date", MediaType.TEXT_PLAIN, "Execution Date");

   AttributeTypeInteger ExecutionFrequency = osee.createInteger(5494590235875265429L, "Execution Frequency", MediaType.TEXT_PLAIN, "Frequency of parameterized command execution");

   AttributeTypeString Extension = osee.createString(1152921504606847064L, "Extension", MediaType.TEXT_PLAIN, "");

   FACETechStandardVersionAttributeType FACEVersion = osee.createEnum(new FACETechStandardVersionAttributeType());

   FACEOSSProfileAttributeType FACEProfile = osee.createEnum(new FACEOSSProfileAttributeType());

   FACESegmentAttributeType FACESegment = osee.createEnum(new FACESegmentAttributeType());

   AttributeTypeBoolean Favorite = osee.createBoolean(2516126323929150072L, "Favorite", MediaType.TEXT_PLAIN, "Favorite or not favorite");

   AttributeTypeString FavoriteBranch = osee.createStringNoTag(1152921504606847074L, "Favorite Branch", MediaType.TEXT_PLAIN, "");

   AttributeTypeString FaxPhone = osee.createString(1152921504606847081L, "Fax Phone", MediaType.TEXT_PLAIN, "");

   FdalAttributeType FDAL = osee.createEnum(new FdalAttributeType());

   AttributeTypeString FdalRationale = osee.createStringNoTag(926274413268034710L, "FDAL Rationale", MediaType.TEXT_PLAIN, "Functional Development Assurance Level Rationale");

   AttributeTypeBoolean FeatureMultivalued = osee.createBoolean(3641431177461038717L, "Feature Multivalued", MediaType.TEXT_PLAIN, "");

   FeatureValueAttributeType FeatureValueType = osee.createEnum(new FeatureValueAttributeType());

   AttributeTypeString ProductApplicability = osee.createString(4522673803793808650L,"Product Type",MediaType.TEXT_PLAIN, "");

   FileExtensionAttributeType FileExtension = osee.createEnum(new FileExtensionAttributeType());

   AttributeTypeString FileNamePrefix = osee.createString(1695022067194142778L, "File Name Prefix", MediaType.TEXT_PLAIN, "");

   AttributeTypeString FileSystemPath = osee.createString(1152921504606847707L, "File System Path", MediaType.TEXT_PLAIN, "");

   AttributeTypeString FunctionalCategory = osee.createString(1152921504606847871L, "Functional Category", MediaType.TEXT_PLAIN, "Functional Category in support of System Safety Report");

   FunctionalGroupingAttributeType FunctionalGrouping = osee.createEnum(new FunctionalGroupingAttributeType());

   AttributeTypeString GeneralStringData = osee.createStringNoTag(1152921504606847096L, "General String Data", MediaType.TEXT_PLAIN, "");

   GfeCfeAttributeType GfeCfe = osee.createEnum(new GfeCfeAttributeType());

   AttributeTypeString GitBuildId = osee.createString(1714059195608838442L, "Git Build-Id", MediaType.TEXT_PLAIN, "Build-Id embedded in Git commit message that is intended to be immutable even during rebase and amending the commit");

   AttributeTypeString GitChangeId = osee.createString(1152921504606847702L, "Git Change-Id", MediaType.TEXT_PLAIN, "Change-Id embedded in Git commit message that is intended to be immutable even during rebase and amending the commit");

   AttributeTypeDate GitCommitAuthorDate = osee.createDate(1152921504606847704L, "Git Commit Author Date", MediaType.TEXT_PLAIN, "when this commit was originally made");

   AttributeTypeString GitCommitMessage = osee.createString(1152921504606847705L, "Git Commit Message", MediaType.TEXT_PLAIN, "Full message minus Change-Id");

   AttributeTypeString GitCommitSha = osee.createString(1152921504606847703L, "Git Commit SHA", MediaType.TEXT_PLAIN, "SHA-1 checksum of the Git commit's content and header");

   AttributeTypeString GitRepoName = osee.createString(1152921504606847706L, "Git Repo Name", MediaType.TEXT_PLAIN, "Name of Relevant Git Repository");

   AttributeTypeString GitBranchName = osee.createString(1152921504606847819L, "Git Branch Name", MediaType.TEXT_PLAIN, "Name of Relevant Branch on Git Repository");

   AttributeTypeString GraphitiDiagram = osee.createStringNoTag(1152921504606847319L, "Graphiti Diagram", MediaType.TEXT_XML, "xml definition of an Eclipse Graphiti Diagram", "diagram");

   AttributeTypeString Hazard = osee.createString(1152921504606847138L, "Hazard", MediaType.TEXT_PLAIN, "");

   AttributeTypeString HtmlContent = osee.createString(1152921504606847869L, "HTML Content", MediaType.TEXT_HTML, "HTML format text must be a valid xhtml file");

   HttpMethodAttributeType HttpMethod = osee.createEnum(new HttpMethodAttributeType());

   AttributeTypeBoolean IaPlan = osee.createBoolean(1253931514616857210L, "IA Plan", MediaType.TEXT_PLAIN, "");

   IdalAttributeType IDAL = osee.createEnum(new IdalAttributeType());

   AttributeTypeString IdalRationale = osee.createStringNoTag(2517743638468399405L, "IDAL Rationale", MediaType.TEXT_PLAIN, "Item Development Assurance Level Rationale");

   AttributeTypeString IdValue = osee.createString(72057896045641815L, "Id Value", MediaType.TEXT_PLAIN, "Key-Value attribute where key (attribute id) is supplied by framework and value is supplied by user.", DisplayHint.SingleLine);

   AttributeTypeBoolean InterfaceMessageExclude = osee.createBoolean(2455059983007225811L, "Message Exclude", MediaType.TEXT_PLAIN, "");

   AttributeTypeString InterfaceMessageIoMode = osee.createString(2455059983007225813L, "Message IO Mode", MediaType.TEXT_PLAIN, "");

   AttributeTypeString InterfaceMessageModeCode = osee.createString(2455059983007225810L, "Message Mode Code", MediaType.TEXT_PLAIN, "");

   AttributeTypeString InterfaceMessageNumber = osee.createString(2455059983007225768L, "Interface Message Number", MediaType.TEXT_PLAIN, "");

   InterfaceMessagePeriodicityAttributeType InterfaceMessagePeriodicity = osee.createEnum(new InterfaceMessagePeriodicityAttributeType());

   InterfaceMessageRateAttributeType InterfaceMessageRate = osee.createEnum(new InterfaceMessageRateAttributeType());

   AttributeTypeString InterfaceMessageRateVer = osee.createString(2455059983007225805L, "Message Rate Ver", MediaType.TEXT_PLAIN, "");

   AttributeTypeString InterfaceMessagePriority = osee.createString(2455059983007225806L, "Message Priority", MediaType.TEXT_PLAIN, "");

   AttributeTypeString InterfaceMessageProtocol = osee.createString(2455059983007225809L, "Message Protocol", MediaType.TEXT_PLAIN, "");

   AttributeTypeString InterfaceMessageRptWordCount = osee.createString(2455059983007225807L, "Message Rpt Word Count", MediaType.TEXT_PLAIN, "");

   AttributeTypeString InterfaceMessageRptCmdWord = osee.createString(2455059983007225808L, "Message Rpt Cmd Word", MediaType.TEXT_PLAIN, "");

   AttributeTypeBoolean InterfaceMessageRunBeforeProc = osee.createBoolean(2455059983007225812L, "Message Run Before Proc", MediaType.TEXT_PLAIN, "");

   InterfaceMessageTypeAttributeType InterfaceMessageType = osee.createEnum(new InterfaceMessageTypeAttributeType());

   AttributeTypeString InterfaceMessageVer = osee.createString(2455059983007225804L, "Message Ver", MediaType.TEXT_PLAIN, "");

   AttributeTypeBoolean InterfaceMessageWriteAccess = osee.createBoolean(2455059983007225754L, "Interface Message Write Access", MediaType.TEXT_PLAIN, "Message has write access");

   AttributeTypeString InterfaceNodeCodeGenName= osee.createString(5390401355909179776L,"Interface Node Code Gen Name",MediaType.TEXT_PLAIN,"");

   AttributeTypeString InterfaceNodeAddress= osee.createString(5726596359647826656L,"Interface Node Address",MediaType.TEXT_PLAIN,"");

   AttributeTypeString InterfaceNodeGroupId= osee.createString(5726596359647826658L,"Interface Node Group Id",MediaType.TEXT_PLAIN,"");

   AttributeTypeString InterfaceNodeNumber= osee.createString(5726596359647826657L,"Interface Node Number",MediaType.TEXT_PLAIN,"");

   AttributeTypeString InterfaceNodeBackgroundColor = osee.createString(5221290120300474048L,"Interface Node Background Color",MediaType.TEXT_PLAIN,"");

   AttributeTypeString InterfaceNodeType= osee.createString(6981431177168910500L,"Interface Node Type",MediaType.TEXT_PLAIN,"");

   AttributeTypeBoolean InterfaceNodeCodeGen = osee.createBoolean(4980834335211418740L, "Interface Node Code Gen", MediaType.TEXT_PLAIN, "");

   AttributeTypeBoolean InterfaceNodeBuildCodeGen = osee.createBoolean(5806420174793066197L, "Interface Node Build Code Gen", MediaType.TEXT_PLAIN, "");

   AttributeTypeBoolean InterfaceNodeToolUse = osee.createBoolean(5863226088234748106L, "Interface Node Tool Use", MediaType.TEXT_PLAIN, "");

   InterfaceStructureCategoryAttribute InterfaceStructureCategory = osee.createEnum(new InterfaceStructureCategoryAttribute());

   AttributeTypeString InterfaceSubMessageNumber = osee.createString(2455059983007225769L, "Interface Sub Message Number", MediaType.TEXT_PLAIN, "");

   AttributeTypeString InterfaceMinSimultaneity = osee.createString(2455059983007225755L, "Interface Minimum Simultaneity", MediaType.TEXT_PLAIN, "");

   AttributeTypeString InterfaceMaxSimultaneity = osee.createString(2455059983007225756L, "Interface Maximum Simultaneity", MediaType.TEXT_PLAIN, "");

   AttributeTypeString InterfaceMinBytesPerSecond = osee.createString(2455059983007225757L, "Interface Minimum Bytes Per Second", MediaType.TEXT_PLAIN, "");

   AttributeTypeString InterfaceMaxBytesPerSecond = osee.createString(2455059983007225758L, "Interface Maximum Bytes Per Second", MediaType.TEXT_PLAIN, "");

   AttributeTypeInteger InterfaceTaskFileType = osee.createInteger(2455059983007225760L, "Interface Task File Type", MediaType.TEXT_PLAIN, "");

   AttributeTypeBoolean InterfaceElementAlterable = osee.createBoolean(2455059983007225788L, "Interface Element Alterable", MediaType.TEXT_PLAIN, "Element can be altered after creation.");

   AttributeTypeBoolean InterfaceElementArrayHeader = osee.createBoolean(3313203088521964923L, "Interface Element Array Header", MediaType.TEXT_PLAIN, "Element is an array header");

   AttributeTypeBoolean InterfaceElementWriteArrayHeaderName = osee.createBoolean(3313203088521964924L, "Interface Element Write Array Header Name", MediaType.TEXT_PLAIN, "Exports write the array header name");

   AttributeTypeInteger InterfaceElementIndexStart = osee.createInteger(2455059983007225801L, "Interface Element Index Start", MediaType.TEXT_PLAIN, "");

   AttributeTypeInteger InterfaceElementIndexEnd = osee.createInteger(2455059983007225802L, "Interface Element Index End", MediaType.TEXT_PLAIN, "");

   InterfaceLogicalTypeAttribute InterfaceLogicalType = osee.createEnum(new InterfaceLogicalTypeAttribute());

   AttributeTypeLong InterfaceEnumOrdinal = osee.createLong(2455059983007225790L, "Ordinal", MediaType.TEXT_PLAIN, "");

   AttributeTypeString InterfaceEnumOrdinalType = osee.createString(2664267173310317306L, "Ordinal Type", MediaType.TEXT_PLAIN, "");

   AttributeTypeString InterfaceElementEnumLiteral = osee.createString(2455059983007225803L, "Enum Literal", MediaType.TEXT_PLAIN, "");

   InterfacePlatformTypeUnitsAttribute InterfacePlatformTypeUnits = osee.createEnum(new InterfacePlatformTypeUnitsAttribute());

   AttributeTypeString InterfacePlatformTypeValidRangeDescription = osee.createString(2121416901992068417L, "Interface Platform Type Valid Range Desc", MediaType.TEXT_PLAIN, "");

   AttributeTypeString InterfacePlatformTypeMinval = osee.createString(3899709087455064782L, "Interface Platform Type Minval", MediaType.TEXT_PLAIN, "");

   AttributeTypeString InterfacePlatformTypeMaxval = osee.createString(3899709087455064783L, "Interface Platform Type Maxval", MediaType.TEXT_PLAIN, "");

   AttributeTypeString InterfacePlatformTypeBitSize = osee.createString(2455059983007225786L, "Interface Platform Type Bit Size", MediaType.TEXT_PLAIN, "");

   AttributeTypeBoolean InterfacePlatformType2sComplement = osee.createBoolean(3899709087455064784L, "Interface Platform Type 2sComplement", MediaType.TEXT_PLAIN, "Platform Type is 2's Complement");

   AttributeTypeString InterfaceDefaultValue = osee.createString(2886273464685805413L, "Interface Default Value", MediaType.TEXT_PLAIN, "");

   AttributeTypeString InterfacePlatformTypeMsbValue = osee.createString(3899709087455064785L, "Interface Platform Type Msb Value", MediaType.TEXT_PLAIN, "");

   AttributeTypeString InterfacePlatformTypeBitsResolution = osee.createString(3899709087455064786L, "Interface Platform Type Bits Resolution", MediaType.TEXT_PLAIN, "");

   AttributeTypeString InterfacePlatformTypeCompRate = osee.createString(3899709087455064787L, "Interface Platform Type Comp Rate", MediaType.TEXT_PLAIN, "");

   AttributeTypeString InterfacePlatformTypeAnalogAccuracy = osee.createString(3899709087455064788L, "Interface Platform Type Analog Accuracy", MediaType.TEXT_PLAIN, "");


   AttributeTypeString InterfaceUnitMeasurement = osee.createString(2478822847543373494L, "Interface Unit Measurement", MediaType.TEXT_PLAIN, "Measurement type of the unit");

   AttributeTypeString ImportTransportType = osee.createString(238254247108261698L, "Import Transport Types", MediaType.TEXT_PLAIN, "Transport Type of MIM Import");

   AttributeTypeString InterfaceTransportType = osee.createString(4522496963078776538L, "Interface Transport Type", MediaType.TEXT_PLAIN, "Transport Type of Interface Connection");

   AttributeTypeBoolean Interactive = osee.createBoolean(1152921504606847358L, "Interactive", MediaType.TEXT_PLAIN, "Is Interactive");

   AttributeTypeBoolean ByteAlignValidation = osee.createBoolean(1682639796635579163L, "Byte Align Validation", MediaType.TEXT_PLAIN, "Whether or not to use byte validation rules on a per-word basis.");

   AttributeTypeInteger ByteAlignValidationSize = osee.createInteger(6745328086388470469L, "Byte Align Validation Size", MediaType.TEXT_PLAIN, "Number of bytes used to validate word sizing if Byte Align Validation is on");

   AttributeTypeBoolean MessageGeneration = osee.createBoolean(6696101226215576386L, "Message Generation", MediaType.TEXT_PLAIN, "Whether or not to generate message information for MIM artifacts");

   AttributeTypeString MessageGenerationType = osee.createString(7121809480940961886L, "Message Generation Type", MediaType.TEXT_PLAIN, "Type of message information generation to use for MIM artifacts if Message Generation is true. Examples include Relational, Dynamic.");

   AttributeTypeString MessageGenerationPosition = osee.createString(7004358807289801815L, "Message Generation Position", MediaType.TEXT_PLAIN, "Location within a list for generation to use for MIM Artifacts if Message Generation is true. This is an array mapped to the related artifacts(NOTE: must be of the same artifact type). Position '0' is the first element in a list of elements. Position 'LAST' is the last element in a list of elements.");

   AttributeTypeString MinimumSubscriberMultiplicity = osee.createString(6433031401579983113L, "Minimum Subscriber Multiplicity", MediaType.TEXT_PLAIN, "Minimum # of subscribers on messages for connections of this transport type. Can be 0...n or n(any)");

   AttributeTypeString MaximumSubscriberMultiplicity = osee.createString(7284240818299786725L, "Maximum Subscriber Multiplicity", MediaType.TEXT_PLAIN, "Maximum # of subscribers on messages for connections of this transport type. Can be 0...n or n(any)");

   AttributeTypeString MinimumPublisherMultiplicity = osee.createString(7904304476851517L, "Minimum Publisher Multiplicity", MediaType.TEXT_PLAIN, "Minimum # of publishers on messages for connections of this transport type. Can be 0...n or n(any)");

   AttributeTypeString MaximumPublisherMultiplicity = osee.createString(8536169210675063038L, "Maximum Publisher Multiplicity", MediaType.TEXT_PLAIN, "Maximum # of publishers on messages for connections of this transport type. Can be 0...n or n(any)");

   AttributeTypeString InterfaceLevelsToUse = osee.createString(1668394842614655222L, "Interface Levels To Use", MediaType.TEXT_PLAIN, "String representation of an array of MIM artifact types in use ex. ['Connection','Node','Message','Submessage','Structure','Element','PlatformType','Enumeration Set','Enumeration']");

   AttributeTypeString AvailableMessageHeaders = osee.createString(2811393503797133191L, "Available Message Headers", MediaType.TEXT_PLAIN, "String representation of an array of headers available on the messages page ex. ['name','description','interfaceMessageNumber','interfaceMessagePeriodicity','interfaceMessageRate','interfaceMessageWriteAccess','interfaceMessageType','applicability'] Note: this is not only attributes available on messages, but also applicability and relations.");

   AttributeTypeString AvailableSubmessageHeaders = osee.createString(3432614776670156459L, "Available Submessage Headers", MediaType.TEXT_PLAIN, "String representation of an array of headers available on the messages page for submessages ex. ['name','description','interfaceSubMessageNumber',' ','applicability'] Note: this is not only attributes available on messages, but also applicability and relations. Empty String is a required field when submessages are being used as it is used for presentational reasons.");

   AttributeTypeString AvailableStructureHeaders = osee.createString(3020789555488549747L, "Available Structure Headers", MediaType.TEXT_PLAIN, "String representation of an array of headers available on the structures page ex. [' ','name','description','interfaceMinSimultaneity','interfaceMaxSimultaneity','interfaceTaskFileType','interfaceStructureCategory','sizeInBytes','bytesPerSecondMinimum','bytesPerSecondMaximum','applicability'] Note: this is not only attributes available on messages, but also applicability, relations and autogenned information. Empty String is a required field when structures are being used as it is used for presentational reasons.");

   AttributeTypeString AvailableElementHeaders = osee.createString(3757258106573748121L, "Available Element Headers", MediaType.TEXT_PLAIN, "String representation of an array of headers available on the structures page for elements ex. ['name','platformTypeName2','interfaceElementIndexStart','interfaceElementIndexEnd','interfacePlatformTypeMinval','interfacePlatformTypeMaxval','beginWord','endWord','beginByte','endByte','interfaceElementAlterable','notes','applicability','units','enumLiteral'] Note: this is not only attributes available on messages, but also applicability, relations and autogenned information.");

   AttributeTypeBoolean SpareAutoNumbering = osee.createBoolean(6696101226215576390L, "Spare Auto Numbering", MediaType.TEXT_PLAIN, "Whether or not to automatically number spares in exports");

   AttributeTypeBoolean DashedPresentation = osee.createBoolean(3564212740439618526L, "Dashed Presentation", MediaType.TEXT_PLAIN, "Whether or not the connection lines are dashed");

   AttributeTypeInputStream ImageContent = osee.createInputStreamNoTag(1152921504606847868L, "Image Content", AttributeTypeToken.IMAGE, "Binary Image content");

   AttributeTypeBoolean IsHelpPageHeader = osee.createBoolean(2037089520306879816L, "Is Help Page Header", MediaType.TEXT_PLAIN, "Determines if this help page is to be used as a navigation header");

   AttributeTypeBoolean IsTrainingPage = osee.createBoolean(1943912856510115199L, "Is Training Page", MediaType.TEXT_PLAIN, "Determines if this help page is used to track training completion");

   AttributeTypeBoolean IsValidated = osee.createBoolean(729356860089871L, "Is Validated", MediaType.TEXT_PLAIN, "");

   AttributeTypeString JavaCode = osee.createString(1253931606616948117L, "Java Code", MediaType.TEXT_PLAIN, "code that can be compiled into java");

   AttributeTypeString LastAuthor = osee.createString(1152921504606847285L, "Last Author", MediaType.TEXT_PLAIN, "Last Author");

   AttributeTypeDate LastModifiedDate = osee.createDate(1152921504606847286L, "Last Modified Date", AttributeTypeToken.TEXT_CALENDAR, "Last Modified");

   LegacyDalAttributeType LegacyDal = osee.createEnum(new LegacyDalAttributeType());

   AttributeTypeString LegacyId = osee.createStringNoTag(1152921504606847107L, "Legacy Id", MediaType.TEXT_PLAIN, "unique identifier from an external system");

   AttributeTypeString LoginId = osee.createString(239475839435799L, "Login Id", MediaType.TEXT_PLAIN, "");

   AttributeTypeString LogMessage = osee.createString(9053989332404948442L, "Log Message", MediaType.TEXT_PLAIN, "");

   AttributeTypeString LogThrowable = osee.createString(3817160350157885150L, "Log Throwable", MediaType.TEXT_PLAIN, "");

   AttributeTypeString LogLevel = osee.createString(4603623669127098982L, "Log Level", MediaType.TEXT_PLAIN, "");

   AttributeTypeString Logger = osee.createString(8924196735537621340L, "Logger", MediaType.TEXT_PLAIN, "");

   AttributeTypeInteger LocationId = osee.createInteger(7167403790289697715L, "Location Id", MediaType.TEXT_PLAIN, "");

   AttributeTypeLong LocationTime = osee.createLong(373826513060316605L, "Location Time", MediaType.TEXT_PLAIN, "");

   AttributeTypeString AttentionMessage = osee.createString(4657673604881393123L, "Attention Message", MediaType.TEXT_PLAIN, "");

   AttributeTypeString MachineName = osee.createString(1152921504606847359L, "Machine Name", MediaType.TEXT_PLAIN, "Machine Name");

   AttributeTypeString MaintainerText = osee.createStringNoTag(188458874335285L, "Maintainer Text", MediaType.TEXT_PLAIN, "");

   AttributeTypeString MimBranchPreferences = osee.createString(6600561480190271962L,"MIM Branch Preferences",MediaType.TEXT_PLAIN,"");

   AttributeTypeString MimColumnPreferences = osee.createString(5383153557691494043L,"MIM Column Preferences",MediaType.TEXT_PLAIN,"");

   AttributeTypeBoolean MimSettingWordWrap = osee.createBoolean(1640046550470950506L, "MIM Word Wrap", MediaType.TEXT_PLAIN, "");

   AttributeTypeString MobilePhone = osee.createString(1152921504606847080L, "Mobile Phone", MediaType.TEXT_PLAIN, "");

   AttributeTypeString ModifiedFlag = osee.createString(1152921504606847284L, "Modified Flag", MediaType.TEXT_PLAIN, "File Modification Flag from Repository");

   AttributeTypeString Name = osee.createString(1152921504606847088L, "Name", MediaType.TEXT_PLAIN, "Descriptive Name");

   AttributeTypeString NameAbbrev= osee.createString(8355308043647703563L,"Name Abbrev",MediaType.TEXT_PLAIN,"");

   AttributeTypeInputStream NativeContent = osee.createInputStreamNoTag(1152921504606847097L, "Native Content", MediaType.APPLICATION_OCTET_STREAM, "content that will be edited by a native program");

   AttributeTypeString Notes = osee.createString(1152921504606847085L, "Notes", MediaType.TEXT_PLAIN, "");

   AttributeTypeString ObjectName = osee.createString(336479226773047054L, "Object Name", MediaType.TEXT_PLAIN, "");

   AttributeTypeString MethodName = osee.createString(5860179243644665094L, "Method Name", MediaType.TEXT_PLAIN, "");

   AttributeTypeString TraceEnd = osee.createString(8326319167119225936L, "Trace End", MediaType.TEXT_PLAIN, "");

   AttributeTypeString OsArchitecture = osee.createString(1152921504606847287L, "OS Architecture", MediaType.TEXT_PLAIN, "OS Architecture");

   AttributeTypeString OsName = osee.createString(1152921504606847288L, "OS Name", MediaType.TEXT_PLAIN, "OS Name");

   AttributeTypeString OsVersion = osee.createString(1152921504606847289L, "OS Version", MediaType.TEXT_PLAIN, "OS Version");

   AttributeTypeString OseeServerJarVersion = osee.createString(1152921504606847292L, "OSEE Server Jar Version", MediaType.TEXT_PLAIN, "OSEE Server Jar Version");

   AttributeTypeString OseeServerTitle = osee.createString(1152921504606847291L, "OSEE Server Title", MediaType.TEXT_PLAIN, "OSEE Server Title");

   AttributeTypeString OseeVersion = osee.createString(1152921504606847290L, "OSEE Version", MediaType.TEXT_PLAIN, "OSEE Version");

   AttributeTypeString OseeAppDefinition = osee.createStringNoTag(1152921504606847380L, "Osee App Definition", MediaType.APPLICATION_JSON, "Json that defines the parameters, action(s), and metadata of an OSEE Single Page App");
   AttributeTypeString JavaVersion = osee.createStringNoTag(1152921504606849836L, "Java Version", MediaType.APPLICATION_JSON, "Java Version");
   PageOrientationAttributeType PageOrientation = osee.createEnum(new PageOrientationAttributeType());

   AttributeTypeString ParagraphNumber = osee.createString(1152921504606847101L, "Paragraph Number", MediaType.TEXT_PLAIN, "This is the corresponding section number from the outline of document from which this artifact was imported");

   AttributeTypeString ParameterizedCommand = osee.createString(8062747461195678171L, "Parameterized Command", MediaType.APPLICATION_JSON, "The JSON representation of the command with its parameterized values");

   PartitionAttributeType Partition = osee.createEnum(new PartitionAttributeType());

   AttributeTypeInteger PassedCount = osee.createInteger(1152921504606847297L, "Passed Count", MediaType.TEXT_PLAIN, "Number of points that passed");

   AttributeTypeInteger FailedCount = osee.createInteger(1152921504606847298L, "Failed Count", MediaType.TEXT_PLAIN, "Number of points that failed");

   AttributeTypeInteger InteractiveCount = osee.createInteger(2668143934534161825L, "Interactive Count", MediaType.TEXT_PLAIN, "Number of points that are interactive");

   AttributeTypeBoolean ScriptAborted = osee.createBoolean(1152921504606847300L, "Script Aborted", MediaType.TEXT_PLAIN, "Test Abort status");

   AttributeTypeString Phone = osee.createString(1152921504606847079L, "Phone", MediaType.TEXT_PLAIN, "");

   AttributeTypeString PlainTextContent = osee.createString(1152921504606847866L, "Plain Text Content", MediaType.TEXT_PLAIN, "plain text file");

   AttributeTypeString MarkdownContent = osee.createString(1152921504606847900L, "Markdown Content", "text/markdown", "text in markdown format", DisplayHint.MultiLine);

   AttributeTypeBoolean PotentialSecurityImpact = osee.createBoolean(1152921504606847109L, "Potential Security Impact", MediaType.TEXT_PLAIN, "");

   AttributeTypeString PrimaryAttribute = osee.createString(298564230602257170L, "Primary Attribute", MediaType.TEXT_PLAIN, "");

   AttributeTypeString ProcessorId = osee.createString(1152921504606847293L, "Processor ID", MediaType.TEXT_PLAIN, "Processor ID");

   ProducesMediaTypeAttributeType ProducesMediaType = osee.createEnum(new ProducesMediaTypeAttributeType());

   AttributeTypeString ProductLinePreferences = osee.createStringNoTag(582562585958993670L, "Product Line Preferences", MediaType.TEXT_PLAIN, "");

   AttributeTypeString SetId = osee.createString(1152921504606847350L, "Set Id", MediaType.TEXT_PLAIN, "CI Set Artifact Id");

   AttributeTypeString PropertyKey = osee.createString(5139071591277404578L, "Property Key", MediaType.TEXT_PLAIN, "Property Store Key");

   AttributeTypeMapEntry PublishingTemplateContentByFormatMapEntry = osee.createMapEntry(81484999873657204L, "Publishing Template Content By Format Map Entry", "Json that defines a key (Format) value (Publishing Template Content) map entry.", "defaultKey", "defaultValue" );

   AttributeTypeString PublishingTemplateDataRightsConfigurationNameReference = osee.createString(6329223727577326200L, "Data Rights Configuration", MediaType.TEXT_PLAIN, "Specifies the data rights configuration to use for a publish.", DisplayHint.SingleLine);

   AttributeTypeBoolean PublishInline = osee.createBoolean(1152921504606847122L, "PublishInline", MediaType.TEXT_PLAIN, "");

   QualificationMethodAttributeType QualificationMethod = osee.createEnum(new QualificationMethodAttributeType());

   AttributeTypeString QualificationLevel = osee.createString(1152921504606847305L, "Qualification Level", MediaType.TEXT_PLAIN, "Qualification level");

   AttributeTypeString RelationOrder = osee.createStringNoTag(1152921504606847089L, "Relation Order", MediaType.TEXT_PLAIN, "Defines relation ordering information", DisplayHint.MultiLine);

   AttributeTypeString RendererOptions = osee.createString(904L, "Renderer Options", MediaType.APPLICATION_JSON, "", "txt");

   AttributeTypeString RepositoryType = osee.createString(8150083798685627257L, "Repository Type", MediaType.TEXT_PLAIN, "");

   AttributeTypeString RepositoryUrl = osee.createString(1152921504606847700L, "Repository URL", MediaType.TEXT_PLAIN, "");

   AttributeTypeBoolean RequireConfirmation = osee.createBooleanNoTag(188458869981239L, "Require Confirmation", MediaType.TEXT_PLAIN, "");

   AttributeTypeBoolean RequiresValidation = osee.createBoolean(2822557585371250127L, "Requires Validation", MediaType.TEXT_PLAIN, "Specifies if a report requires validation before running");

   AttributeTypeBoolean Scheduled = osee.createBoolean(1152921504606847360L, "Scheduled", MediaType.TEXT_PLAIN, "Should the script be run again");

   AttributeTypeDate ScheduledTime = osee.createDate(1152921504606847361L, "Scheduled Time", MediaType.TEXT_PLAIN, "Scheduled time to rerun script");

   AttributeTypeString ScheduledMachine = osee.createString(527991454069746927L, "Scheduled Machine", MediaType.TEXT_PLAIN, "Machine the script should run on next");

   AttributeTypeString Result = osee.createString(1152921504606847364L, "Result", MediaType.TEXT_PLAIN, "Result");

   AttributeTypeString ResultType = osee.createString(1152921504606847374L, "Result Type", MediaType.TEXT_PLAIN, "Result Type");

   AttributeTypeInteger ReviewId = osee.createInteger(1152921504606847245L, "Review Id", MediaType.TEXT_PLAIN, "");

   AttributeTypeString ReviewStoryId = osee.createString(1152921504606847246L, "Review Story Id", MediaType.TEXT_PLAIN, "");

   AttributeTypeString Revision = osee.createString(1152921504606847283L, "Revision", MediaType.TEXT_PLAIN, "Version");

   AttributeTypeString RuntimeVersion = osee.createString(2638318397467121190L, "Runtime Version", MediaType.TEXT_PLAIN, "Version");

   AttributeTypeString SFHA = osee.createString(1152921504606847140L, "SFHA", MediaType.TEXT_PLAIN, "");

   AttributeTypeBoolean Safety = osee.createBoolean(1152921504606847357L, "Safety", MediaType.TEXT_PLAIN, "Safety");

   AttributeTypeString SafetyImpact = osee.createString(1684721504606847095L, "Safety Impact", MediaType.TEXT_PLAIN, "");

   SafetySeverityAttributeType SafetySeverity = osee.createEnum(new SafetySeverityAttributeType());

   SeverityCategoryAttributeType SeverityCategory = osee.createEnum(new SeverityCategoryAttributeType());

   AttributeTypeInteger ScriptHealth = osee.createInteger(1152921504606847367L, "Script Health", MediaType.TEXT_PLAIN, "Script Health");

   AttributeTypeString ScriptName = osee.createString(1152921504606847353L, "Script Name", MediaType.TEXT_PLAIN, "Full Name of Script Run");
   AttributeTypeString ScriptSubsystem = osee.createString(1152921504606848173L, "Script Subsystem", MediaType.TEXT_PLAIN, "Subsystem the script tests");
   SoftwareControlCategoryAttributeType SoftwareControlCategory = osee.createEnum(new SoftwareControlCategoryAttributeType());

   AttributeTypeString SoftwareControlCategoryRationale = osee.createStringNoTag(750929222178534710L, "Software Control Category Rationale", MediaType.TEXT_PLAIN, "");

   AttributeTypeInteger SummaryId = osee.createInteger(3162538699764608039L, "Summary Id", MediaType.TEXT_PLAIN, "");

   SoftwareCriticalityIndexAttributeType SwCI = osee.createEnum(new SoftwareCriticalityIndexAttributeType());

   ComputedSoftwareCriticalityIndex SoftwareCriticalityIndex = osee.createComp(ComputedSoftwareCriticalityIndex::new, 1152921504606847725L, "Safety Criticality Index", "Calculation of SwCI using Safety Severity and Software Control Category", SoftwareControlCategory, SafetySeverity);

   AttributeTypeBoolean SoftwareSafetyImpact = osee.createBooleanNoTag(8318805403746485981L, "Software Safety Impact", MediaType.TEXT_PLAIN, "Software Safety Impact");
   AttributeTypeString GroupType = osee.createStringNoTag(750929222178593862L, "Group Type", MediaType.TEXT_PLAIN, "Info Group Type");
   AttributeTypeDate StartDate = osee.createDate(1152921504606847294L, "Start Date", AttributeTypeToken.TEXT_CALENDAR, "Start Date");

   AttributeTypeDate EndDate = osee.createDate(1152921504606847295L, "End Date", AttributeTypeToken.TEXT_CALENDAR, "Stop Date");

   AttributeTypeInteger StartNumber = osee.createInteger(6723773741627566250L, "Start Number", MediaType.TEXT_PLAIN, "");

   AttributeTypeInteger InformationalCount = osee.createInteger(1699317482637032631L, "Informational Count", MediaType.TEXT_PLAIN, "");

   AttributeTypeInteger MinorCount = osee.createInteger(4794674762028707668L, "Minor Count", MediaType.TEXT_PLAIN, "");

   AttributeTypeInteger SeriousCount = osee.createInteger(2525855002274824867L, "Serious Count", MediaType.TEXT_PLAIN, "");

   AttributeTypeInteger CriticalCount = osee.createInteger(2323326159003010092L, "Critical Count", MediaType.TEXT_PLAIN, "");

   AttributeTypeInteger ExceptionCount = osee.createInteger(2003086737979123501L, "Exception Count", MediaType.TEXT_PLAIN, "");

   AttributeTypeString ErrorSeverity = osee.createString(1976422954176720594L, "Error Severity", MediaType.TEXT_PLAIN, "");

   AttributeTypeString ErrorVersion = osee.createString(8487464372724189608L, "Error Version", MediaType.TEXT_PLAIN, "");

   AttributeTypeInteger ErrorCount = osee.createInteger(1109939132981873357L, "Error Count", MediaType.TEXT_PLAIN, "");

   AttributeTypeString StartPage = osee.createStringNoTag(1152921504606847135L, "osee.wi.Start Page", MediaType.TEXT_PLAIN, "");

   AttributeTypeString State = osee.createString(1152921504606847070L, "State", MediaType.TEXT_PLAIN, "");

   AttributeTypeString StaticId = osee.createString(1152921504606847095L, "Static Id", MediaType.TEXT_PLAIN, "", DisplayHint.SingleLine);

   AttributeTypeString StatusBy = osee.createString(1152921504606847362L, "Status By", MediaType.TEXT_PLAIN, "Status By");

   AttributeTypeDate StatusDate = osee.createDate(1152921504606847363L, "Status Date", MediaType.TEXT_PLAIN, "Status Date");

   AttributeTypeString Street = osee.createString(1152921504606847069L, "Street", MediaType.TEXT_PLAIN, "");

   AttributeTypeInteger StackTraceLine = osee.createInteger(3884112593403558397L, "Stack Trace Line", MediaType.TEXT_PLAIN, "Stack Trace Line");
   AttributeTypeString StackTraceSource = osee.createString(3884112593403558184L, "Stack Trace Source", MediaType.TEXT_PLAIN, "Stack Trace Source");
   AttributeTypeString SubjectMatterExpert = osee.createString(72057594037928275L, "Subject Matter Expert", MediaType.TEXT_PLAIN, "Name of the Subject Matter Expert");

   SubsystemAttributeType Subsystem = osee.createEnum(new SubsystemAttributeType());

   AttributeTypeString TeamName = osee.createString(1152921504606847354L, "Team", MediaType.TEXT_PLAIN, "Team");

   AttributeTypeBoolean TechnicalPerformanceParameter = osee.createBooleanNoTag(1152921504606847123L, "Technical Performance Parameter", MediaType.TEXT_PLAIN, "");

   AttributeTypeString TemplateMatchCriteria = osee.createString(1152921504606847087L, "Template Match Criteria", MediaType.TEXT_PLAIN, "Criteria that determines what template is selected ie: 'Render Artifact PresentationType Option'");

   AttributeTypeString TestPointGroupName = osee.createString(1152921504606847375L, "Script Group Name", MediaType.TEXT_PLAIN, "Script Group Name");

   AttributeTypeString TestPointGroupType = osee.createString(1152921504606847376L, "Script Group Name", MediaType.TEXT_PLAIN, "Script Group Name");

   AttributeTypeString TestPointGroupOperator = osee.createString(1152921504606847366L, "Script Group Name", MediaType.TEXT_PLAIN, "Script Group Name");

   AttributeTypeDouble TestNumber = osee.createDouble(1152921504606847369L, "Script Errors", MediaType.TEXT_PLAIN, "Script Errors");

   AttributeTypeString TestPointRequirement = osee.createString(750383587408069958L, "Test Point Requirement", MediaType.TEXT_PLAIN, "");

   AttributeTypeInteger TransmissionCount = osee.createInteger(6150912720805220879L, "Transmission Count", MediaType.TEXT_PLAIN, "Number Of Transmissions");

   TestProcedureStatusAttributeType TestProcedureStatus = osee.createEnum(new TestProcedureStatusAttributeType());

   AttributeTypeString TestScriptGuid = osee.createString(1152921504606847301L, "Test Script GUID", MediaType.TEXT_PLAIN, "Test Case GUID");

   TisTestCategoryAttributeType TisTestCategory = osee.createEnumNoTag(new TisTestCategoryAttributeType());

   AttributeTypeString TisTestNumber = osee.createStringNoTag(1152921504606847116L, "TIS Test Number", MediaType.TEXT_PLAIN, "Test Number");

   TisTestTypeAttributeType TisTestType = osee.createEnumNoTag(new TisTestTypeAttributeType());

   AttributeTypeInteger TotalTestPoints = osee.createInteger(1152921504606847299L, "Total Test Points", MediaType.TEXT_PLAIN, "Total test points");

   AttributeTypeString UriGeneralStringData = osee.createStringNoTag(1152921504606847381L, "Uri General String Data", AttributeTypeToken.TEXT_URI_LIST, "");

   AttributeTypeArtifactId UserArtifactId = osee.createArtifactIdNoTag(1152921504606847701L, "User Artifact Id", MediaType.TEXT_PLAIN, "Artifact id of an artifact of type User");

   AttributeTypeString UserId = osee.createString(1152921504606847073L, "User Id", MediaType.TEXT_PLAIN, "");
   AttributeTypeString UserName = osee.createString(1152921504606848975L, "User Name", MediaType.TEXT_PLAIN, "");
   AttributeTypeString UserSettings = osee.createString(1152921504606847076L, "User Settings", MediaType.TEXT_PLAIN, "", XML.getFileExtension());

   AttributeTypeBoolean UseValidator = osee.createBoolean(322346571838162L, "Is Validator Used", MediaType.TEXT_PLAIN, "");

   AttributeTypeString Value = osee.createString(861995499338466438L, "Value", MediaType.TEXT_PLAIN, "");

   AttributeTypeString ValidatorType = osee.createString(432553480318267424L, "Validator Type", MediaType.TEXT_PLAIN, "");

   AttributeTypeString VerificationAcceptanceCriteria = osee.createStringNoTag(1152921504606847117L, "Verification Acceptance Criteria", MediaType.TEXT_PLAIN, "");

   VerificationEventAttributeType VerificationEvent = osee.createEnum(new VerificationEventAttributeType());

   VerificationLevelAttributeType VerificationLevel = osee.createEnum(new VerificationLevelAttributeType());

   AttributeTypeString VersionInfo = osee.createString(8843767590097397663L, "Version Info", MediaType.TEXT_PLAIN, "");

   AttributeTypeString VersionUnit = osee.createString(677533385360465714L, "Version Unit", MediaType.TEXT_PLAIN, "");

   AttributeTypeBoolean UnderTest = osee.createBoolean(5925046570190903466L, "Under Test", MediaType.TEXT_PLAIN, "");

   AttributeTypeString WebPreferences = osee.createString(1152921504606847386L, "Web Preferences", MediaType.TEXT_PLAIN, "", XML.getFileExtension(), DisplayHint.MultiLine);

   AttributeTypeString Website = osee.createString(1152921504606847084L, "Website", AttributeTypeToken.TEXT_URI_LIST, "");

   AttributeTypeString WholeWordContent = osee.createString(1152921504606847099L, "Whole Word Content", AttributeTypeToken.APPLICATION_MSWORD, "value must comply with WordML xml schema", DisplayHint.NoGeneralRender);

   AttributeTypeString Witness = osee.createString(1152921504606847378L, "Witness", MediaType.TEXT_PLAIN, "");

   AttributeTypeString WordOleData = osee.createStringNoTag(1152921504606847092L, "Word Ole Data", AttributeTypeToken.APPLICATION_MSWORD, "Word Ole Data");

   AttributeTypeString WordTemplateContent = osee.createString(1152921504606847098L, "Word Template Content", AttributeTypeToken.APPLICATION_MSWORD, "value must comply with WordML xml schema", DisplayHint.MultiLine);

   AttributeTypeString WorkData = osee.createStringNoTag(1152921504606847126L, "osee.wi.Work Data", MediaType.TEXT_XML, "");

   AttributeTypeString WorkDescription = osee.createStringNoTag(1152921504606847129L, "osee.wi.Work Description", MediaType.TEXT_PLAIN, "");

   AttributeTypeString WorkId = osee.createStringNoTag(1152921504606847127L, "osee.wi.Work Id", MediaType.TEXT_PLAIN, "");

   AttributeTypeString WorkParentId = osee.createStringNoTag(1152921504606847130L, "osee.wi.Work Parent Id", MediaType.TEXT_PLAIN, "");

   AttributeTypeString WorkTransition = osee.createStringNoTag(1152921504606847133L, "osee.wi.Work Transition", MediaType.TEXT_PLAIN, "");

   AttributeTypeString WorkType = osee.createStringNoTag(1152921504606847128L, "osee.wi.Work Type", MediaType.TEXT_PLAIN, "");

   AttributeTypeString XViewerCustomization = osee.createString(1152921504606847077L, "XViewer Customization", MediaType.TEXT_XML, "");

   AttributeTypeString XViewerDefaults = osee.createString(1152921504606847078L, "XViewer Defaults", MediaType.TEXT_PLAIN, "");

   AttributeTypeString Zip = osee.createString(1152921504606847071L, "Zip", MediaType.TEXT_PLAIN, "");

   AttributeTypeJoin NameWord = osee.attributeTypeJoin("Name and Word", Name, CoreAttributeTypes.WordTemplateContent);

   // @formatter:on
}
