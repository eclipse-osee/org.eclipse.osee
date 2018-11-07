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
package org.eclipse.osee.framework.skynet.core.artifact;

import java.util.Comparator;
import org.eclipse.osee.framework.core.data.ArtifactToken;

/**
 * @author Donald G. Dunne
 */
public class ArtifactNameComparator extends AbstractArtifactNameComparator implements Comparator<ArtifactToken> {

   public ArtifactNameComparator(boolean descending) {
      super(descending);
   }

   @Override
   public int compare(ArtifactToken artifact1, ArtifactToken artifact2) {
      String name1 = artifact1.getName();
      String name2 = artifact2.getName();

      return compareNames(name1, name2);
   }
}