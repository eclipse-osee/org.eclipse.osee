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
package org.eclipse.osee.ats.artifact;

import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.NamedIdentity;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;

/**
 * @author Ryan D. Brooks
 */
public class AtsAttributeTypes extends NamedIdentity implements IAttributeType {

   public static final AtsAttributeTypes ATS_ACTIVE = new AtsAttributeTypes("AAMFEclQOVmzkIvzyWwA", "Active");
   public static final AtsAttributeTypes ATS_RESOLUTION = new AtsAttributeTypes("AAMFEdUMfV1KdbQNaKwA", "Resolution");
   public static final AtsAttributeTypes ATS_ROLE = new AtsAttributeTypes("AAMFEeCqMz0XCSBJ+IQA", "Role");
   public static final AtsAttributeTypes ATS_LOG = new AtsAttributeTypes("AAMFEdgB1DX3eJSZb0wA", "Log");
   public static final AtsAttributeTypes ATS_STATE = new AtsAttributeTypes("AAMFEdMa3wzVvp60xLQA", "State");
   public static final AtsAttributeTypes ATS_POINTS = new AtsAttributeTypes("AY2EeqhzcDEGtXtREkAA", "Points");
   public static final AtsAttributeTypes ATS_NUMERIC_1 = new AtsAttributeTypes("AABY2xxQsDm811kCViwA", "Numeric1");
   public static final AtsAttributeTypes ATS_NUMERIC_2 = new AtsAttributeTypes("AABiRtvZsAEkU4BS9qwA", "Numeric2");
   public static final AtsAttributeTypes ATS_CATEGORY_1 = new AtsAttributeTypes("AAMFEdrYniOQYrYUKKQA", "Category");
   public static final AtsAttributeTypes ATS_CATEGORY_2 = new AtsAttributeTypes("AAMFEdthBkolbJKLXuAA", "Category2");
   public static final AtsAttributeTypes ATS_CATEGORY_3 = new AtsAttributeTypes("AAMFEd06oxr8LMzZxdgA", "Category3");
   public static final AtsAttributeTypes ATS_ACTIONABLE = new AtsAttributeTypes("AAMFEcvDtBiaJ3TMatAA", "Actionable");
   public static final AtsAttributeTypes ATS_PROBLEM = new AtsAttributeTypes("AAMFEdQUxRyevvTu+bwA", "Problem");
   public static final AtsAttributeTypes ATS_DESCRIPTION = new AtsAttributeTypes("AAMFEdWJ_ChxX6+YKbwA", "Description");
   public static final AtsAttributeTypes ATS_FULL_NAME = new AtsAttributeTypes("AAMFEdZI9XLT34cTonAA", "Full Name");
   public static final AtsAttributeTypes ATS_LOCATION = new AtsAttributeTypes("AAMFEeAW4QBlesdfacwA", "Location");
   public static final AtsAttributeTypes ATS_CHANGE_TYPE = new AtsAttributeTypes("AAMFEc+MwGHnPCv7HlgA", "Change Type");
   public static final AtsAttributeTypes ATS_PRIORITY_TYPE = new AtsAttributeTypes("AAMFEc8JzH1U6XGD59QA", "Priority");
   public static final AtsAttributeTypes ATS_NEED_BY = new AtsAttributeTypes("AAMFEcxAGzHAKfDNAIwA", "Need By");
   public static final AtsAttributeTypes ATS_SMA_NOTE = new AtsAttributeTypes("AAMFEdm7ywte8qayfbAA", "SMA Note");
   public static final AtsAttributeTypes ATS_STATE_NOTES = new AtsAttributeTypes("AAMFEdiWPm7M_xV1EswA", "State Notes");
   public static final AtsAttributeTypes ATS_RELEASED = new AtsAttributeTypes("AAMFEcnMoUZMLA2zB1AA", "Released");
   public static final AtsAttributeTypes ATS_DECISION = new AtsAttributeTypes("AAMFEd7uDXcmqq_FrCQA", "Decision");

