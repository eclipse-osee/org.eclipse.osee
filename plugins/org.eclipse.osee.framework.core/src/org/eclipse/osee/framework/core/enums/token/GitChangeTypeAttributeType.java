/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.framework.core.enums.token;

import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.enums.EnumToken;
import org.eclipse.osee.framework.core.enums.token.GitChangeTypeAttributeType.GitChangeTypeEnum;

/**
 * @author Stephen Molaro
 */

public class GitChangeTypeAttributeType extends AttributeTypeEnum<GitChangeTypeEnum> {

   public final GitChangeTypeEnum Unspecified = new GitChangeTypeEnum(0, "Unspecified");
   public final GitChangeTypeEnum Add = new GitChangeTypeEnum(1, "ADD");
   public final GitChangeTypeEnum Modify = new GitChangeTypeEnum(2, "MODIFY");
   public final GitChangeTypeEnum Delete = new GitChangeTypeEnum(3, "DELETE");
   public final GitChangeTypeEnum Rename = new GitChangeTypeEnum(4, "RENAME");

   public GitChangeTypeAttributeType(NamespaceToken namespace, int enumCount) {
      super(1152921504606847920L, namespace, "Git Change Type", MediaType.TEXT_PLAIN, "",
         TaggerTypeToken.PlainTextTagger, enumCount);
   }

   public GitChangeTypeAttributeType() {
      this(NamespaceToken.OSEE, 5);
   }

   public class GitChangeTypeEnum extends EnumToken {
      public GitChangeTypeEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}