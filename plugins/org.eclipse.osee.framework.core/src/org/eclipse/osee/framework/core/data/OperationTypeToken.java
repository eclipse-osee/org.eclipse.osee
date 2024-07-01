/*********************************************************************
 * Copyright (c) 2024 Boeing
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
package org.eclipse.osee.framework.core.data;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.osee.framework.jdk.core.type.HasDescription;
import org.eclipse.osee.framework.jdk.core.type.NamedId;
import org.eclipse.osee.framework.jdk.core.type.NamedIdDescription;

/**
 * @author Jaden W. Puckett
 */
public interface OperationTypeToken extends HasDescription, NamedId {
   public MaterialIcon getMaterialIcon();

   public static @NonNull OperationTypeToken create(long id, String name, String description, MaterialIcon icon) {
      final class OperationTypeTokenImpl extends NamedIdDescription implements OperationTypeToken {
         private final MaterialIcon icon;

         public OperationTypeTokenImpl(long id, String name, String description, MaterialIcon icon) {
            super(id, name, description);
            this.icon = icon;
         }

         @Override
         public MaterialIcon getMaterialIcon() {
            return icon;
         }
      }
      return new OperationTypeTokenImpl(id, name, description, icon);
   }
}
