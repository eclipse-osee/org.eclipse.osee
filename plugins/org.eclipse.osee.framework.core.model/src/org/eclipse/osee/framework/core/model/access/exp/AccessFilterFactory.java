/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.model.access.exp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AccessFilterFactory {

   public Collection<IAccessFilter> createFilter() {
      List<IAccessFilter> filters = new ArrayList<>();

      //		Collection<IOseeBranch> allowedBranches = new List<>();
      //		Collection<IBasicArtifact<T>> allowedArtifactType = new List<>();
      //		Collection<IArtifactType> allowedArtifactType = new List<>();
      //
      //		filters.add(new BranchAccessFilter(artifact, branchPermission));
      //		filters.add(new ArtifactAccessFilter());
      //		filters.add(new ArtifactTypeFilter());
      //		filters.add(new AttributeTypeFilter());
      //		filters.add(new RelationTypeFilter());

      return filters;
   }
}