   public static final AtsAttributeTypes ATS_DECISION_REVIEW_OPTIONS = new AtsAttributeTypes("AAMFEd5hRy1+SRJRqfwA",
      "Decision Review Options");
   public static final AtsAttributeTypes ATS_LEGACY_PCR_ID = new AtsAttributeTypes("AAMFEd3TakphMtQX1zgA",
      "Legacy PCR Id");
   public static final AtsAttributeTypes ATS_TEAM_DEFINITION = new AtsAttributeTypes("AAMFEdd5bFEe18bd0lQA",
      "Team Definition");
   public static final AtsAttributeTypes ATS_CURRENT_STATE = new AtsAttributeTypes("AAMFEdOWL3u6hmX2VbwA",
      "Current State");
   public static final AtsAttributeTypes ATS_ALLOW_CREATE_BRANCH = new AtsAttributeTypes("AAMFEbARuQEvi6rtY5gA",
      "Allow Create Branch");
   public static final AtsAttributeTypes ATS_ALLOW_COMMIT_BRANCH = new AtsAttributeTypes("AAMFEbCZCkwgj73BsQgA",
      "Allow Commit Branch");
   public static final AtsAttributeTypes ATS_USER_COMMUNITY = new AtsAttributeTypes("AAMFEdAPtAq1IEwiCQAA",
      "User Community");
   public static final AtsAttributeTypes ATS_RELEASE_DATE = new AtsAttributeTypes("AAMFEc3+cGcMDOCdmdAA",
      "Release Date");
   public static final AtsAttributeTypes ATS_REVIEW_DEFECT = new AtsAttributeTypes("AAMFEd+MSVAb8JQ6f5gA",
      "Review Defect");
   public static final AtsAttributeTypes ATS_ESTIMATED_HOURS = new AtsAttributeTypes("AAMFEdCSqBh+cPyadiwA",
      "Estimated Hours");
   public static final AtsAttributeTypes ATS_WEEKLY_BENEFIT = new AtsAttributeTypes("AAMFEdEnEU9AecOHMOwA",
      "Weekly Benefit");
   public static final AtsAttributeTypes ATS_PERCENT_REWORK = new AtsAttributeTypes("AAMFEdKfjl2TII9+tuwA",
      "Percent Rework");
   public static final AtsAttributeTypes ATS_BLOCKING_REVIEW = new AtsAttributeTypes("AAMFEctKkjMRrIy1C7gA",
      "Blocking Review");
   public static final AtsAttributeTypes ATS_REVIEW_BLOCKS = new AtsAttributeTypes("AAMFEc6G2A8jmRWJgagA",
      "Review Blocks");
   public static final AtsAttributeTypes ATS_ACTIONABLE_ITEM = new AtsAttributeTypes("AAMFEdbcR2zpGzFOLOQA",
      "Actionable Item");
   public static final AtsAttributeTypes ATS_HOURS_PER_WORK_DAY = new AtsAttributeTypes("AAMFEdGlqFsZp22RMdAA",
      "Hours Per Work Day");
   public static final AtsAttributeTypes ATS_VALIDATION_REQUIRED = new AtsAttributeTypes("AAMFEcjT0TwkD2R4w1QA",
      "Validation Required");
   public static final AtsAttributeTypes ATS_PROPOSED_RESOLUTION = new AtsAttributeTypes("AAMFEdSSRDGgBQ5tctAA",
      "Proposed Resolution");
   public static final AtsAttributeTypes ATS_ESTIMATED_RELEASE_DATE = new AtsAttributeTypes("AAMFEcy6VB7Ble5SP1QA",
      "Estimated Release Date");
   public static final AtsAttributeTypes ATS_ESTIMATED_COMPLETION_DATE = new AtsAttributeTypes("AAMFEc18k3Gh+GP7zqAA",
      "Estimated Completion Date");
   public static final AtsAttributeTypes ATS_WORK_PACKAGE = new AtsAttributeTypes("AAMFEdpJqRp2wvA2qvAA",
      "Work Package");
   public static final AtsAttributeTypes ATS_GOAL_ORDER_VOTE = new AtsAttributeTypes("Aiecsz9pP1CRoQdaYRAA",
      "Goal Order Vote");
   public static final AtsAttributeTypes ATS_TEAM_USES_VERSIONS = new AtsAttributeTypes("AAMFEcrHnzPxQ7w3ligA",
      "Team Uses Versions");
   public static final AtsAttributeTypes ATS_VERSION_LOCKED = new AtsAttributeTypes("AAzRtEJXbjzR5jySOZgA",
      "Version Locked");
   public static final AtsAttributeTypes ATS_NEXT_VERSION = new AtsAttributeTypes("AAMFEcpH8Xb72hsF5AwA",
      "Next Version");
   public static final AtsAttributeTypes ATS_BASELINE_BRANCH_GUID = new AtsAttributeTypes("AAMFEdIjJ2za2fblEVgA",
      "Baseline Branch Guid");
   public static final AtsAttributeTypes ATS_RELATED_TO_STATE = new AtsAttributeTypes("AAMFEdkwHULOmHbMbGgA",
      "Related To State");

   public static final AtsAttributeTypes ATS_TITLE = new AtsAttributeTypes(CoreAttributeTypes.NAME);

   private AtsAttributeTypes(IAttributeType attributeType) {
      super(attributeType.getGuid(), attributeType.getName());
   }

   private AtsAttributeTypes(String guid, String name) {
      super(guid, "ats." + name);
   }

}