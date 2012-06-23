package org.eclipse.osee.orcs.db.internal.transaction;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.DataProxy;

public class BinaryStoreTx {
   private final long gammaId;
   private final DataProxy proxy;

   public BinaryStoreTx(long gammaId, DataProxy proxy) {
      super();
      this.gammaId = gammaId;
      this.proxy = proxy;
   }

   public long getGammaId() {
      return gammaId;
   }

   public DataProxy getProxy() {
      return proxy;
   }

   public void persist() throws OseeCoreException {
      proxy.persist(gammaId);
   }

   public void rollBack() throws OseeCoreException {
      //TX_TODO
      //   @Override
      //   protected void internalOnRollBack() throws OseeCoreException {
      //      if (!useExistingBackingData() && Strings.isValid(daoToSql.getUri())) {
      //         try {
      //            HttpProcessor.delete(AttributeURL.getDeleteURL(daoToSql.getUri()));
      //         } catch (Exception ex) {
      //            OseeExceptions.wrapAndThrow(ex);
      //         }
      //      }
      //   }
   }

}