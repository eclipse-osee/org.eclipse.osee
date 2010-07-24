/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.model;

/**
 * @author Roberto E. Escobar
 */
public final class BranchField {

   public static final String BRANCH_TYPE_FIELD_KEY = "osee.branch.type.field";
   public static final String BRANCH_STATE_FIELD_KEY = "osee.branch.state.field";
   public static final String BRANCH_ARCHIVED_STATE_FIELD_KEY = "osee.branch.archived.state.field";
   public static final String BRANCH_ASSOCIATED_ARTIFACT_ID_FIELD_KEY = "osee.branch.associated.artifact.field";
   public static final String BRANCH_CHILDREN = "osee.branch.child.branches";
   public static final String PARENT_BRANCH = "osee.branch.parent";
   public static final String BRANCH_BASE_TRANSACTION = "osee.branch.base.transaction";
   public static final String BRANCH_SOURCE_TRANSACTION = "osee.branch.source.transaction";

   public static final String MERGE_BRANCH_SOURCE = "osee.merge.branch.source";
   public static final String MERGE_BRANCH_DESTINATION = "osee.merge.branch.destination";

   private BranchField() {
   }
}
