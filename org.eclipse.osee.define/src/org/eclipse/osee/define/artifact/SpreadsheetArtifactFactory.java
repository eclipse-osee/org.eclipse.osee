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
package org.eclipse.osee.define.artifact;

import java.sql.SQLException;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.factory.ArtifactFactory;

/**
 * @author Ryan D. Brooks
 */
public class SpreadsheetArtifactFactory extends ArtifactFactory<Spreadsheet> {
   private static SpreadsheetArtifactFactory factory = null;

   private SpreadsheetArtifactFactory(int factoryId) {
      super(factoryId);
   }

   public static SpreadsheetArtifactFactory getInstance(int factoryId) {
      if (factory == null) {
         factory = new SpreadsheetArtifactFactory(factoryId);
      }
      return factory;
   }

   public static SpreadsheetArtifactFactory getInstance() {
      return factory;
   }

   @Override
   public Spreadsheet getNewArtifact(String guid, String humandReadableId, String factoryKey, Branch branch) throws SQLException {
      return new Spreadsheet(this, guid, humandReadableId, branch);
   }
}