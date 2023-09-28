/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.orcs.core.ds;

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.jdk.core.util.Strings;

public final class OptionsUtil {

   private OptionsUtil() {
      // Utility method
   }
   private static final String FROM_TRANSACTION = "from.transaction";
   private static final String INCLUDE_DELETED_BRANCHES = "include.deleted.branches";
   private static final String INCLUDE_DELETED_ARTIFACTS = "include.deleted.artifacts";
   private static final String INCLUDE_DELETED_ATTRIBUTES = "include.deleted.attributes";
   private static final String INCLUDE_DELETED_RELATIONS = "include.deleted.relations";
   private static final String INCLUDE_CACHE = "include.cache";
   private static final String LOAD_LEVEL = "load.level";
   private static final String SHOW_HIDDEN_FIELDS = "show.hidden.fields";
   private static final String BRANCH_VIEW = "from.branch.view";
   private static final String INCLUDE_APPLICABILITY_TOKENS = "include.applicability.tokens";
   private static final String ORDER_BY_MECHANISM = "order.by.mechanism";
   private static final String ORDER_BY_ATTRIBUTE = "order.by.attribute";
   private static final String FOLLOW_SEARCH_IN_PROGRESS = "follow.search.in.progress";
   private static final String SINGLE_LEVEL_RELATIONS_SEARCH = "single.level.relations.search";
   private static final String BRANCH_ORDER = "branch.order";

   public static Options createBranchOptions() {
      Options options = new Options();
      setIncludeCache(options, false);
      setIncludeDeletedArtifacts(options, false);
      setIncludeDeletedAttributes(options, false);
      setIncludeDeletedRelations(options, false);
      setLoadLevel(options, LoadLevel.ARTIFACT_DATA);
      setIncludeApplicabilityTokens(options, false);
      setBranchOrder(options, "id");
      return options;
   }

   public static Options createOptions() {
      Options options = new Options();
      reset(options);
      setFollowSearchInProgress(options, false);
      setBranchOrder(options, "id");
      return options;
   }

   public static void reset(Options options) {
      setIncludeCache(options, false);
      setIncludeDeletedBranches(options, false);
      setIncludeDeletedArtifacts(options, false);
      setIncludeDeletedAttributes(options, false);
      setIncludeDeletedRelations(options, false);
      setHeadTransaction(options);
      setLoadLevel(options, LoadLevel.ALL);
      setFromBranchView(options, ArtifactId.SENTINEL);
      setIncludeApplicabilityTokens(options, false);
      setSingleLevelRelationsSearch(options, false);
   }

   public static boolean isCacheIncluded(Options options) {
      return options.getBoolean(INCLUDE_CACHE);
   }

   public static void setIncludeCache(Options options, boolean enabled) {
      options.put(INCLUDE_CACHE, enabled);
   }

   public static boolean areDeletedBranchesIncluded(Options options) {
      return options.getBoolean(INCLUDE_DELETED_BRANCHES);
   }

   public static void setIncludeDeletedBranches(Options options, boolean enabled) {
      options.put(INCLUDE_DELETED_BRANCHES, enabled);
   }

   public static boolean areDeletedArtifactsIncluded(Options options) {
      return options.getBoolean(INCLUDE_DELETED_ARTIFACTS);
   }

   public static void setIncludeDeletedArtifacts(Options options, boolean enabled) {
      options.put(INCLUDE_DELETED_ARTIFACTS, enabled);
   }

   public static DeletionFlag getIncludeDeletedArtifacts(Options options) {
      boolean includeDeleted = areDeletedArtifactsIncluded(options);
      return DeletionFlag.allowDeleted(includeDeleted);
   }

   public static boolean areDeletedAttributesIncluded(Options options) {
      return options.getBoolean(INCLUDE_DELETED_ATTRIBUTES);
   }

   public static void setIncludeDeletedAttributes(Options options, boolean enabled) {
      options.put(INCLUDE_DELETED_ATTRIBUTES, enabled);
   }

   public static DeletionFlag getIncludeDeletedAttributes(Options options) {
      boolean includeDeleted = areDeletedAttributesIncluded(options);
      return DeletionFlag.allowDeleted(includeDeleted);
   }

   public static boolean areDeletedRelationsIncluded(Options options) {
      return options.getBoolean(INCLUDE_DELETED_RELATIONS);
   }

