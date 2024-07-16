/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.define.rest.operations;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.AXml;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.ElapsedTime;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.xml.Xml;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Donald G. Dunne
 */
public class ValidateProcFuncCalls {

   private static final List<ArtifactTypeToken> SR_METHOD_TYPES = Arrays.asList(
      CoreArtifactTypes.SoftwareRequirementProcedureMsWord, CoreArtifactTypes.SoftwareRequirementFunctionMsWord,
      CoreArtifactTypes.SoftwareRequirementDataDefinitionMsWord);

   private static final List<ArtifactTypeToken> ID_METHOD_TYPES = Arrays.asList(
      CoreArtifactTypes.ImplementationDetailsProcedureMsWord, CoreArtifactTypes.ImplementationDetailsFunctionMsWord,
      CoreArtifactTypes.ImplementationDetailsDataDefinitionMsWord);

   private static final List<ArtifactTypeToken> ALL_METHOD_TYPES = new LinkedList<>();
   Map<String, ArtifactToken> artNameToArtifact = new HashMap<>(3000);
   HashCollection<ArtifactTypeToken, String> artTypeToArtUiName = new HashCollection<>(3000);
   HashCollection<String, ArtifactToken> linkNameToArtifact = new HashCollection<>(3000);
   Set<String> methodCallWordContentLinkUiNames = new HashSet<>(3000);
   HashCollection<ArtifactTypeToken, String> artTypeToWordContentLinkUiName = new HashCollection<>(3000);
   Map<ArtifactTypeToken, Pattern> artTypeToPattern = new HashMap<>(3000);
   Pattern uiPattern = Pattern.compile("\\{([A-Z0-9_]+?)\\}", Pattern.CASE_INSENSITIVE);

   private final BranchId branch;
   private final JdbcClient jdbcClient;
   private final XResultData results;
   private final OrcsApi orcsApi;

   private List<Pair<String, String>> srchReplPairs;

   public ValidateProcFuncCalls(JdbcClient jdbcClient, BranchId branch, XResultData results, OrcsApi orcsApi) {
      this.jdbcClient = jdbcClient;
      this.branch = branch;
      this.results = results;
      this.orcsApi = orcsApi;
   }

   public ValidateProcFuncCalls(JdbcClient jdbcClient, BranchId branch, OrcsApi orcsApi) {
      this(jdbcClient, branch, new XResultData(false), orcsApi);
   }

   // http://localhost:8092/define/branch/8203443705848388493/validate/proc
   public XResultData get() {
      ElapsedTime time = new ElapsedTime("run");

      getReqNamesForMethodArtifacts();

      getMethodLinksFromWordContent();

      // If any ID requirement name is referenced in SR, error
      //      validateIndirectRequirementsNotReferencedInSoftReq();

      validateSoftReqMethsAreReferencedInSoftReq();

      validateMethodLinksInSoftReqExistInReqArtifacts();

      time.end();
      return results;
   }

   // If any Software Proc,Funct,Draw,Data is not in SR, error
   private void validateSoftReqMethsAreReferencedInSoftReq() {
      log("");
      log("");
      log("Errors: Software Requirement Procedure, Function, Data Definition not found in Software Requirements");
      for (ArtifactTypeToken methodType : SR_METHOD_TYPES) {
         List<String> wordContentLinkUiNames = artTypeToWordContentLinkUiName.getValues(methodType);
         List<String> artUiNames = artTypeToArtUiName.getValues(methodType);
         Set<String> nameUsed = new HashSet<>();
         for (String artUiName : artUiNames) {
            if (nameUsed.contains(artUiName)) {
               continue;
            }
            nameUsed.add(artUiName);
            if (!wordContentLinkUiNames.contains(artUiName) && !methodCallWordContentLinkUiNames.contains(artUiName)) {
               ArtifactToken art = artNameToArtifact.get(artUiName);
               if (art == null) {
                  log("Error: Can't find artifact with name [%s]", artUiName);
               } else {
                  log("[%s] %s not found in Software Requirements", methodType.getName(), art.toStringWithId());
               }
            }
         }
      }
   }

   // If any Software Req UI Proc, Funct, Data Def link in Software Req not found in
   private void validateMethodLinksInSoftReqExistInReqArtifacts() {
      log("");
      log("");
      log("Errors: Software Requirement Procedure, Function, Data Definition not found in Software Requirements");
      List<String> allTypeArtNames = new LinkedList<>();
      for (ArtifactTypeToken methodType : SR_METHOD_TYPES) {
         List<String> wordContentLinkUiNames = artTypeToWordContentLinkUiName.getValues(methodType);
         List<String> artUiNames = artTypeToArtUiName.getValues(methodType);
         allTypeArtNames.addAll(artUiNames);
         Set<String> linkUsed = new HashSet<>();
         for (String wordContentLinkUiName : wordContentLinkUiNames) {
            if (linkUsed.contains(wordContentLinkUiName)) {
               continue;
            }
            linkUsed.add(wordContentLinkUiName);
            if (!artUiNames.contains(wordContentLinkUiName)) {
               log("[%s] Method Link [%s] not found as Software Requirements Artifact", methodType.getName(),
                  wordContentLinkUiName);
            }
         }
      }
      log("");
      log("");
      log("Errors: Call link w/o method name not found in Software Requirements");
      for (String methodCallWordContentLinkUiName : methodCallWordContentLinkUiNames) {
         if (!allTypeArtNames.contains(methodCallWordContentLinkUiName)) {
            log("Call Link [%s] not found as Software Requirements Artifact", methodCallWordContentLinkUiName);
         }
      }
   }

