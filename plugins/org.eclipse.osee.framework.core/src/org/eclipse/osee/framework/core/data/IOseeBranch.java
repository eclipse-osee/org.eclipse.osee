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
package org.eclipse.osee.framework.core.data;

import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.type.NamedId;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Ryan D. Brooks
 */
public interface IOseeBranch extends BranchId, Named {
   static final int SHORT_NAME_LIMIT = 35;

   default String getShortName() {
      return getShortName(SHORT_NAME_LIMIT);
   }

   default String getShortName(int length) {
      return Strings.truncate(getName(), length);
   }

   public static IOseeBranch valueOf(Long id, String name) {
      final class BranchTokenImpl extends NamedId implements IOseeBranch {

         public BranchTokenImpl(Long id, String name) {
            super(id, name);
         }
      }
      return new BranchTokenImpl(id, name);
   }
}