/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.framework.ui.skynet.render.word.template;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.word.WordUtil;
import org.eclipse.osee.framework.ui.skynet.render.word.WordMLProducer;

/**
 * @author Andrew M. Finkbeiner
 */
public class ArtifactProcessing implements ITemplateTask {

   private List<ITemplateTask> innerTasks;
   private boolean outlining;
   private boolean recurseChildren;
   private CoreRelationTypes outlineRelation;
   private String headingAttributeName;
   private String outlineNumber;
   private String cleanedText;
   private String artifactSetName;

   private static final Matcher outlineElementsMatcher =
         Pattern.compile("<((\\w+:)?(Outline))>(.*?)</\\1>",
               Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE).matcher("");
   private static final Matcher internalOutlineElementsMatcher =
         Pattern.compile("<((\\w+:)?(HeadingAttribute|RecurseChildren|Number))>(.*?)</\\1>",
               Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE).matcher("");

   /**
    * @param innerTasks
    * @param artifactSection
    * @param elementType
    */
   public ArtifactProcessing(List<ITemplateTask> innerTasks, String artifactSection, String elementType) {
      this.innerTasks = innerTasks;
      extractInformation(artifactSection, elementType);
   }

   @Override
   public boolean isTypeNameWildcard() {
      return false;
   }

   @Override
   public void process(WordMLProducer wordMl, Artifact artifact, List<ITemplateAttributeHandler> handlers) {

   }
   private static final Matcher setNameMatcher =
         Pattern.compile("<(\\w+:)?Set_Name>(.*?)</(\\w+:)?Set_Name>", Pattern.DOTALL | Pattern.MULTILINE).matcher("");

   public List<ITemplateTask> getTasks() {
      return innerTasks;
   }

   private void extractInformation(String artifactElement, String type) {
      if (type.equals("Artifact")) {
         setNameMatcher.reset(artifactElement);
         setNameMatcher.find();
         artifactSetName = WordUtil.textOnly(setNameMatcher.group(2));
         artifactElement = setNameMatcher.replaceAll("");
      }
      outlineElementsMatcher.reset(artifactElement);

      if (outlineElementsMatcher.find()) {
         internalOutlineElementsMatcher.reset(outlineElementsMatcher.group(4));
         outlining = true;
         recurseChildren = false;
         outlineRelation = CoreRelationTypes.Default_Hierarchical__Child;

         while (internalOutlineElementsMatcher.find()) {
            String elementType = internalOutlineElementsMatcher.group(3);
            String value = WordUtil.textOnly(internalOutlineElementsMatcher.group(4));

            if (elementType.equals("HeadingAttribute")) {
               headingAttributeName = value;
            } else if (elementType.equals("RecurseChildren")) {
               recurseChildren = Boolean.parseBoolean(value);
            } else if (elementType.equals("Number")) {
               outlineNumber = value;
            }
         }
      } else {
         outlining = false;
         recurseChildren = false;
         outlineRelation = null;
         headingAttributeName = null;
      }
      cleanedText = outlineElementsMatcher.replaceAll("");
   }

   /**
    * @return the innerTasks
    */
   public List<ITemplateTask> getInnerTasks() {
      return innerTasks;
   }

   /**
    * @return the outlining
    */
   public boolean isOutlining() {
      return outlining;
   }

   /**
    * @return the recurseChildren
    */
   public boolean isRecurseChildren() {
      return recurseChildren;
   }

   /**
    * @return the outlineRelation
    */
   public CoreRelationTypes getOutlineRelation() {
      return outlineRelation;
   }

   /**
    * @return the headingAttributeName
    */
   public String getHeadingAttributeName() {
      return headingAttributeName;
   }

   /**
    * @return the outlineNumber
    */
   public String getOutlineNumber() {
      return outlineNumber;
   }

   public String getText() {
      return this.cleanedText;
   }

}
