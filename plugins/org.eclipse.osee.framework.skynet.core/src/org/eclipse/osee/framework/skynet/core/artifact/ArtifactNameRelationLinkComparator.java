/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.artifact;

import java.util.Comparator;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;

/**
 * @author Donald G. Dunne
 */
public class ArtifactNameRelationLinkComparator extends AbstractArtifactNameComparator implements Comparator<RelationLink> {

   public ArtifactNameRelationLinkComparator(boolean descending) {
      super(descending);
   }

   @Override
   public int compare(RelationLink link1, RelationLink link2) {
      String name1 = link1.getArtifactB().getName();
      String name2 = link2.getArtifactB().getName();

      return compareNames(name1, name2);
   }
}