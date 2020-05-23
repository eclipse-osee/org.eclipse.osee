/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.ats.api.review;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Donald G. Dunne
 */
public enum Role {
   Moderator,
   Reviewer,
   ModeratorReviewer,
   Author,
   Quality;

   public static Collection<String> strValues() {
      Set<String> values = new HashSet<>();
      for (Enum<Role> e : values()) {
         values.add(e.name());
      }
      return values;
   }
};
