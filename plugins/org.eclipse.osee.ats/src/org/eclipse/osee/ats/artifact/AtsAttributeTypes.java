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
public final class AtsAttributeTypes extends NamedIdentity implements IAttributeType {

   // @formatter:off
   public static final IAttributeType Actionable = new AtsAttributeTypes("AAMFEcvDtBiaJ3TMatAA", "Actionable", "True if item can have Action written against or assigned to.");
   public static final IAttributeType ActionableItem = new AtsAttributeTypes("AAMFEdbcR2zpGzFOLOQA", "Actionable Item", "Actionable Items that are impacted by this change.");
   public static final IAttributeType Active = new AtsAttributeTypes("AAMFEclQOVmzkIvzyWwA", "Active", "Active ATS configuration object.");
   public static final IAttributeType AllowCommitBranch = new AtsAttributeTypes("AAMFEbCZCkwgj73BsQgA", "Allow Commit Branch");
   public static final IAttributeType AllowCreateBranch = new AtsAttributeTypes("AAMFEbARuQEvi6rtY5gA", "Allow Create Branch");
   public static final IAttributeType BaselineBranchGuid = new AtsAttributeTypes("AAMFEdIjJ2za2fblEVgA", "Baseline Branch Guid", "Basline branch associated with ATS object.");
   public static final IAttributeType BlockingReview = new AtsAttributeTypes("AAMFEctKkjMRrIy1C7gA", "Blocking Review");
   public static final IAttributeType Category1 = new AtsAttributeTypes("AAMFEdrYniOQYrYUKKQA", "Category", "Open field for user to be able to enter text to use for categorizing/sorting.");
   public static final IAttributeType Category2 = new AtsAttributeTypes("AAMFEdthBkolbJKLXuAA", "Category2", Category1.getDescription());
   public static final IAttributeType Category3 = new AtsAttributeTypes("AAMFEd06oxr8LMzZxdgA", "Category3", Category1.getDescription());
   public static final IAttributeType ChangeType = new AtsAttributeTypes("AAMFEc+MwGHnPCv7HlgA", "Change Type", "Type of change.");
   public static final IAttributeType CurrentState = new AtsAttributeTypes("AAMFEdOWL3u6hmX2VbwA", "Current State", "Current state of workflow state machine.");
   public static final IAttributeType Decision = new AtsAttributeTypes("AAMFEd7uDXcmqq_FrCQA", "Decision", "Option selected during decision review.");
   public static final IAttributeType DecisionReviewOptions = new AtsAttributeTypes("AAMFEd5hRy1+SRJRqfwA", "Decision Review Options", "Options available for selection in review.  Each line is a separate option. Format: <option name>;<state to transition to>;<assignee>");
   public static final IAttributeType Description = new AtsAttributeTypes("AAMFEdWJ_ChxX6+YKbwA", "Description", "Detailed explanation.");
   public static final IAttributeType EstimatedCompletionDate = new AtsAttributeTypes("AAMFEc18k3Gh+GP7zqAA", "Estimated Completion Date", "Date the changes will be completed.");
   public static final IAttributeType EstimatedHours = new AtsAttributeTypes("AAMFEdCSqBh+cPyadiwA", "Estimated Hours", "Hours estimated to implement the changes associated with this Action.\nIncludes estimated hours for workflows, tasks and reviews.");
   public static final IAttributeType EstimatedReleaseDate = new AtsAttributeTypes("AAMFEcy6VB7Ble5SP1QA", "Estimated Release Date", "Date the changes will be made available to the users.");
   public static final IAttributeType FullName = new AtsAttributeTypes("AAMFEdZI9XLT34cTonAA", "Full Name", "Expanded and descriptive name.");
   public static final IAttributeType GoalOrderVote = new AtsAttributeTypes("Aiecsz9pP1CRoQdaYRAA", "Goal Order Vote", "Vote for order item belongs to within goal.");
   public static final IAttributeType HoursPerWorkDay = new AtsAttributeTypes("AAMFEdGlqFsZp22RMdAA", "Hours Per Work Day");
   public static final IAttributeType LegacyPcrId = new AtsAttributeTypes("AAMFEd3TakphMtQX1zgA", "Legacy PCR Id", "Field to register problem change report id from legacy items imported into ATS.");
   public static final IAttributeType Location = new AtsAttributeTypes("AAMFEeAW4QBlesdfacwA", "Location", "Enter location of materials to review.");
   public static final IAttributeType Log = new AtsAttributeTypes("AAMFEdgB1DX3eJSZb0wA", "Log");
   public static final IAttributeType NeedBy = new AtsAttributeTypes("AAMFEcxAGzHAKfDNAIwA", "Need By", "Hard schedule date that workflow must be completed.");
   public static final IAttributeType NextVersion = new AtsAttributeTypes("AAMFEcpH8Xb72hsF5AwA", "Next Version", "True if version artifact is \"Next\" version to be released.");
   public static final IAttributeType Numeric1 = new AtsAttributeTypes("AABY2xxQsDm811kCViwA", "Numeric1", "Open field for user to be able to enter numbers for sorting.");
   public static final IAttributeType Numeric2 = new AtsAttributeTypes("AABiRtvZsAEkU4BS9qwA", "Numeric2", Numeric1.getDescription());
   public static final IAttributeType PercentRework = new AtsAttributeTypes("AAMFEdKfjl2TII9+tuwA", "Percent Rework");
   public static final IAttributeType Points = new AtsAttributeTypes("AY2EeqhzcDEGtXtREkAA", "Points", "Abstract value that describes risk, complexity, and size of Actions.");
   public static final IAttributeType PriorityType = new AtsAttributeTypes("AAMFEc8JzH1U6XGD59QA", "Priority", "1 = High; 5 = Low");
   public static final IAttributeType Problem = new AtsAttributeTypes("AAMFEdQUxRyevvTu+bwA", "Problem", "Problem found during analysis.");
   public static final IAttributeType ProposedResolution = new AtsAttributeTypes("AAMFEdSSRDGgBQ5tctAA", "Proposed Resolution", "Recommended resolution.");
   public static final IAttributeType RelatedToState = new AtsAttributeTypes("AAMFEdkwHULOmHbMbGgA", "Related To State", "State of parent workflow this object is related to.");
   public static final IAttributeType ReleaseDate = new AtsAttributeTypes("AAMFEc3+cGcMDOCdmdAA", "Release Date", "Date the changes were made available to the users.");
   public static final IAttributeType Released = new AtsAttributeTypes("AAMFEcnMoUZMLA2zB1AA", "Released", "True if object is in a released state.");
   public static final IAttributeType Resolution = new AtsAttributeTypes("AAMFEdUMfV1KdbQNaKwA", "Resolution", "Implementation details.");
   public static final IAttributeType ReviewBlocks = new AtsAttributeTypes("AAMFEc6G2A8jmRWJgagA", "Review Blocks", "Review Completion will block it's parent workflow in this manner.");
   public static final IAttributeType ReviewDefect = new AtsAttributeTypes("AAMFEd+MSVAb8JQ6f5gA", "Review Defect");
   public static final IAttributeType Role = new AtsAttributeTypes("AAMFEeCqMz0XCSBJ+IQA", "Role");
   public static final IAttributeType SmaNote = new AtsAttributeTypes("AAMFEdm7ywte8qayfbAA", "SMA Note", "Notes applicable to ATS object");
   public static final IAttributeType State = new AtsAttributeTypes("AAMFEdMa3wzVvp60xLQA", "State", "States of workflow state machine.");
   public static final IAttributeType StateNotes = new AtsAttributeTypes("AAMFEdiWPm7M_xV1EswA", "State Notes");
   public static final IAttributeType TeamDefinition = new AtsAttributeTypes("AAMFEdd5bFEe18bd0lQA", "Team Definition");
   public static final IAttributeType TeamUsesVersions = new AtsAttributeTypes("AAMFEcrHnzPxQ7w3ligA", "Team Uses Versions", "True if Team Workflow uses versioning/releasing option.");
   public static final IAttributeType Title = new AtsAttributeTypes(CoreAttributeTypes.Name.getGuid(), CoreAttributeTypes.Name.getName(), "Enter clear and consise title that can be generally understood.");
   public static final IAttributeType UserCommunity = new AtsAttributeTypes("AAMFEdAPtAq1IEwiCQAA", "User Community", "If working in one of these communities resulted in the creation of this Action, please select.  Otherwise, select Other.");
   public static final IAttributeType ValidationRequired = new AtsAttributeTypes("AAMFEcjT0TwkD2R4w1QA", "Validation Required", "If selected, originator will be asked to validate the implementation.");
   public static final IAttributeType VersionLocked = new AtsAttributeTypes("AAzRtEJXbjzR5jySOZgA", "Version Locked", "True if version artifact is locked.");
   public static final IAttributeType WeeklyBenefit = new AtsAttributeTypes("AAMFEdEnEU9AecOHMOwA", "Weekly Benefit", "Estimated number of hours that will be saved over a single year if this change is completed.");
   public static final IAttributeType WorkPackage = new AtsAttributeTypes("AAMFEdpJqRp2wvA2qvAA", "Work Package", "Designated accounting work package for completing workflow.");
   // @formatter:on

   private AtsAttributeTypes(String guid, String name) {
      super(guid, "ats." + name);
   }

   private AtsAttributeTypes(String guid, String name, String description) {
      super(guid, "ats." + name, description);
   }
}