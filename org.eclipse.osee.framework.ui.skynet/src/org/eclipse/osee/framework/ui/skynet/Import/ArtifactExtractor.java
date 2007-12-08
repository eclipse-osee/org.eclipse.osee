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

public interface ArtifactExtractor {
   /**
    * called before getRoughArtifacts and getRoughRelations to discover the data that they will return
    * 
    * @param artifactsFile file from which to extract artifact data
    * @throws Exception
    */
   public abstract void discoverArtifactAndRelationData(File artifactsFile) throws Exception;

   public abstract List<RoughArtifact> getRoughArtifacts() throws Exception;

   public abstract List<RoughRelation> getRoughRelations(RoughArtifact potentialParent) throws Exception;

   public abstract FileFilter getFileFilter();
}