   public static void setIncludeDeletedRelations(Options options, boolean enabled) {
      options.put(INCLUDE_DELETED_RELATIONS, enabled);
   }

   public static DeletionFlag getIncludeDeletedRelations(Options options) {
      boolean includeDeleted = areDeletedRelationsIncluded(options);
      return DeletionFlag.allowDeleted(includeDeleted);
   }

   public static LoadLevel getLoadLevel(Options options) {
      String level = options.get(LOAD_LEVEL);
      LoadLevel loadLevel = LoadLevel.ARTIFACT_DATA;
      if (Strings.isValid(level)) {
         loadLevel = LoadLevel.valueOf(level);
      }
      return loadLevel;
   }

   public static void setLoadLevel(Options options, LoadLevel loadLevel) {
      options.put(LOAD_LEVEL, loadLevel.name());
   }

   public static void setFromTransaction(Options options, TransactionId transactionId) {
      options.put(FROM_TRANSACTION, transactionId);
   }

   public static TransactionId getFromTransaction(Options options) {
      TransactionId transactionId = TransactionId.SENTINEL;
      if (!options.isEmpty(FROM_TRANSACTION)) {
         transactionId = options.getObject(TransactionId.class, FROM_TRANSACTION);
      }
      return transactionId;
   }

   public static void setFromBranchView(Options options, ArtifactId viewId) {
      options.put(BRANCH_VIEW, viewId);
   }

   public static ArtifactId getFromBranchView(Options options) {
      ArtifactId viewId = ArtifactId.SENTINEL;
      if (!options.isEmpty(BRANCH_VIEW)) {
         viewId = options.getObject(ArtifactId.class, BRANCH_VIEW);
      }
      return viewId;
   }

   public static void setHeadTransaction(Options options) {
      setFromTransaction(options, TransactionId.SENTINEL);
   }

   public static boolean isHeadTransaction(Options options) {
      return TransactionId.SENTINEL.equals(getFromTransaction(options));
   }

   public static boolean isHistorical(Options options) {
      return !isHeadTransaction(options);
   }

   public static boolean showHiddenFields(Options options) {
      return options.getBoolean(SHOW_HIDDEN_FIELDS);
   }

   public static void setShowHiddenFields(Options options, boolean enabled) {
      options.put(SHOW_HIDDEN_FIELDS, enabled);
   }

   public static void setIncludeApplicabilityTokens(Options options, boolean enabled) {
      options.put(INCLUDE_APPLICABILITY_TOKENS, enabled);
   }

   public static boolean getIncludeApplicabilityTokens(Options options) {
      return options.getBoolean(INCLUDE_APPLICABILITY_TOKENS);
   }

   /**
    * @param mechanism This should be "RELATION", "ATTRIBUTE", or "RELATION AND ATTRIBUTE"
    */
   public static void setOrderByMechanism(Options options, String mechanism) {
      options.put(ORDER_BY_MECHANISM, mechanism);
   }

   public static String getOrderByMechanism(Options options) {
      return options.get(ORDER_BY_MECHANISM);
   }

   /**
    * @param attributeType the attribute type to query by, however the ORDER_BY_MECHANISM must be "ATTRIBUTE" or
    * "RELATION AND ATTRIBUTE"
    */
   public static void setOrderByAttribute(Options options, AttributeTypeId attributeType) {
      options.put(ORDER_BY_ATTRIBUTE, attributeType.getId());
   }

   public static Long getOrderByAttribute(Options options) {
      return options.getLong(ORDER_BY_ATTRIBUTE);
   }

   public static void setFollowSearchInProgress(Options options, boolean inProgress) {
      options.put(FOLLOW_SEARCH_IN_PROGRESS, inProgress);
   }

   public static boolean getFollowSearchInProgress(Options options) {
      return options.getBoolean(FOLLOW_SEARCH_IN_PROGRESS);
   }

   public static void setSingleLevelRelationsSearch(Options options, boolean singleLevel) {
      options.put(SINGLE_LEVEL_RELATIONS_SEARCH, singleLevel);
   }

   public static boolean getSingleLevelRelationsSearch(Options options) {
      return options.getBoolean(SINGLE_LEVEL_RELATIONS_SEARCH);
   }

   public static void setBranchOrder(Options options, String order) {
      options.put(BRANCH_ORDER, order);
   }

   public static String getBranchOrder(Options options) {
      return options.get(BRANCH_ORDER);
   }
}
