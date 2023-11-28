/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils;

import org.eclipse.jdt.annotation.NonNull;

/**
 * Instances of this interface are used to define test branches and the hierarchical structure of the test branches.
 *
 * @author Loren K. Ashley
 */

public interface BranchSpecificationRecord {

   /**
    * Gets the {@link BranchSpecificationRecord} identifier for the test branch.
    *
    * @return the assigned {@link Integer} identifier.
    * @implSpec Implementations shall not return <code>null</code>.
    */

   public @NonNull Integer getIdentifier();

   /**
    * Gets the {@link BranchSpecificationRecord} identifier of this {@link BranchSpecificationRecord}'s test branch's
    * hierarchical parent.
    *
    * @return the {@link Integer} identifier of the hierarchical parent.
    * @implSpec Implementations shall not return <code>null</code>.
    */

   public @NonNull Integer getHierarchicalParentIdentifier();

   /**
    * Gets the test branch creation comment.
    *
    * @return a creation comment to be associated with the test branch.
    * @implSpec Implementations shall not return <code>null</code>.
    */

   public @NonNull String getTestBranchCreationComment();

   /**
    * Get's the test branch name.
    *
    * @return the test branch name.
    * @implSpec Implementations shall not return <code>null</code>.
    */

   public @NonNull String getTestBranchName();

}

/* EOF */