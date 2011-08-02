/*******************************************************************************
 * Copyright (c) 2004, 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.server.admin;

import java.util.LinkedList;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.AbstractDbTxOperation;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.server.admin.internal.Activator;

/**
 * Purges given attribute types.<br/>
 * <p>
 * Tables involved:
 * <li>osee_artifact_type_attributes</li>
 * <li>osee_attribute</li>
 * <li>osee_attribute_type</li>
 * </p>
 * <br/>
 * 
 * @author Ryan D. Brooks
 * @author Shawn F. Cook
 */
public class PurgeAttributeType extends AbstractDbTxOperation {
   private static final String DELETE_ARTIFACT_ATTRIBUTE =
      "delete from osee_artifact_type_attributes where attr_type_id = ?";
   private static final String COUNT_ATTRIBUTE_OCCURRENCE =
      "select count(1) FROM osee_attribute where attr_type_id = ?";
   private static final String DELETE_ATTRIBUTE_TYPE = "delete from osee_attribute_type where attr_type_id = ?";

   private final LinkedList<String> attributeTypeIds;

   public PurgeAttributeType(IOseeDatabaseService databaseService, OperationLogger logger, String... typesToPurge) {
      super(databaseService, "Purge Attribute Type", Activator.PLUGIN_ID, logger);

      this.attributeTypeIds = new LinkedList<String>();
      for (String att : typesToPurge) {
         this.attributeTypeIds.add(att);
      }
   }

   @Override
   protected void doTxWork(IProgressMonitor monitor, OseeConnection connection) throws OseeCoreException {

      log();
      log("Purging Attribute Types...");

      for (String attributeTypeId : attributeTypeIds) {
         log("  Purging " + attributeTypeId);
         purgeAttributeType(attributeTypeId, connection);
      }

      log("...done.");
   }

   private static void purgeAttributeType(final String attributeTypeId, OseeConnection connection) throws OseeCoreException {
      int attributeCount = ConnectionHandler.runPreparedQueryFetchInt(0, COUNT_ATTRIBUTE_OCCURRENCE, attributeTypeId);
      if (attributeCount != 0) {
         throw new OseeArgumentException(
            "Can not delete attribute type [%s] because there are %d existing attributes of this type.",
            attributeTypeId, attributeCount);
      }

      ConnectionHandler.runPreparedUpdate(connection, DELETE_ARTIFACT_ATTRIBUTE, attributeTypeId);
      ConnectionHandler.runPreparedUpdate(connection, DELETE_ATTRIBUTE_TYPE, attributeTypeId);
   }
}
