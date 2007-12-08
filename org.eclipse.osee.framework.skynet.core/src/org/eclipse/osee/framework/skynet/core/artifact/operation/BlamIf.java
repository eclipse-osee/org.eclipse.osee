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
package org.eclipse.osee.framework.skynet.core.artifact.operation;

import java.sql.SQLException;
import java.util.List;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.factory.IArtifactFactory;

/**
 * @author Ryan D. Brooks
 */
public abstract class BlamIf extends Artifact {
   public static final String ARTIFACT_NAME = "Blam If";

   /**
    * @param parentFactory
    * @param guid
    * @param tagId
    * @throws SQLException
    */
   public BlamIf(IArtifactFactory parentFactory, String guid, String humanReadableId, Branch tagId) throws SQLException {
      super(parentFactory, guid, humanReadableId, tagId);
   }

   public void execute(List<Artifact> artifacts) throws IllegalArgumentException {

   }
}