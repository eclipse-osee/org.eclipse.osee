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
package org.eclipse.osee.framework.skynet.core.importing;

import java.net.URI;
import java.util.List;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;

/**
 * @author Roberto E. Escobar
 */
public interface IArtifactSourceParser {

   public abstract String getName();

   public abstract String getDescription();

   /**
    * called before getRoughArtifacts and getRoughRelations to discover the data that they will return
    * 
    * @param source input from which to extract artifact data
    * @param branch
    * @throws Exception
    */
   public abstract void process(URI source, Branch branch) throws Exception;

   public abstract List<RoughArtifact> getRoughArtifacts() throws OseeCoreException;

   public abstract List<RoughRelation> getRoughRelations(RoughArtifact potentialParent) throws OseeCoreException;

   public abstract void addRoughArtifact(RoughArtifact roughArtifact) throws OseeCoreException;

   public abstract void addRoughRelation(RoughRelation roughRelation) throws OseeCoreException;

   public abstract boolean usesTypeList();

}
