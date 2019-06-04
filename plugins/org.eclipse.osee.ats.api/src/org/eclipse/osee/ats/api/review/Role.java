/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
