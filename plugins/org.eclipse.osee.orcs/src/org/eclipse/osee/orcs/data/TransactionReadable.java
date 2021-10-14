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

package org.eclipse.osee.orcs.data;

import java.util.Date;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * @author Roberto E. Escobar
 */
public interface TransactionReadable extends TransactionToken {

   TransactionReadable SENTINEL = createSentinel();

   TransactionDetailsType getTxType();

   Date getDate();

   String getComment();

   UserToken getAuthor();

   ArtifactId getCommitArt();

   Long getBuildId();

   public static TransactionReadable createSentinel() {
      final class TransactionReadableSentinel extends NamedIdBase implements TransactionReadable {

         @Override
         public TransactionDetailsType getTxType() {
            return null;
         }

         @Override
         public Date getDate() {
            return null;
         }

         @Override
         public String getComment() {
            return null;
         }

         @Override
         public UserToken getAuthor() {
            return UserToken.SENTINEL;
         }

         @Override
         public ArtifactId getCommitArt() {
            return null;
         }

         @Override
         public Long getBuildId() {
            return 0L;
         }

         @Override
         public BranchToken getBranch() {
            return null;
         }
      }
      return new TransactionReadableSentinel();
   }

}