   private void extractReqNamesFromArtName(ArtifactToken swArt) {
      String artName = swArt.getName();

      List<ArtifactTypeToken> TYPES = new LinkedList<>();
      TYPES.addAll(ALL_METHOD_TYPES);
      TYPES.add(CoreArtifactTypes.SoftwareRequirementMsWord);

      for (ArtifactTypeToken type : TYPES) {
         if (swArt.getArtifactType().equals(type)) {
            Matcher uiMatch = uiPattern.matcher(artName);
            if (uiMatch.find()) {
               String uiName = uiMatch.group(1);
               if (Strings.isValid(uiName)) {
                  artTypeToArtUiName.put(type, uiName);
                  artNameToArtifact.put(uiName, swArt);
                  log("Artifact %s UI Name [%s]", swArt.toStringWithId(), uiName);
               }
            }
         }
      }
   }

   private void extractLinksFromWordContent(String reqStr, ArtifactReadable swArt) {
      for (ArtifactTypeToken type : ALL_METHOD_TYPES) {
         Pattern linkPattern = artTypeToPattern.get(type);
         Matcher m = linkPattern.matcher(reqStr);
         while (m.find()) {
            String linkName = m.group(1);
            artTypeToWordContentLinkUiName.put(type, linkName);
            linkNameToArtifact.put(linkName, swArt);
            log("Content [%s] Link Match [%s] UI Name [%s]", type.getName(), m.group(), linkName);
         }
      }

      Pattern uiPattern = Pattern.compile("Call *\\{([A-Z0-9_]+?)\\}", Pattern.CASE_INSENSITIVE);
      Matcher m = uiPattern.matcher(reqStr);
      Set<String> linkUsed = new HashSet<>();
      while (m.find()) {
         String linkName = m.group(1);
         linkNameToArtifact.put(linkName, swArt);
         methodCallWordContentLinkUiNames.add(linkName);
         if (linkUsed.contains(linkName)) {
            continue;
         }
         log("Call Content Link Match [%s] UI Name [%s]", m.group(), linkName);
      }

   }

   private void createTypeLinkPatterns() {
      for (ArtifactTypeToken type : ALL_METHOD_TYPES) {
         String typeName = "";
         if (type.getName().contains("Func")) {
            typeName = "FUNC";
         } else if (type.getName().contains("Proc")) {
            typeName = "PROC";
         } else if (type.getName().contains("Data")) {
            typeName = "DATA";
         } else if (type.getName().contains("Draw")) {
            typeName = "DRAW";
         }
         Conditions.assertNotNullOrEmpty(typeName, "typeName can't be null for " + type.getName());
         artTypeToPattern.put(type, Pattern.compile("\\{([A-Z0-9_ ]+?)\\} *" + typeName, Pattern.CASE_INSENSITIVE));
      }
   }

   private List<Long> getSoftwareReqIds(BranchId branch) {
      List<Long> resultIds = new LinkedList<>();
      for (ArtifactId art : orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(
         CoreArtifactTypes.SoftwareRequirementMsWord, CoreArtifactTypes.SoftwareRequirementFunctionMsWord,
         CoreArtifactTypes.SoftwareRequirementProcedureMsWord).asArtifactIds()) {
         resultIds.add(art.getId());
      }
      return resultIds;
   }

   private void log(String message, Object... data) {
      String errStr = String.format(message, data);
      results.log(errStr);
   }

   private void getMethodLinksFromWordContent() {
      List<Long> softwareReqArtIds = getSoftwareReqIds(branch);
      List<Collection<Long>> subDivide = Collections.subDivide(softwareReqArtIds, 100);
      createTypeLinkPatterns();
      for (Collection<Long> ids : subDivide) {
         for (ArtifactReadable swArt : orcsApi.getQueryFactory().fromBranch(branch).andIdsL(
            ids).getResults().getList()) {
            try {
               // Extract req links from word xml content
               String reqXmlStr = swArt.getSoleAttributeValue(CoreAttributeTypes.WordTemplateContent,
                  DeletionFlag.EXCLUDE_DELETED, null);
               if (reqXmlStr == null) {
                  continue;
               }
               String reqStr = AXml.removeXmlTags(reqXmlStr);

               extractLinksFromWordContent(reqStr, swArt);
            } catch (Exception ex) {
               results.errorf("Exception processing art %s - ex: %s", swArt.toStringWithId(), ex.getLocalizedMessage());
            }
         }
      }
   }

