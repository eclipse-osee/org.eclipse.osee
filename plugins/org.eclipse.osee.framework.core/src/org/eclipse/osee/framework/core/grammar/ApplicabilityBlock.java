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

package org.eclipse.osee.framework.core.grammar;

import java.util.ArrayList;

/**
 * @author Ryan D. Brooks
 */
public class ApplicabilityBlock {

   public enum ApplicabilityType {
      Configuration,
      NotConfiguration,
      Feature,
      ConfigurationGroup,
      NotConfigurationGroup
   };

   private final ApplicabilityType applicabilityType;
   private final ArrayList<ApplicabilityBlock> embeddedApplicabilityBlocks;
   private final ArrayList<String> outerExpressionOperators;
   private String applicabilityExpression;
   private String optionalEndExpression;
   private boolean isInTable;
   private boolean commentNonApplicableBlocks;

   private String beforeElseText;
   private String afterElseText;
   private String beforeEmbeddedBlockText;
   private String afterEmbeddedBlockText;
   private String insideText;
   private String beginTag;
   private int startInsertIndex;
   private int endInsertIndex;
   private int startTextIndex;
   private int endTextIndex;

   public ApplicabilityBlock(ApplicabilityType applicabilityType) {
      this.applicabilityType = applicabilityType;
      embeddedApplicabilityBlocks = new ArrayList<>();
      outerExpressionOperators = new ArrayList<>();
      isInTable = false;
      commentNonApplicableBlocks = false;

      startInsertIndex = Integer.MAX_VALUE;
      startTextIndex = Integer.MAX_VALUE;
      endInsertIndex = Integer.MIN_VALUE;
      endTextIndex = Integer.MIN_VALUE;
   }

   public int getStartInsertIndex() {
      return startInsertIndex;
   }

   public void setStartInsertIndex(int startInsertIndex) {
      this.startInsertIndex = startInsertIndex;
   }

   public int getEndInsertIndex() {
      return endInsertIndex;
   }

   public void setEndInsertIndex(int endInsertIndex) {
      this.endInsertIndex = endInsertIndex;
   }

   public int getEndTextIndex() {
      return endTextIndex;
   }

   public void setEndTextIndex(int endTextIndex) {
      this.endTextIndex = endTextIndex;
   }

   public int getStartTextIndex() {
      return startTextIndex;
   }

   public void setStartTextIndex(int startTextIndex) {
      this.startTextIndex = startTextIndex;
   }

   public String getBeforeElseText() {
      return beforeElseText;
   }

   public void setBeforeElseText(String beforeElseText) {
      this.beforeElseText = beforeElseText;
   }

   public String getAfterElseText() {
      return afterElseText;
   }

   public void setAfterElseText(String afterElseText) {
      this.afterElseText = afterElseText;
   }

   public String getBeforeEmbeddedBlockText() {
      return beforeEmbeddedBlockText;
   }

   public void setBeforeEmbeddedBlockText(String beforeEmbeddedBlockText) {
      this.beforeEmbeddedBlockText = beforeEmbeddedBlockText;
   }

   public String getAfterEmbeddedBlockText() {
      return afterEmbeddedBlockText;
   }

   public void setAfterEmbeddedBlockText(String afterEmbeddedBlockText) {
      this.afterEmbeddedBlockText = afterEmbeddedBlockText;
   }

   public String getInsideText() {
      return insideText;
   }

   public void setInsideText(String insideText) {
      this.insideText = insideText;
   }

   public ApplicabilityType getType() {
      return applicabilityType;
   }

   public ArrayList<String> getOuterExpressionOperators() {
      return outerExpressionOperators;
   }

   public void addOuterExpressionOperator(String operator) {
      outerExpressionOperators.add(operator);
   }

   public String getApplicabilityExpression() {
      return applicabilityExpression;
   }

   public void setApplicabilityExpression(String applicabilityExpression) {
      this.applicabilityExpression = applicabilityExpression;
   }

   public void addEmbeddedBlock(ApplicabilityBlock applicBlock) {
      embeddedApplicabilityBlocks.add(applicBlock);
   }

   public ArrayList<ApplicabilityBlock> getEmbeddedApplicabilityBlocks() {
      return embeddedApplicabilityBlocks;
   }

   public String getOptionalEndExpression() {
      return optionalEndExpression;
   }

   public void setOptionalEndExpression(String optionalEndExpression) {
      this.optionalEndExpression = optionalEndExpression;
   }

   public void setIsInTable(boolean isInTable) {
      this.isInTable = isInTable;
   }

   public boolean isInTable() {
      return isInTable;
   }

   public String getBeginTag() {
      return beginTag;
   }

   public void setBeginTag(String beginTag) {
      this.beginTag = beginTag;
   }

   public boolean getCommentNonApplicableBlocks() {
      return commentNonApplicableBlocks;
   }

   public void setCommentNonApplicableBlocks(boolean commentNonApplicableBlocks) {
      this.commentNonApplicableBlocks = commentNonApplicableBlocks;
   }
}