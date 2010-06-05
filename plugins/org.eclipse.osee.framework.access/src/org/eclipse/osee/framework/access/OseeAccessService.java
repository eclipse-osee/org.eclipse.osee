/*
 * Created on Jun 2, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.access;

import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.framework.access.internal.OseeAccessPoint;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

public interface OseeAccessService {

   public <H extends OseeAccessHandler> void addHandler(OseeAccessPoint.Type<H> type, final H handler) throws OseeCoreException;

   public <H extends OseeAccessHandler> void removeHandler(OseeAccessPoint.Type<H> type, final H handler) throws OseeCoreException;

   public Collection<OseeAccessPoint.Type<?>> getAccessTypes();

   public int getHandlerCount(OseeAccessPoint.Type<?> type);

   public <H extends OseeAccessHandler> IStatus dispatch(IProgressMonitor monitor, String id, OseeAccessPoint<H> accessPoint);
}
