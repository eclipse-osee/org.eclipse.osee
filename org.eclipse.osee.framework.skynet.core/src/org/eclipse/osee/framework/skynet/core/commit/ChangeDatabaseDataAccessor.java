/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.commit;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.database.core.ConnectionHandlerStatement;

/**
 * @author Ryan D. Brooks
 * @author Roberto E. Escobar
 */
public class ChangeDatabaseDataAccessor implements IChangeDataAccessor {

   private final static String GET_CHANGE_DATA_QUERY = "";

   @Override
   public Collection<OseeChange> getChangeData(IProgressMonitor monitor, IChangeFactory factory, IChangeLocator locator) throws Exception {
      Collection<OseeChange> data = new ArrayList<OseeChange>();
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         chStmt.runPreparedQuery(GET_CHANGE_DATA_QUERY, toArray(locator));
         while (chStmt.next()) {
            if (monitor.isCanceled()) {
               throw new OperationCanceledException();
            }
            TxChange txChange = TxChange.getChangeType(-1);
            ModificationType modificationType = ModificationType.getMod(-1);
            int typeId = -1;
            int gammaId = -1;

            OseeChange oseeChange = null;
            oseeChange = factory.createArtifactChange(txChange, gammaId, modificationType, typeId);
            oseeChange = factory.createAttributeChange(txChange, gammaId, modificationType, typeId);
            oseeChange = factory.createRelationChange(txChange, gammaId, modificationType, typeId);

            data.add(oseeChange);
         }
      } finally {
         chStmt.close();
      }
      return null;
   }

   private Object[] toArray(IChangeLocator locator) {
      return new Object[] {locator.getBranch().getBranchId()};
   }
}
