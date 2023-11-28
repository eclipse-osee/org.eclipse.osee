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

import java.util.Objects;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.Validation;

/**
 * A basic implementation of the {@link BranchSpecificationRecord} interface that will provide the necessary information
 * for the {@link TestDocumentBuilder} to verify or create the test branches.
 *
 * @author Loren K. Ashley
 */

public class BasicBranchSpecificationRecord implements BranchSpecificationRecord {

   /**
    * Saves the {@link BranchSpecificationRecord} identifier.
    */

   private final @NonNull Integer identifier;

   /**
    * Saves the identifier of the {@link BranchSpecificationRecord} for the parent branch.
    */

   private final @NonNull Integer parentTestBranchIdentifier;

   /**
    * The creation comment to use when the test branch needs to be created.
    */

   private final @NonNull String testBranchCreationComment;

   /**
    * The name of the test branch.
    */

   private final @NonNull String testBranchName;

   /**
    * Creates a new {@link BranchSpecificationRecord} implementation. The branch creation comment is set to "Create
    * &lt;branch-name&gt;". The {@link BranchSpecificationRecord} identifier for the parent branch is set to 0 for the
    * {@link CoreBranches#SYSTEM_ROOT}.
    *
    * @param identifier a unique positive and non-zero integer used to identify the {@link BranchSpecificationRecord}.
    * @param testBranchToken a {@link BranchToken} containing the test branch name.
    * @throws NullPointerException when <code>testBranchToken</code> is <code>null</code>.
    * @throws IllegalArgumentException when <code>testBranchToken</code> method {@link BranchToken#getName} returns a
    * <code>null</code> or blank {@link String}.
    */

   public BasicBranchSpecificationRecord(@NonNull Integer identifier, @NonNull BranchToken testBranchToken) {

      //@formatter:off
      this
         (
            identifier,
            Validation
               .require
                  (
                     testBranchToken,
                     Validation.ValueType.PARAMETER,
                     "BasicBranchSpecificationRecord",
                     "new",
                     "testBranchToken",
                     "cannot be null",
                     Objects::isNull,
                     NullPointerException::new,
                     "test branch token method getName cannot return null",
                     ( p ) -> Strings.isInvalidOrBlank( p.getName() ),
                     IllegalArgumentException::new
                  )
               .getName(),
            "Create ".concat( testBranchToken.getName() ),
            0
         );
      //@formatter:on

   }

   /**
    * Creates a new {@link BranchSpecificationRecord} implementation. The branch creation comment is set to "Create
    * &lt;branch-name&gt;". The {@link BranchSpecificationRecord} identifier for the parent branch is set to 0 for the
    * {@link CoreBranches#SYSTEM_ROOT}.
    *
    * @param identifier a unique positive and non-zero integer used to identify the {@link BranchSpecificationRecord}.
    * @param testBranchName the name of the test branch.
    * @throws NullPointerException when <code>testBranchName</code> is <code>null</code>.
    * @throws IllegalArgumentException when <code>testBranchName</code> is blank.
    */

   public BasicBranchSpecificationRecord(@NonNull Integer identifier, @NonNull String testBranchName) {
      //@formatter:off
      this
         (
            identifier,
            Validation.require
               (
                  testBranchName,
                  Validation.ValueType.PARAMETER,
                  "BasicBranchSpecificationRecord",
                  "new",
                  "testBranchName",
                  "cannot be null or blank",
                  Strings::isInvalidOrBlank,
                  IllegalArgumentException::new
               ),
            "Create ".concat( testBranchName ),
            0
         );
      //@formatter:on
   }

   /**
    * Creates a new {@link BranchSpecificationRecord} implementation. The {@link BranchSpecificationRecord} identifier
    * for the parent branch is set to 0 for the {@link CoreBranches#SYSTEM_ROOT}.
    *
    * @param identifier a unique positive and non-zero integer used to identify the {@link BranchSpecificationRecord}.
    * @param testBranchName the name of the test branch.
    * @param testBranchCreationComment the branch creation comment to use when the test branch need to be created.
    * @throws NullPointerException when any of the parameters are <code>null</code>.
    * @throws IllegalArgumentException when <code>testBranchName</code> is blank.
    */

   public BasicBranchSpecificationRecord(@NonNull Integer identifier, @NonNull String testBranchName, @NonNull String testBranchCreationComment) {
      //@formatter:off
      this
         (
            identifier,
            testBranchName,
            testBranchCreationComment,
            0
         );
      //@formatter:on
   }

   /**
    * Creates a new {@link BranchSpecificationRecord} implementation.
    *
    * @param identifier a unique positive and non-zero integer used to identify the {@link BranchSpecificationRecord}.
    * @param testBranchName the name of the test branch.
    * @param testBranchCreationComment the branch creation comment to use when the test branch need to be created.
    * @param parentTestBranchIdentifier a unique non-negative integer used to identifier the
    * {@link BranchSpecificationRecord} for the parent test branch. The special value 0 refers to the branch
    * {@link CoreBranches#SYSTEM_ROOT}.
    * @throws NullPointerException when any of the parameters are <code>null</code>.
    * @throws IllegalArgumentException when <code>testBranchName</code> or <code>testBranchCreationComment</code> are
    * blank.
    */

   public BasicBranchSpecificationRecord(@NonNull Integer identifier, @NonNull String testBranchName, @NonNull String testBranchCreationComment, @NonNull Integer parentTestBranchIdentifier) {

      //@formatter:off
      this.identifier =
         Validation.require
            (
               identifier,
               Validation.ValueType.PARAMETER,
               "BasicBranchSpecificationRecord",
               "new",
               "identifier",
               "cannot be null",
               Objects::isNull,
               NullPointerException::new,
               "identifier is positive",
               ( p ) -> ( p <= 0 ),
               IllegalArgumentException::new
            );
      //@formatter:on

      //@formatter:off
      this.testBranchName =
         Validation.require
            (
               testBranchName,
               Validation.ValueType.PARAMETER,
               "BasicBranchSpecificationRecord",
               "new",
               "testBranchName",
               "is valid and not blank",
               Strings::isInvalidOrBlank,
               IllegalArgumentException::new
            );
      //@formatter:on

      //@formatter:off
      this.testBranchCreationComment =
         Validation.require
            (
               testBranchCreationComment,
               Validation.ValueType.PARAMETER,
               "BasicBranchSpecificationRecord",
               "new",
               "testBranchCreationComment",
               "is valid and not blank",
               Strings::isInvalidOrBlank,
               IllegalArgumentException::new
            );
      //@formatter:on

      //@formatter:off
      this.parentTestBranchIdentifier =
         Validation.require
            (
               parentTestBranchIdentifier,
               Validation.ValueType.PARAMETER,
               "BasicBranchSpecificationRecord",
               "new",
               "parentTestBranchIdentifier",
               "cannot be null",
               Objects::isNull,
               "is greater than or equal to zero",
               ( p ) -> ( p < 0 ),
               IllegalArgumentException::new
            );
      //@formatter:on

   }

   /**
    * {@inheritDoc}
    */

   @Override
   public @NonNull Integer getIdentifier() {
      return this.identifier;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public @NonNull Integer getHierarchicalParentIdentifier() {
      return this.parentTestBranchIdentifier;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public @NonNull String getTestBranchCreationComment() {
      return this.testBranchCreationComment;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public @NonNull String getTestBranchName() {
      return this.testBranchName;
   }

}

/* EOF */
