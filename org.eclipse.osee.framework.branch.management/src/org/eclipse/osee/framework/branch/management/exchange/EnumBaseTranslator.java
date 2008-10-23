/*
 * Created on Oct 22, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.branch.management.exchange;

import java.util.Map;

/**
 * @author Roberto E. Escobar
 */
public class EnumBaseTranslator<T extends Enum<?>> extends BaseTranslator {

   private final Map<T, IdTranslator> enumToTranslatorMap;
   private final String enumKeyColumnName;

   EnumBaseTranslator(String enumKeyColumnName, Map<T, IdTranslator> enumToTranslatorMap, String... aliases) {
      super(aliases);
      this.enumToTranslatorMap = enumToTranslatorMap;
      this.enumKeyColumnName = enumKeyColumnName;
   }

   public Object translate(T enumKey, Object original) throws Exception {
      Object toReturn = original;
      IdTranslator translatedIdMap = enumToTranslatorMap.get(enumKey);
      if (translatedIdMap != null) {
         toReturn = translatedIdMap.getId(original);
      }
      return toReturn;
   }

   public String getEnumKeyColumnName() {
      return enumKeyColumnName;
   }

   public T getEnum(Object value) {
      return null;
   }
}