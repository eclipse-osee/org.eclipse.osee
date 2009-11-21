/*
 * Created on Nov 20, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.translation;

import java.util.Collections;
import org.eclipse.osee.framework.core.data.CacheUpdateResponse;
import org.eclipse.osee.framework.core.enums.OseeCacheEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.services.IDataTranslationService;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author b1122182
 */
public class CacheUpdateResponseTranslator implements ITranslator<CacheUpdateResponse> {

   private enum Entry {
      CACHE_ID,
      COUNT;
   }

   private final IDataTranslationService service;

   public CacheUpdateResponseTranslator(IDataTranslationService service) {
      this.service = service;
   }

   @Override
   public CacheUpdateResponse convert(PropertyStore propertyStore) throws OseeCoreException {
      OseeCacheEnum cacheId = OseeCacheEnum.valueOf(propertyStore.get(Entry.CACHE_ID.name()));

      //      return new CacheUpdateRequest(cacheId, Arrays.asList(guids));
      return new CacheUpdateResponse(cacheId, Collections.emptyList());
   }

   @Override
   public PropertyStore convert(CacheUpdateResponse object) throws OseeCoreException {
      PropertyStore store = new PropertyStore();

      store.put(Entry.CACHE_ID.name(), object.getCacheId().name());

      for (Object items : object.getItems()) {
         service.convert(items);
      }
      return store;
   }

}
