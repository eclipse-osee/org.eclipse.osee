/*
 * Created on Oct 22, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.branch.management.exchange;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Roberto E. Escobar
 */
public class BaseTranslator {

   private final Set<String> aliases;

   private BaseTranslator() {
      this.aliases = new HashSet<String>();
   }

   protected BaseTranslator(String... aliases) {
      this();
      if (aliases != null && aliases.length > 0) {
         for (String alias : aliases) {
            this.aliases.add(alias.toLowerCase());
         }
      }
   }

   public boolean hasAliases() {
      return this.aliases.size() > 0;
   }

   public Set<String> getAliases() {
      return this.aliases;
   }
}
