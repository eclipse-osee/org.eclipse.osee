/*
 * Created on Oct 22, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.branch.management.exchange;

import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.jdk.core.util.Collections;

/**
 * @author Roberto E. Escobar
 */
public abstract class MixedTranslator<T extends Enum<?>> extends BaseTranslator {

   private final Map<T, String> enumToSequenceMap;
   private final String enumKeyColumnName;

   MixedTranslator(String enumKeyColumnName, Map<T, String> enumToSequenceMap, String... aliases) {
      super(aliases);
      this.enumToSequenceMap = enumToSequenceMap;
      this.enumKeyColumnName = enumKeyColumnName;
   }

   public boolean isApplicable(Set<String> columnNames) {
      return columnNames.contains(enumKeyColumnName) && Collections.setIntersection(getAliases(), columnNames).size() > 0;
   }

   public String getSequence(T value) throws Exception {
      return enumToSequenceMap.get(value);
   }

   public String getEnumKeyColumnName() {
      return enumKeyColumnName;
   }

   public abstract T getEnum(Object value);
}