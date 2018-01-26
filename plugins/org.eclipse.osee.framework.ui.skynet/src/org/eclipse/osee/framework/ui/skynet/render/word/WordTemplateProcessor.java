/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.framework.ui.skynet.render.word;

import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Partition;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.SeverityCategory;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.WordTemplateContent;
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import static org.eclipse.osee.framework.core.enums.DeletionFlag.EXCLUDE_DELETED;
import static org.eclipse.osee.framework.core.enums.PresentationType.PREVIEW;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.CharacterCodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ws.rs.core.MediaType;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.datarights.DataRightResult;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.core.util.PageOrientation;
import org.eclipse.osee.framework.core.util.RendererOption;
import org.eclipse.osee.framework.core.util.RendererUtil;
import org.eclipse.osee.framework.core.util.ReportConstants;
import org.eclipse.osee.framework.core.util.WordMLProducer;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.CharBackedInputStream;
import org.eclipse.osee.framework.jdk.core.util.xml.Xml;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.AIFile;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoader;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;
import org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer;
import org.eclipse.osee.framework.ui.skynet.util.WordUiUtil;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.orcs.rest.client.OseeClient;
import org.eclipse.osee.orcs.rest.model.ApplicabilityEndpoint;
import org.eclipse.swt.program.Program;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Robert A. Fisher
 * @author Jeff C. Phillips
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 * @link WordTemplateProcessorTest
 */
public class WordTemplateProcessor {

   private static final String LOAD_EXCLUDED_ARTIFACTIDS =
      "select art_id from osee_artifact art, osee_txs txs where art.gamma_id = txs.gamma_id and txs.branch_id = ? and txs.tx_current = 1 and not exists (select null from osee_tuple2 t2, osee_txs txsP where tuple_type = 2 and e1 = ? and t2.gamma_id = txsP.gamma_id and txsP.branch_id = ? and txsP.tx_current = 1 and e2 = txs.app_id)";
   private static final String ARTIFACT = "Artifact";
   private static final String ARTIFACT_TYPE = "Artifact Type";
   private static final Object ARTIFACT_ID = "Artifact Id";
   private static final String APPLICABILITY = "Applicability";
   private static final String INSERT_LINK = "INSERT_LINK_HERE";
   private static final String INSERT_ARTIFACT_HERE = "INSERT_ARTIFACT_HERE";
   private static final String NESTED_TEMPLATE = "NestedTemplate";
   public static final String PGNUMTYPE_START_1 = "<w:pgNumType [^>]*w:start=\"1\"/>";
   public static final String STYLES = "<w:lists>.*?</w:lists><w:styles>.*?</w:styles>";

   private static final Pattern headElementsPattern =
      Pattern.compile("(" + INSERT_ARTIFACT_HERE + ")" + "|" + INSERT_LINK,
         Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);

   private static final Program wordApp = Program.findProgram("doc");

   private String slaveTemplate;
   private String slaveTemplateOptions;
   private String slaveTemplateStyles;

   private String elementType;
   private String overrideClassification;
   private BranchId branch;

   //Outlining Options
   private AttributeTypeId headingAttributeType;
   private boolean outlining;
   private boolean recurseChildren;
   private String outlineNumber;

   //Attribute Options
   private String attributeLabel;
   private String attributeType;
   private String formatPre;
   private String formatPost;

   //Metadata Options
   private String metadataType;
   private String metadataLabel;
   private String metadataFormat;

   //Nested Template Options
   private String outlineType;
   private String sectionNumber;
   private String subDocName;
   private String key;
   private String value;
   private int nestedCount;

   private final List<AttributeElement> attributeElements = new LinkedList<>();
   private final List<MetadataElement> metadataElements = new LinkedList<>();
   final List<Artifact> nonTemplateArtifacts = new LinkedList<>();
   private final Set<String> ignoreAttributeExtensions = new HashSet<>();
   private final Set<Artifact> processedArtifacts = new HashSet<>();
   private final WordTemplateRenderer renderer;
   private boolean isDiff;
   private boolean excludeFolders;
   private CharSequence paragraphNumber = null;
   private IArtifactType[] excludeArtifactTypes = {};
   private HashMap<ApplicabilityId, ApplicabilityToken> applicabilityTokens;
   private HashMap<ArtifactId, ArtifactId> artifactsToExclude;