   private void getReqNamesForMethodArtifacts() {
      ALL_METHOD_TYPES.addAll(SR_METHOD_TYPES);
      ALL_METHOD_TYPES.addAll(ID_METHOD_TYPES);

      for (ArtifactTypeToken type : ALL_METHOD_TYPES) {
         for (ArtifactToken art : orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(type).asArtifactTokens()) {
            extractReqNamesFromArtName(art);
         }
      }
   }

   // http://localhost:8092/define/branch/conv
   boolean reportOnly = true;

   public void searchAndReplace() {
      BranchId branch = BranchId.valueOf(8203443705848388493L);
      TransactionBuilder tx = null;
      if (!reportOnly) {
         tx = orcsApi.getTransactionFactory().createTransaction(branch, "Fix UI names in SoftReq word content.");
      }
      String wasPath = "C:/UserData/SrsConversion/was/";
      String isPath = "C:/UserData/SrsConversion/is/";

      List<Long> softwareReqArtIds = getSoftwareReqIds(branch);
      List<Collection<Long>> subDivide = Collections.subDivide(softwareReqArtIds, 100);
      getSrchReplStrs();
      for (Collection<Long> ids : subDivide) {
         for (ArtifactReadable swArt : orcsApi.getQueryFactory().fromBranch(branch).andIdsL(
            ids).getResults().getList()) {
            try {
               // Extract req links from word xml content
               String reqXmlStrOrig = swArt.getSoleAttributeValue(CoreAttributeTypes.WordTemplateContent,
                  DeletionFlag.EXCLUDE_DELETED, null);
               String reqXmlStr = reqXmlStrOrig;
               if (reqXmlStr == null) {
                  continue;
               }

               reqXmlStr = performSearchAndReplace(reqXmlStr);
               reqXmlStr = fixXmlInUiNameAndUppercase(reqXmlStrOrig, reqXmlStr);

               if (!reqXmlStr.equals(reqXmlStrOrig)) {
                  Lib.writeStringToFile(reqXmlStrOrig, new File(wasPath + "id" + swArt.getIdString() + ".txt"));
                  Lib.writeStringToFile(reqXmlStr, new File(isPath + "id" + swArt.getIdString() + ".txt"));
               }

               if (!reportOnly && tx != null) {
                  if (!reqXmlStr.equals(reqXmlStrOrig)) {
                     tx.setSoleAttributeValue(swArt, CoreAttributeTypes.WordTemplateContent, reqXmlStr);
                  }
               }

            } catch (Exception ex) {
               String msg = String.format("Exception processing art %s - ex: %s", swArt.toStringWithId(),
                  ex.getLocalizedMessage());
               results.error(msg);
            }
         }
      }
      if (!reportOnly && tx != null) {
         tx.commit();
      }
   }

   private String performSearchAndReplace(String reqXmlStr) {
      for (Pair<String, String> srchReplStr : getSrchReplStrs()) {
         String srch = srchReplStr.getFirst();
         String repl = srchReplStr.getSecond();
         reqXmlStr = reqXmlStr.replace(srch, repl);
      }
      return reqXmlStr;
   }

   private final Pattern curlBracePattern = Pattern.compile("(\\{.*?\\})");

   private String fixXmlInUiNameAndUppercase(String reqXmlStrOrig, String reqXmlStr) {
      Matcher m = curlBracePattern.matcher(reqXmlStrOrig);
      while (m.find()) {
         String name = m.group(1);
         String fixedName = Strings.xmlToText(name);
         fixedName = fixedName.toUpperCase();
         fixedName = fixedName.replaceAll(" ", "_");
         if (!name.equals(fixedName)) {
            System.out.println(String.format("now [%s] was [%s]", fixedName, name));
            String escapeForRegex = Lib.escapeForRegex(name);
            reqXmlStr = reqXmlStr.replaceFirst(escapeForRegex, fixedName);
         }
      }
      return reqXmlStr;
   }

   private List<Pair<String, String>> getSrchReplStrs() {
      if (srchReplPairs == null) {
         srchReplPairs = new ArrayList<Pair<String, String>>(3000);

         String[] searchReplaceLines = SearchReplaceLines.getSearchReplaceLines().toArray(
            new String[SearchReplaceLines.getSearchReplaceLines().size()]);
         for (int x = 0; x < SearchReplaceLines.getSearchReplaceLines().size(); x += 3) {

            String srch = searchReplaceLines[x];
            srch = srch.replaceAll("}.*$", "}");
            srch = Xml.escape(srch).toString();

            String repl = searchReplaceLines[x + 2];
            if (srch.equals(repl)) {
               // Skipping same
               continue;
            }
            if (!srch.startsWith("{") || !srch.endsWith("}")) {
               // Skipping unexpected srch str
               continue;
            }

            if (repl.contains("N/A")) {
               // Skipping N/A
               continue;
            }
            if (!repl.startsWith("{") || !repl.endsWith("}")) {
               // Skipping unexpected repl str
               continue;
            }
            srchReplPairs.add(new Pair<>(srch, repl));
         }
      }
      return srchReplPairs;
   }

}
