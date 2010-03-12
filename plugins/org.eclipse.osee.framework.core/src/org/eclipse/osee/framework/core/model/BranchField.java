/*
 * Created on Nov 10, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.model;

/**
 * @author Roberto E. Escobar
 */
public class BranchField {

   public static final String BRANCH_TYPE_FIELD_KEY = "osee.branch.type.field";
   public static final String BRANCH_STATE_FIELD_KEY = "osee.branch.state.field";
   public static final String BRANCH_ARCHIVED_STATE_FIELD_KEY = "osee.branch.archived.state.field";
   public static final String BRANCH_ASSOCIATED_ARTIFACT_FIELD_KEY = "osee.branch.associated.artifact.field";
   public static final String BRANCH_CHILDREN = "osee.branch.child.branches";
   public static final String PARENT_BRANCH = "osee.branch.parent";
   public static final String BRANCH_BASE_TRANSACTION = "osee.branch.base.transaction";
   public static final String BRANCH_SOURCE_TRANSACTION = "osee.branch.source.transaction";

   public static final String MERGE_BRANCH_SOURCE = "osee.merge.branch.source";
   public static final String MERGE_BRANCH_DESTINATION = "osee.merge.branch.destination";

   private BranchField() {
   }
}
