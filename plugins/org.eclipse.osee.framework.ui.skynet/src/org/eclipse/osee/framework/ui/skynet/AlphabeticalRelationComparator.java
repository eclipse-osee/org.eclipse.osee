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

package org.eclipse.osee.framework.ui.skynet;

import java.util.Comparator;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

/**
 * @author Ryan D. Brooks
 */
public class AlphabeticalRelationComparator implements Comparator<RelationLink> {
   private final RelationSide relationSide;

   public AlphabeticalRelationComparator(RelationSide relationSide) {
      super();
      this.relationSide = relationSide;
   }

   @Override
   public int compare(RelationLink relationLink1, RelationLink relationLink2) {
      try {
         return relationLink1.getArtifact(relationSide).compareTo(relationLink2.getArtifact(relationSide));
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         return 0;
      }
   }
}
