package org.eclipse.osee.ats.core.review.role;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public enum Role {
   Moderator,
   Reviewer,
   Author;
   public static Collection<String> strValues() {
      Set<String> values = new HashSet<String>();
      for (Enum<Role> e : values()) {
         values.add(e.name());
      }
      return values;
   }
};
