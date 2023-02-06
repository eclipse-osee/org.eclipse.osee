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
import org.eclipse.osee.framework.core.enums.token.TaskChangeTypeAttributeType.TaskChangeTypeEnum;

/**
 * @author Stephen Molaro
 */

public class TaskChangeTypeAttributeType extends AttributeTypeEnum<TaskChangeTypeEnum> {

   public final TaskChangeTypeEnum Unspecified = new TaskChangeTypeEnum(0, "Unspecified");
   public final TaskChangeTypeEnum Add = new TaskChangeTypeEnum(1, "ADD");
   public final TaskChangeTypeEnum Modify = new TaskChangeTypeEnum(2, "MODIFY");
   public final TaskChangeTypeEnum Delete = new TaskChangeTypeEnum(3, "DELETE");
   public final TaskChangeTypeEnum Rename = new TaskChangeTypeEnum(4, "RENAME");

   public TaskChangeTypeAttributeType(NamespaceToken namespace, int enumCount) {
      super(1152921504606847920L, namespace, "Task Change Type", MediaType.TEXT_PLAIN, "",
         TaggerTypeToken.PlainTextTagger, enumCount);
   }

   public TaskChangeTypeAttributeType() {
      this(NamespaceToken.OSEE, 5);
   }

   public class TaskChangeTypeEnum extends EnumToken {
      public TaskChangeTypeEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}