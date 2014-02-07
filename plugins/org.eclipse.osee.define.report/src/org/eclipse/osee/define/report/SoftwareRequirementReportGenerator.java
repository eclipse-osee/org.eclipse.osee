/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.report;

import static org.eclipse.osee.define.report.internal.util.RequirementConstants.BULLETSYM;
import static org.eclipse.osee.define.report.internal.util.RequirementConstants.CSID_NONE;
import static org.eclipse.osee.define.report.internal.util.RequirementConstants.EMPTY_PARAGRAPH;
import static org.eclipse.osee.define.report.internal.util.RequirementConstants.FONT;
import static org.eclipse.osee.define.report.internal.util.RequirementConstants.FONT_REGEX;
import static org.eclipse.osee.define.report.internal.util.RequirementConstants.HEADING_BOLDED;
import static org.eclipse.osee.define.report.internal.util.RequirementConstants.INS;
import static org.eclipse.osee.define.report.internal.util.RequirementConstants.LISTNUM_FIELD;
import static org.eclipse.osee.define.report.internal.util.RequirementConstants.NEW_BULLET_STYLE;
import static org.eclipse.osee.define.report.internal.util.RequirementConstants.OLD_BULLET_STYLE;
import static org.eclipse.osee.define.report.internal.util.RequirementConstants.PARAGRAPH_END;
import static org.eclipse.osee.define.report.internal.util.RequirementConstants.PARAGRAPH_START;
import static org.eclipse.osee.define.report.internal.util.RequirementConstants.PARA_REGEX;
import static org.eclipse.osee.define.report.internal.util.RequirementConstants.PIDS_NONE;
import static org.eclipse.osee.define.report.internal.util.RequirementConstants.RUNEND;
import static org.eclipse.osee.define.report.internal.util.RequirementConstants.SENTENCE;
import static org.eclipse.osee.define.report.internal.util.RequirementConstants.SUBDD_NONE;
import static org.eclipse.osee.define.report.internal.util.RequirementConstants.WXML_CHARS;
import static org.eclipse.osee.define.report.internal.util.RequirementConstants.WXML_ESCAPES;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.define.report.internal.util.RequirementUtil;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.template.engine.AppendableRule;

/**
 * @author Megumi Telles
 */
public class SoftwareRequirementReportGenerator extends AppendableRule<String> {

   private final QueryFactory queryFactory;
   private final String branchGuid;
   private final String srsRoot;
   private final List<String> templateString;
   HashSet<String> pidArts = new HashSet<String>();
   HashSet<String> srsSubddArts = new HashSet<String>();
   HashSet<String> srsCsidArts = new HashSet<String>();

   public SoftwareRequirementReportGenerator(QueryFactory queryFactory, String branchName, String srsRoot, List<String> templateString) {
      super("content");
      this.queryFactory = queryFactory;
      this.branchGuid = branchName;
      this.srsRoot = srsRoot;
      this.templateString = templateString;
   }

   @SuppressWarnings({"unchecked", "rawtypes"})
   @Override
   public void applyTo(Appendable content) throws IOException {

      addTitleHeaderFooter(content);

      ArtifactReadable srsHeadingFolder =
         queryFactory.fromBranch(branchGuid).andIsOfType(CoreArtifactTypes.Folder).andNameEquals(srsRoot).getResults().getExactlyOne();

      String reqTitle = "Requirements:";
      String subSystem = Strings.emptyString();
      for (ArtifactReadable heading : srsHeadingFolder.getDescendants()) {

         ResultSet<ArtifactReadable> srsArtifacts = RequirementUtil.getSupportingInfo(heading);
         String name = escape(heading.getName()).toString();

         String tmp = RequirementUtil.extractAbbrevSubsystem(name);
         if (!tmp.equals(subSystem)) {
            content.append(EMPTY_PARAGRAPH);
            addParagraph(content, String.format(HEADING_BOLDED, "[SUBSYSTEM DESCRIPTION GOES HERE]"));

            content.append(EMPTY_PARAGRAPH);
            content.append(EMPTY_PARAGRAPH);

            addParagraph(content, String.format(HEADING_BOLDED, reqTitle));

            content.append(EMPTY_PARAGRAPH);
            content.append(EMPTY_PARAGRAPH);
            subSystem = tmp;
         }

         content.append(PARAGRAPH_START);
         content.append(String.format(HEADING_BOLDED,
            name + RequirementUtil.getAbbrevQualificationMethod(srsArtifacts) + " "));
         content.append(RUNEND);

         String wtc = heading.getSoleAttributeAsString(CoreAttributeTypes.WordTemplateContent);
         content.append(formatWtc(wtc));
         content.append(EMPTY_PARAGRAPH);

         content.append(PARAGRAPH_START);
         content.append(LISTNUM_FIELD);
         content.append(PARAGRAPH_END);

         getAllPidNames(srsArtifacts, subSystem);
         getAllSrsNames(srsArtifacts);

         ArrayList<String> sortedPids = new ArrayList(pidArts);
         ArrayList<String> sortedSubdd = new ArrayList(srsSubddArts);
         ArrayList<String> sortedCsid = new ArrayList(srsCsidArts);

         Collections.sort(sortedPids);
         Collections.sort(sortedSubdd);
         Collections.sort(sortedCsid);

         checkForNone(sortedPids, PIDS_NONE);
         checkForNone(sortedSubdd, SUBDD_NONE);
         checkForNone(sortedCsid, CSID_NONE);

         addArtWordML(content, sortedPids);
         addArtWordML(content, sortedSubdd);
         addArtWordML(content, sortedCsid);

         content.append(EMPTY_PARAGRAPH);

         pidArts.clear();
         srsSubddArts.clear();
         srsCsidArts.clear();
      }

   }

