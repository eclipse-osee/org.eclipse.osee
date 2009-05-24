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
package org.eclipse.osee.framework.ui.skynet.Import;

import java.io.File;
import java.io.FileFilter;
import java.util.List;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;

public interface ArtifactExtractor {
   /**
    * called before getRoughArtifacts and getRoughRelations to discover the data that they will return
    * 
    * @param artifactsFile file from which to extract artifact data
    * @param branch
    * @throws Exception
    */
   public abstract void discoverArtifactAndRelationData(File artifactsFile, Branch branch) throws Exception;

   public abstract List<RoughArtifact> getRoughArtifacts() throws OseeCoreException;

   public abstract List<RoughRelation> getRoughRelations(RoughArtifact potentialParent) throws OseeCoreException;

   public abstract FileFilter getFileFilter();

   public abstract String getName();

   public abstract String getDescription();

   public abstract boolean usesTypeList();

}