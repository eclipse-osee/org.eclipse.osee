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
package org.eclipse.osee.ats.world.search;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive;

/**
 * @author Donald G. Dunne
 */
public class CriteriaSearchItem extends WorldSearchItem {

   private final List<ISearchPrimitive> criteria;
   private final boolean all;

   public CriteriaSearchItem(String name, List<ISearchPrimitive> criteria, boolean all) {
      super(name);
      this.criteria = criteria;
      this.all = all;
   }

   @Override
   public Collection<Artifact> performSearch(SearchType searchType) throws SQLException, IllegalArgumentException {
      if (criteria == null) throw new IllegalArgumentException("Inavlid search \"" + getName() + "\"");
      Collection<Artifact> artifacts =
            ArtifactPersistenceManager.getInstance().getArtifacts(criteria, all,
                  BranchPersistenceManager.getInstance().getAtsBranch());
      if (cancelled) return EMPTY_SET;
      return artifacts;
   }

}