   private String formatWtc(String wtc) {
      wtc = wtc.replace(INS, "");
      wtc = wtc.replaceFirst(PARA_REGEX, "");
      wtc = wtc.replaceAll(FONT_REGEX, FONT);
      wtc = wtc.replaceAll(OLD_BULLET_STYLE, NEW_BULLET_STYLE);
      wtc = wtc.replace(BULLETSYM, "");
      return wtc;
   }

   private void addParagraph(Appendable content, String wordMl) throws IOException {
      content.append(PARAGRAPH_START);
      content.append(wordMl);
      content.append(PARAGRAPH_END);
   }

   private void addTitleHeaderFooter(Appendable content) throws IOException {
      if (templateString.size() == 3) {
         content.append(templateString.get(0));
         content.append(templateString.get(1));
         content.append(templateString.get(2));
      }
   }

   private void addArtWordML(Appendable content, List<String> arts) throws IOException {
      for (String art : arts) {
         addParagraph(content, String.format(SENTENCE, art));
      }
   }

   private void getAllPidNames(ResultSet<ArtifactReadable> srsArtifacts, String abbrevSubSystem) {
      for (ArtifactReadable srs : srsArtifacts) {
         String fullSubSystemName = srs.getSoleAttributeAsString(CoreAttributeTypes.Subsystem);
         addPidNames(abbrevSubSystem, srs, fullSubSystemName);
      }
   }

   private void addPidNames(String abbrevSubSystem, ArtifactReadable srs, String fullSubSystemName) {
      for (ArtifactReadable pid : RequirementUtil.getHigherLevelTrace(srs)) {
         String pidName = pid.getName();
         if (!pidName.equals(PIDS_NONE)) {
            if (pid.getSoleAttributeAsString(CoreAttributeTypes.Subsystem).equals(fullSubSystemName)) {
               pidArts.add("PIDS " + abbrevSubSystem + " " + escape(pidName).toString());
            }
         }
      }
   }

   private void getAllSrsNames(ResultSet<ArtifactReadable> srsArtifacts) {
      for (ArtifactReadable srs : srsArtifacts) {
         String srsName = escape(srs.getName()).toString();
         String volume = Strings.emptyString();
         if (srs.isOfType(CoreArtifactTypes.AbstractSoftwareRequirement)) {
            if (!srsName.equals(CSID_NONE) && !srsName.equals(SUBDD_NONE)) {
               volume = getVolumeFolder(srs);
               if (volume.startsWith("CSID")) {
                  srsCsidArts.add(volume + " " + srsName);
               } else if (volume.startsWith("SubDD")) {
                  srsSubddArts.add(volume + " " + srsName);
               }
            }
         }
      }
   }

   private String getVolumeFolder(ArtifactReadable srs) {
      ArtifactReadable volArt = null;
      String volume = null;
      String regex = "Vol(ume)? \\d";

      List<ArtifactReadable> ancestors = srs.getAncestors();
      if (!ancestors.isEmpty()) {
         Matcher matcher = Pattern.compile(regex).matcher("");
         for (ArtifactReadable parent : ancestors) {
            matcher.reset(parent.getName());
            if (matcher.find()) {
               int grpCnt = matcher.groupCount();
               volume = matcher.group(grpCnt - 1);
               volArt = parent;
               break;
            }
         }
         volume = formatVolume(volArt, volume);
      }
      return volume;
   }

   private String formatVolume(ArtifactReadable volArt, String volume) {
      if (volume != null && volArt != null) {
         String[] split = volume.split(" ");
         ArtifactReadable parentFolder = volArt.getParent();
         if (split.length == 2 && parentFolder != null) {
            volume = parentFolder.getName() + " " + split[1];
         }
      }
      return volume;
   }

   private void checkForNone(ArrayList<String> sortedList, String noneStr) {
      if (sortedList.isEmpty()) {
         sortedList.add(noneStr);
      }
   }

   private CharSequence escape(CharSequence text) {
      String textString = text.toString();
      for (int x = 0; x < WXML_CHARS.length; x++) {
         textString = textString.replaceAll(WXML_CHARS[x], WXML_ESCAPES[x]);
      }
      return textString;
   }

}
