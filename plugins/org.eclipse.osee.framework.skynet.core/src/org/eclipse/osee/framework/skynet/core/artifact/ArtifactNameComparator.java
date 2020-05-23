/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

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