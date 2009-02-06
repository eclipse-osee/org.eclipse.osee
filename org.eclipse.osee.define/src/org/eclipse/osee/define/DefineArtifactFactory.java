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
package org.eclipse.osee.define;

import org.eclipse.osee.define.artifact.Spreadsheet;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;

/**
 * @author Ryan D. Brooks
 */
public class DefineArtifactFactory extends ArtifactFactory {

   public DefineArtifactFactory() {
      super("Spreadsheet");
   }

   @Override
   public Spreadsheet getArtifactInstance(String guid, String humandReadableId, Branch branch, ArtifactType artifactType) throws OseeCoreException {
      if (artifactType.getName().equals("Spreadsheet")) {
         return new Spreadsheet(this, guid, humandReadableId, branch, artifactType);
      }
      throw new OseeArgumentException("did not recognize the artifact type: " + artifactType.getName());
   }
}