   public WordTemplateProcessor(WordTemplateRenderer renderer) {
      this.renderer = renderer;
      loadIgnoreAttributeExtensions();
      artifactsToExclude = new HashMap<>();
   }

   /**
    * Parse through template to find xml defining artifact sets and replace it with the result of publishing those
    * artifacts Only used by Publish SRS
    */
   public void publishWithNestedTemplates(Artifact masterTemplateArtifact, Artifact slaveTemplateArtifact, List<Artifact> artifacts) {
      nestedCount = 0;
      String masterTemplate = masterTemplateArtifact.getSoleAttributeValue(CoreAttributeTypes.WholeWordContent, "");
      String masterTemplateOptions =
         masterTemplateArtifact.getSoleAttributeValue(CoreAttributeTypes.RendererOptions, "");
      slaveTemplate = "";
      slaveTemplateOptions = "";
      isDiff = (boolean) renderer.getRendererOptionValue(RendererOption.PUBLISH_DIFF);
      renderer.updateOption(RendererOption.TEMPLATE_ARTIFACT, masterTemplateArtifact);

      slaveTemplateStyles = "";
      if (slaveTemplateArtifact != null) {
         renderer.updateOption(RendererOption.TEMPLATE_ARTIFACT, slaveTemplateArtifact);
         slaveTemplate = slaveTemplateArtifact.getSoleAttributeValue(CoreAttributeTypes.WholeWordContent, "");
         slaveTemplateOptions =
            slaveTemplateArtifact.getSoleAttributeValueAsString(CoreAttributeTypes.RendererOptions, "");

         List<Artifact> slaveTemplateRelatedArtifacts =
            slaveTemplateArtifact.getRelatedArtifacts(CoreRelationTypes.SupportingInfo_SupportingInfo);

         if (slaveTemplateRelatedArtifacts != null) {
            if (slaveTemplateRelatedArtifacts.size() == 1) {
               slaveTemplateStyles += slaveTemplateRelatedArtifacts.get(0).getSoleAttributeValueAsString(
                  CoreAttributeTypes.WholeWordContent, "");
            } else {
               OseeLog.log(this.getClass(), Level.INFO,
                  "More than one style relation currently not supported. Defaulting to styles defined in the template.");
            }
         }

      }

      try {
         attributeElements.clear();
         metadataElements.clear();
         JSONObject jsonObject = new JSONObject(masterTemplateOptions);
         elementType = jsonObject.getString("ElementType");
         if (elementType.equals(ARTIFACT)) {
            parseAttributeOptions(masterTemplateOptions);
            parseMetadataOptions(masterTemplateOptions);
         }
      } catch (JSONException ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
      // Need to check if all attributes will be published.  If so set the AllAttributes option.
      // Assumes that all (*) will not be used when other attributes are specified
      renderer.updateOption(RendererOption.ALL_ATTRIBUTES, false);
      if (attributeElements.size() == 1) {
         String attributeName = attributeElements.get(0).getAttributeName();
         if (attributeName.equals("*")) {
            renderer.updateOption(RendererOption.ALL_ATTRIBUTES, true);
         }
      }
      List<Artifact> masterTemplateRelatedArtifacts =
         masterTemplateArtifact.getRelatedArtifacts(CoreRelationTypes.SupportingInfo_SupportingInfo);
      String masterTemplateStyles = "";

      if (masterTemplateRelatedArtifacts != null && !masterTemplateRelatedArtifacts.isEmpty()) {
         if (masterTemplateRelatedArtifacts.size() == 1) {
            masterTemplateStyles += masterTemplateRelatedArtifacts.get(0).getSoleAttributeValueAsString(
               CoreAttributeTypes.WholeWordContent, "");
         } else {
            OseeLog.log(this.getClass(), Level.INFO,
               "More than one style relation currently not supported. Defaulting to styles defined in the template.");
         }
      }

      getExcludeArtifactTypes();

      IFile file = RendererUtil.getRenderFile(COMMON, PREVIEW, "/", masterTemplateArtifact.getSafeName(), ".xml");
      renderer.updateOption(RendererOption.RESULT_PATH_RETURN, file.getLocation().toOSString());

      AIFile.writeToFile(file, applyTemplate(artifacts, masterTemplate, masterTemplateOptions, masterTemplateStyles,
         file.getParent(), null, null, PREVIEW));

      if (!((boolean) renderer.getRendererOptionValue(RendererOption.NO_DISPLAY)) && !isDiff) {
         RenderingUtil.ensureFilenameLimit(file);
         wordApp.execute(file.getLocation().toFile().getAbsolutePath());
      }
   }

   private IArtifactType[] getExcludeArtifactTypes() {
      excludeArtifactTypes = null;

      Object o = renderer.getRendererOptionValue(RendererOption.EXCLUDE_ARTIFACT_TYPES);
      if (o instanceof Collection<?>) {
         Collection<?> coll = (Collection<?>) o;
         excludeArtifactTypes = new IArtifactType[coll.size()];
         int i = 0;
         Iterator<?> iterator = coll.iterator();
         while (iterator.hasNext()) {
            Object next = iterator.next();
            if (next instanceof IArtifactType) {
               excludeArtifactTypes[i] = (IArtifactType) next;
               i++;
            }
         }

      }

      return excludeArtifactTypes;
   }

   /**
    * Parse through template to find xml defining artifact sets and replace it with the result of publishing those
    * artifacts. Only used by Publish SRS
    *
    * @param artifacts = null if the template defines the artifacts to be used in the publishing
    * @param folder = null when not using an extension template
    * @param outlineNumber if null will find based on first artifact
    */
   public InputStream applyTemplate(List<Artifact> artifacts, String templateContent, String templateOptions, String templateStyles, IContainer folder, String outlineNumber, String outlineType, PresentationType presentationType) {

      overrideClassification = (String) renderer.getRendererOptionValue(RendererOption.OVERRIDE_DATA_RIGHTS);
      if (overrideClassification == null) {
         overrideClassification = "invalid";
      }

      excludeFolders = (boolean) renderer.getRendererOptionValue(RendererOption.EXCLUDE_FOLDERS);

      if (artifacts.isEmpty()) {
         branch = BranchId.SENTINEL;
      } else {
         branch = artifacts.get(0).getBranch();
         Branch fullBranch = BranchManager.getBranch(branch);
         if (fullBranch.getBranchType().equals(BranchType.MERGE)) {
            fullBranch = fullBranch.getParentBranch();
         }
         ApplicabilityEndpoint applEndpoint =
            ServiceUtil.getOseeClient().getApplicabilityEndpoint(fullBranch.getParentBranch());

         applicabilityTokens = new HashMap<>();
         Collection<ApplicabilityToken> appTokens = applEndpoint.getApplicabilityTokens();
         for (ApplicabilityToken token : appTokens) {
            applicabilityTokens.put(token, token);
         }
      }

      ArtifactId view = (ArtifactId) renderer.getRendererOptionValue(RendererOption.VIEW);
      artifactsToExclude = getNonApplicableArtifacts(artifacts, view == null ? ArtifactId.SENTINEL : view);

      WordMLProducer wordMl = null;
      CharBackedInputStream charBak = null;

      try {
         charBak = new CharBackedInputStream();
         wordMl = new WordMLProducer(charBak);

         templateContent = templateContent.replaceAll(PGNUMTYPE_START_1, "");

         if (!templateStyles.isEmpty()) {
            templateContent = templateContent.replaceAll(STYLES, templateStyles);
         }

         this.outlineNumber = outlineNumber == null ? peekAtFirstArtifactToGetParagraphNumber(templateContent, null,
            artifacts) : outlineNumber;
         templateContent = wordMl.setHeadingNumbers(this.outlineNumber, templateContent, outlineType);

         Matcher matcher = headElementsPattern.matcher(templateContent);

         int lastEndIndex = 0;
         while (matcher.find()) {
            wordMl.addWordMl(templateContent.substring(lastEndIndex, matcher.start()));
            lastEndIndex = matcher.end();

            JSONObject jsonObject = new JSONObject(templateOptions);
            elementType = jsonObject.getString("ElementType");

            if (elementType.equals(ARTIFACT)) {
               parseOutliningOptions(templateOptions);

               if (presentationType == PresentationType.SPECIALIZED_EDIT && artifacts.size() == 1) {
                  // for single edit override outlining options
                  outlining = false;
               }
               processArtifactSet(templateOptions, artifacts, wordMl, outlineType, presentationType,
                  (ArtifactId) renderer.getRendererOptionValue(RendererOption.VIEW));
            } else if (elementType.equals(NESTED_TEMPLATE)) {
               parseNestedTemplateOptions(templateOptions, folder, wordMl, presentationType);
            } else {
               throw new OseeArgumentException("Invalid input [%s]", "");
            }
         }

         String endOfTemplate = templateContent.substring(lastEndIndex);
         // Write out the last of the template
         wordMl.addWordMl(updateFooter(endOfTemplate));

         displayNonTemplateArtifacts(nonTemplateArtifacts,
            "Only artifacts of type Word Template Content are supported in this case.");

      } catch (CharacterCodingException ex) {
         OseeCoreException.wrapAndThrow(ex);
      } catch (JSONException ex) {
         OseeCoreException.wrapAndThrow(ex);
      }

      return charBak;
   }

   private void parseNestedTemplateOptions(String templateOptions, IContainer folder, WordMLProducer wordMl, PresentationType presentationType) {
      try {
         JSONObject jsonObject = new JSONObject(templateOptions);
         JSONArray nestedTemplateOptions = jsonObject.getJSONArray("NestedTemplates");
         JSONObject options = null;

         if (nestedCount < nestedTemplateOptions.length()) {
            options = nestedTemplateOptions.getJSONObject(nestedCount);
            nestedCount++;
            outlineType = options.getString("OutlineType");
            if (outlineType.isEmpty()) {
               outlineType = null;
            }
            sectionNumber = options.getString("SectionNumber");
            subDocName = options.getString("SubDocName");
            key = options.getString("Key");
            // rendererOption is either ID or NAME
            RendererOption rendererOption = RendererOption.valueOf(key.toUpperCase());
            value = options.getString("Value");

            renderer.updateOption(rendererOption, value);

            String artifactName = (String) renderer.getRendererOptionValue(RendererOption.NAME);
            String artifactId = (String) renderer.getRendererOptionValue(RendererOption.ID);
            String orcsQuery = (String) renderer.getRendererOptionValue(RendererOption.ORCS_QUERY);
            BranchId branch = (BranchId) renderer.getRendererOptionValue(RendererOption.BRANCH);
            List<Artifact> artifacts = null;

            if (Strings.isValid(artifactId)) {
               List<ArtifactId> artIds = Arrays.asList(ArtifactId.valueOf(artifactId));
               artifacts = ArtifactQuery.getArtifactListFrom(artIds, branch, EXCLUDE_DELETED);
            } else if (Strings.isValid(artifactName)) {
               artifacts = ArtifactQuery.getArtifactListFromName(artifactName, branch, EXCLUDE_DELETED);
            } else if (Strings.isValid(orcsQuery)) {
               Writer writer = new StringWriter();
               OseeClient oseeClient = ServiceUtil.getOseeClient();
               oseeClient.executeScript(orcsQuery, null, false, MediaType.APPLICATION_JSON_TYPE, writer);
               artifacts = parseOrcsQueryResult(writer.toString(), branch);
            }

            String subDocFileName = subDocName + ".xml";

            if (isDiff) {
               WordTemplateFileDiffer templateFileDiffer = new WordTemplateFileDiffer(renderer);
               templateFileDiffer.generateFileDifferences(artifacts, "/results/" + subDocFileName, sectionNumber,
                  outlineType, recurseChildren);
            } else {
               IFile file = folder.getFile(new Path(subDocFileName));
               AIFile.writeToFile(file, applyTemplate(artifacts, slaveTemplate, slaveTemplateOptions,
                  slaveTemplateStyles, folder, sectionNumber, outlineType, presentationType));
            }
            wordMl.createHyperLinkDoc(subDocFileName);
         }

      } catch (JSONException ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
   }

   private List<Artifact> parseOrcsQueryResult(String result, BranchId branch) {
      ArrayList<Artifact> artifacts = new ArrayList<Artifact>();
      try {
         JSONObject jsonObject = new JSONObject(result);
         JSONArray results = jsonObject.getJSONArray("results");
         if (results.length() >= 1) {
            JSONArray artifactIds = results.getJSONObject(0).getJSONArray("artifacts");
            JSONObject id = null;
            for (int i = 0; i < artifactIds.length(); i++) {
               id = artifactIds.getJSONObject(i);
               ArtifactId artifactId = ArtifactId.valueOf(id.getLong("id"));
               Artifact artifact = ArtifactQuery.getArtifactFromId(artifactId, branch, EXCLUDE_DELETED);
               artifacts.add(artifact);
            }
         }
      } catch (JSONException ex) {
         OseeCoreException.wrapAndThrow(ex);
      }

      return artifacts;
   }

   private void parseAttributeOptions(String templateOptions) {
      try {
         attributeElements.clear();

         JSONObject jsonObject = new JSONObject(templateOptions);
         JSONArray attributeOptions = jsonObject.getJSONArray("AttributeOptions");
         JSONObject options = null;

         for (int i = 0; i < attributeOptions.length(); i++) {
            options = attributeOptions.getJSONObject(i);
            attributeType = options.getString("AttrType");
            attributeLabel = options.getString("Label");
            formatPre = options.getString("FormatPre");
            formatPost = options.getString("FormatPost");

            AttributeElement attrElement = new AttributeElement();

            if (attributeType.equals("*") || AttributeTypeManager.typeExists(attributeType)) {
               attrElement.setElements(attributeType, attributeLabel, formatPre, formatPost);
               attributeElements.add(attrElement);
            }

         }
      } catch (JSONException ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
   }

   private void parseOutliningOptions(String templateOptions) {
      try {
         JSONObject jsonObject = new JSONObject(templateOptions);
         JSONArray optionsArray = jsonObject.getJSONArray("OutliningOptions");
         JSONObject options = optionsArray.getJSONObject(0);

         outlining = options.getBoolean("Outlining");
         recurseChildren = options.getBoolean("RecurseChildren");
         String headingAttrType = options.getString("HeadingAttributeType");
         headingAttributeType = AttributeTypeManager.getType(headingAttrType);
      } catch (JSONException ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
   }

   private void parseMetadataOptions(String metadataOptions) {
      try {
         JSONObject jsonObject = new JSONObject(metadataOptions);
         JSONObject options = null;

         if (!jsonObject.has("MetadataOptions")) {
            return;
         }

         JSONArray optionsArray = jsonObject.getJSONArray("MetadataOptions");
         for (int i = 0; i < optionsArray.length(); i++) {
            options = optionsArray.getJSONObject(i);

            metadataType = options.getString("Type");
            metadataFormat = options.getString("Format");
            metadataLabel = options.getString("Label");

            MetadataElement metadataElement = new MetadataElement();

            metadataElement.setElements(metadataType, metadataFormat, metadataLabel);
            metadataElements.add(metadataElement);
         }
      } catch (JSONException ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
   }

   private String updateFooter(String endOfTemplate) {
      // footer cleanup
      endOfTemplate = endOfTemplate.replaceAll(ReportConstants.FTR, "");
      endOfTemplate =
         endOfTemplate.replaceFirst(ReportConstants.PAGE_SZ, ReportConstants.CONTINUOUS + ReportConstants.PG_SZ);
      return endOfTemplate;
   }

   protected String peekAtFirstArtifactToGetParagraphNumber(String template, String nextParagraphNumber, List<Artifact> artifacts) {
      String startParagraphNumber = "1";
      if (artifacts != null) {
         Matcher matcher = headElementsPattern.matcher(template);

         if (matcher.find()) {
            String elementType = matcher.group(0);

            if (elementType != null && elementType.equals(INSERT_ARTIFACT_HERE) && !artifacts.isEmpty()) {
               Artifact artifact = artifacts.iterator().next();
               if (artifact.isAttributeTypeValid(CoreAttributeTypes.ParagraphNumber)) {
                  String paragraphNum = artifact.getSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, "");
                  if (Strings.isValid(paragraphNum)) {
                     startParagraphNumber = paragraphNum;
                  }
               }
            }
         }
      }
      return startParagraphNumber;
   }

   private void processArtifactSet(String templateOptions, List<Artifact> artifacts, WordMLProducer wordMl, String outlineType, PresentationType presentationType, ArtifactId viewId) {
      nonTemplateArtifacts.clear();
      renderer.updateOption(RendererOption.VIEW, viewId == null ? ArtifactId.SENTINEL : viewId);

      if (Strings.isValid(outlineNumber)) {
         wordMl.setNextParagraphNumberTo(outlineNumber);
      }

      // Don't extract the settings from the template if already done.
      if (attributeElements.isEmpty()) {
         parseAttributeOptions(templateOptions);
      }

      if (metadataElements.isEmpty()) {
         parseMetadataOptions(templateOptions);
      }

      if (((boolean) renderer.getRendererOptionValue(RendererOption.PUBLISH_DIFF))) {
         WordTemplateFileDiffer templateFileDiffer = new WordTemplateFileDiffer(renderer);
         templateFileDiffer.generateFileDifferences(artifacts, "/results/", outlineNumber, outlineType,
            recurseChildren);
      } else {

         List<ArtifactId> allArtifacts = new ArrayList<>();
         if (recurseChildren || (((boolean) renderer.getRendererOptionValue(
            RendererOption.RECURSE_ON_LOAD)) && !((boolean) renderer.getRendererOptionValue(
               RendererOption.ORIG_PUBLISH_AS_DIFF)))) {
            for (Artifact art : artifacts) {
               allArtifacts.add(art);
               if (!art.isHistorical()) {
                  allArtifacts.addAll(art.getDescendants());
               }
            }
         } else {
            allArtifacts.addAll(artifacts);
         }

         DataRightResult response = ServiceUtil.getOseeClient().getDataRightsEndpoint().getDataRights(allArtifacts,
            branch, overrideClassification);

         for (Artifact artifact : artifacts) {
            processObjectArtifact(artifact, wordMl, outlineType, presentationType, response);
         }
      }
      // maintain a list of artifacts that have been processed so we do not
      // have duplicates.
      processedArtifacts.clear();
   }

   private HashMap<ArtifactId, ArtifactId> getNonApplicableArtifacts(List<Artifact> artifacts, ArtifactId view) {
      HashMap<ArtifactId, ArtifactId> toReturn = new HashMap<>();

      if (artifacts != null && !artifacts.isEmpty()) {
         Object[] objs = {branch, view, branch};

         if (!view.equals(ArtifactId.SENTINEL)) {
            List<ArtifactId> excludedArtifacts = ArtifactLoader.selectArtifactIds(LOAD_EXCLUDED_ARTIFACTIDS, objs, 300);
            for (ArtifactId artId : excludedArtifacts) {
               toReturn.put(artId, artId);
            }
         }
      }

      return toReturn;
   }

   private void processObjectArtifact(Artifact artifact, WordMLProducer wordMl, String outlineType, PresentationType presentationType, DataRightResult data) {
      if (!artifact.isAttributeTypeValid(CoreAttributeTypes.WholeWordContent) && !artifact.isAttributeTypeValid(
         CoreAttributeTypes.NativeContent)) {
         // If the artifact has not been processed
         if (!processedArtifacts.contains(artifact)) {

            boolean ignoreArtifact =
               (excludeFolders && artifact.isOfType(CoreArtifactTypes.Folder)) || artifactsToExclude.containsKey(
                  ArtifactId.valueOf(artifact.getId()));

            boolean ignoreArtType = excludeArtifactTypes != null && artifact.isOfType(excludeArtifactTypes);
            boolean publishInline = artifact.getSoleAttributeValue(CoreAttributeTypes.PublishInline, false);
            boolean startedSection = false;
            boolean templateOnly = (boolean) renderer.getRendererOptionValue(RendererOption.TEMPLATE_ONLY);
            boolean includeUUIDs = (boolean) renderer.getRendererOptionValue(RendererOption.INCLUDE_UUIDS);

            if (!ignoreArtifact && !ignoreArtType) {
               if (outlining && !templateOnly) {
                  String headingText = artifact.getSoleAttributeValue(headingAttributeType, "");

                  if (includeUUIDs) {
                     String UUIDtext = String.format(" <UUID = %s>", artifact.getArtId());
                     headingText = headingText.concat(UUIDtext);
                  }

                  Boolean mergeTag = (Boolean) renderer.getRendererOptionValue(RendererOption.ADD_MERGE_TAG);
                  if (mergeTag != null && mergeTag) {
                     headingText = headingText.concat(" [MERGED]");
                  }

                  if (!publishInline && !templateOnly) {
                     paragraphNumber = wordMl.startOutlineSubSection("Times New Roman", headingText, outlineType);
                     startedSection = true;
                  }

                  if (paragraphNumber == null) {
                     paragraphNumber = wordMl.startOutlineSubSection();
                     startedSection = true;
                  }

                  if (((boolean) renderer.getRendererOptionValue(
                     RendererOption.UPDATE_PARAGRAPH_NUMBERS)) && !publishInline) {
                     if (artifact.isAttributeTypeValid(CoreAttributeTypes.ParagraphNumber)) {
                        artifact.setSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, paragraphNumber.toString());

                        SkynetTransaction transaction =
                           (SkynetTransaction) renderer.getRendererOptionValue(RendererOption.TRANSACTION_OPTION);
                        if (transaction != null) {
                           artifact.persist(transaction);
                        } else {
                           artifact.persist(getClass().getSimpleName());
                        }
                     }
                  }
               }
               PageOrientation orientation = WordRendererUtil.getPageOrientation(artifact);
               String footer = data.getContent(artifact.getId(), orientation);

               processMetadata(artifact, wordMl);

               processAttributes(artifact, wordMl, presentationType, publishInline, footer);
            }

            // Check for option that may have been set from Publish with Diff BLAM to recurse
            boolean recurse = ((boolean) renderer.getRendererOptionValue(RendererOption.RECURSE_ON_LOAD));
            boolean origDiff = ((boolean) renderer.getRendererOptionValue(RendererOption.ORIG_PUBLISH_AS_DIFF));

            if (recurseChildren && !recurse || (recurse && !origDiff)) {
               for (Artifact childArtifact : artifact.getChildren()) {
                  processObjectArtifact(childArtifact, wordMl, outlineType, presentationType, data);
               }
            }

            if (startedSection) {
               wordMl.endOutlineSubSection();
            }
            processedArtifacts.add(artifact);
         }
      } else {
         nonTemplateArtifacts.add(artifact);
      }
   }

   private void processMetadata(Artifact artifact, WordMLProducer wordMl) {
      for (MetadataElement metadataElement : metadataElements) {
         processMetadata(artifact, wordMl, metadataElement);
      }
   }

   private void processAttributes(Artifact artifact, WordMLProducer wordMl, PresentationType presentationType, boolean publishInLine, String footer) {
      for (AttributeElement attributeElement : attributeElements) {
         String attributeName = attributeElement.getAttributeName();

         if (((boolean) renderer.getRendererOptionValue(RendererOption.ALL_ATTRIBUTES)) || attributeName.equals("*")) {
            for (AttributeTypeToken attributeType : RendererManager.getAttributeTypeOrderList(artifact)) {
               if (!outlining || attributeType.notEqual(headingAttributeType)) {
                  processAttribute(artifact, wordMl, attributeElement, attributeType, true, presentationType,
                     publishInLine, footer);
               }
            }
         } else {
            AttributeType attributeType = AttributeTypeManager.getType(attributeName);
            if (artifact.isAttributeTypeValid(attributeType)) {
               processAttribute(artifact, wordMl, attributeElement, attributeType, false, presentationType,
                  publishInLine, footer);
            }
         }
      }
   }

   private void processMetadata(Artifact artifact, WordMLProducer wordMl, MetadataElement element) {
      wordMl.startParagraph();
      String name = element.getType();
      String format = element.getFormat();
      String label = element.getLabel();
      String value = "";
      if (name.equals(APPLICABILITY)) {
         ApplicabilityToken applicabilityToken = applicabilityTokens.get(artifact.getApplicablityId());
         value = applicabilityToken.getName();
      } else if (name.equals(ARTIFACT_TYPE)) {
         value = artifact.getArtifactTypeName();
      } else if (name.equals(ARTIFACT_ID)) {
         value = artifact.getIdString();
      }
      if (!format.isEmpty() || !label.isEmpty()) {
         if (label.contains(">x<")) {
            wordMl.addWordMl(label.replace(">x<", ">" + Xml.escape(name + ": ").toString() + "<"));
         }
         if (format.contains(">x<")) {
            wordMl.addWordMl(format.replace(">x<", ">" + Xml.escape(value).toString() + "<"));
         }
      } else {
         wordMl.addTextInsideParagraph(name + ": " + value);
      }
      wordMl.endParagraph();
   }

   @SuppressWarnings("deprecation")
   private void processAttribute(Artifact artifact, WordMLProducer wordMl, AttributeElement attributeElement, AttributeTypeToken attributeType, boolean allAttrs, PresentationType presentationType, boolean publishInLine, String footer) {
      renderer.updateOption(RendererOption.ALL_ATTRIBUTES, allAttrs);
      // This is for SRS Publishing. Do not publish unspecified attributes
      if (!allAttrs && attributeType.matches(Partition, SeverityCategory)) {
         if (artifact.isAttributeTypeValid(Partition)) {
            for (Attribute<?> partition : artifact.getAttributes(Partition)) {
               if (partition == null || partition.getValue() == null || partition.getValue().equals("Unspecified")) {
                  return;
               }
            }
         }
      }
      boolean templateOnly = (boolean) renderer.getRendererOptionValue(RendererOption.TEMPLATE_ONLY);
      if (templateOnly && attributeType.notEqual(WordTemplateContent)) {
         return;
      }

      /**
       * In some cases this returns no attributes at all, including no wordTemplateContent, even though it exists This
       * happens when wordTemplateContent is blank, so the else if condition takes this into account.
       */
      Collection<Attribute<Object>> attributes = artifact.getAttributes(attributeType);

      if (!attributes.isEmpty()) {
         // check if the attribute descriptor name is in the ignore list.
         if (ignoreAttributeExtensions.contains(attributeType.getName())) {
            return;
         }

         // Do not publish relation order during publishing
         if (((boolean) renderer.getRendererOptionValue(
            RendererOption.IN_PUBLISH_MODE)) && CoreAttributeTypes.RelationOrder.equals(attributeType)) {
            return;
         }

         if (!(publishInLine && artifact.isAttributeTypeValid(WordTemplateContent)) || attributeType.equals(
            WordTemplateContent)) {
            RendererManager.renderAttribute(attributeType, presentationType, artifact, wordMl,
               attributeElement.getFormat(), attributeElement.getLabel(), footer, renderer.getRendererOptions());
         }
      } else if (attributeType.equals(WordTemplateContent)) {
         RendererManager.renderAttribute(attributeType, presentationType, artifact, wordMl,
            attributeElement.getFormat(), attributeElement.getLabel(), footer, renderer.getRendererOptions());
      }
   }

   private void loadIgnoreAttributeExtensions() {
      IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
      if (extensionRegistry != null) {
         IExtensionPoint point =
            extensionRegistry.getExtensionPoint("org.eclipse.osee.framework.ui.skynet.IgnorePublishAttribute");
         if (point != null) {
            IExtension[] extensions = point.getExtensions();
            for (IExtension extension : extensions) {
               IConfigurationElement[] elements = extension.getConfigurationElements();
               for (IConfigurationElement element : elements) {
                  ignoreAttributeExtensions.add(element.getAttribute("name"));
               }
            }
         }
      }
   }

   private void displayNonTemplateArtifacts(final Collection<Artifact> artifacts, final String warningString) {
      if (!artifacts.isEmpty()) {
         Displays.ensureInDisplayThread(new Runnable() {

            @Override
            public void run() {
               ArrayList<Artifact> nonTempArtifacts = new ArrayList<>(artifacts.size());
               nonTempArtifacts.addAll(artifacts);
               WordUiUtil.displayUnhandledArtifacts(artifacts, warningString);
            }
         });
      }
   }
}
