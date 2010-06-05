/*
 * Created on Jun 2, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.access.internal;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.framework.access.OseeAccessHandler;
import org.eclipse.osee.framework.access.OseeAccessPolicy;
import org.eclipse.osee.framework.access.OseeAccessService;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;

public class OseeAccessServiceImpl implements OseeAccessService {

   private final HashCollection<OseeAccessPoint.Type<?>, OseeAccessHandler> handlersByType =
         new HashCollection<OseeAccessPoint.Type<?>, OseeAccessHandler>();

   private final OseeAccessPolicy policy;

   public OseeAccessServiceImpl() {
      this.policy = new OseeAccessPolicy();
   }

   public Collection<OseeAccessPoint.Type<?>> getAccessTypes() {
      return handlersByType.keySet();
   }

   @SuppressWarnings("unchecked")
   private <H> ArrayList<H> get(OseeAccessPoint.Type<H> type) {
      return (ArrayList<H>) handlersByType.getValues(type);
   }

   public <H extends OseeAccessHandler> IStatus dispatch(IProgressMonitor monitor, String id, OseeAccessPoint<H> accessPoint) {
      OseeAccessPoint.Type<H> type = accessPoint.getAssociatedType();
      Collection<H> rawList = get(type);
      if (rawList != null) {
         Collection<H> filtered = policy.getApplicable(accessPoint, rawList);
         for (H handler : filtered) {
            // Populate with data ... and execute... get result continue only if ok.
            IStatus status = accessPoint.dispatch(handler);
            //            accessPoint.dispatch(handler);
            //            handler.execute();
            //            IStatus interim = null;
            //            if (!interim.isOK()) {
            //
            //            }
         }
      }
      return null;
   }

   public int getHandlerCount(OseeAccessPoint.Type<?> type) {
      ArrayList<?> handlers = get(type);
      return handlers == null ? 0 : handlers.size();
   }

   public <H extends OseeAccessHandler> void addHandler(OseeAccessPoint.Type<H> type, final H handler) throws OseeCoreException {
      Conditions.checkNotNull(type, "access handler type");
      Conditions.checkNotNull(handler, "access handler");
      handlersByType.put(type, handler);
   }

   public <H extends OseeAccessHandler> void removeHandler(OseeAccessPoint.Type<H> type, final H handler) throws OseeCoreException {
      Conditions.checkNotNull(type, "access handler type");
      Conditions.checkNotNull(handler, "access handler");
      handlersByType.removeValue(type, handler);
   }

}
