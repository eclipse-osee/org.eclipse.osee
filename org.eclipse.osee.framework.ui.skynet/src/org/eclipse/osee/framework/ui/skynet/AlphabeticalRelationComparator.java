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
package org.eclipse.osee.framework.ui.skynet;

import java.util.Comparator;
import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;

/**
 * @author Ryan D. Brooks
 */
public class AlphabeticalRelationComparator implements Comparator<RelationLink> {
   private final RelationSide relationSide;

   /**
    * @param relationSide
    */
   public AlphabeticalRelationComparator(RelationSide relationSide) {
      super();
      this.relationSide = relationSide;
   }

   /* (non-Javadoc)
    * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
    */
   @Override
   public int compare(RelationLink relationLink1, RelationLink relationLink2) {
      try {
         return relationLink1.getArtifact(relationSide).compareTo(relationLink2.getArtifact(relationSide));
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         return 0;
      }
   }
}
