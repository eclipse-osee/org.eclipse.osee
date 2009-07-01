/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.exception.OseeStateException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;

/**
 * @author Ryan D. Brooks
 */
public class InPlaceRelationSort extends AbstractBlam {
   private static final String UPDATE_ORDER_SQL = "UPDATE osee_relation_link SET b_order = ? WHERE gamma_id =?";

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam#getName()
    */
   @Override
   public String getName() {
      return "In-place Relation Sort";
   }

   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      List<Object[]> updateData = new ArrayList<Object[]>();
      List<Artifact> artifacts = variableMap.getArtifacts("Artifacts");
      ArtifactNameComparator nameComparator = new ArtifactNameComparator();
      for (Artifact parent : artifacts) {
         List<Artifact> children = parent.getChildren();
         Collections.sort(children, nameComparator);

         int previousArtId = -1;

         for (Artifact child : children) {
            List<RelationLink> relations =
                  parent.getRelations(CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD, child);
            if (relations.size() != 1) {
               throw new OseeStateException(
                     relations.size() + " hierarchical relations found between " + parent + " and " + child);
            }
            RelationLink relation = relations.get(0);
            updateData.add(new Object[] {previousArtId, relation.getGammaId()});
            previousArtId = child.getArtId();
         }
      }

      ConnectionHandler.runBatchUpdate(UPDATE_ORDER_SQL, updateData);

      /*  OseeEventManager.kickRelationModifiedEvent(RelationManager.class, RelationModType.ReOrdered, relation,
              relation.getABranch(), relationType.getTypeName());*/
   }

   private static class ArtifactNameComparator implements Comparator<Artifact> {
      private static final Pattern numberPattern = Pattern.compile("[+-]?\\d+");
      private final Matcher numberMatcher = numberPattern.matcher("");

      @Override
      public int compare(Artifact artifact1, Artifact artifact2) {
         String name1 = artifact1.getDescriptiveName();
         String name2 = artifact2.getDescriptiveName();

         numberMatcher.reset(name1);
         if (numberMatcher.matches()) {
            numberMatcher.reset(name2);
            if (numberMatcher.matches()) {
               return Integer.valueOf(name1).compareTo(Integer.valueOf(name2));
            }
         }
         return name1.compareTo(name2);
      }
   }

   @Override
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XListDropViewer\" displayName=\"Artifacts\" /></xWidgets>";
   }

   @Override
   public String getDescriptionUsage() {
      return "Alphebetically sorts children of the given artifacts without the use of a transaction - staight sql";
   }

   public Collection<String> getCategories() {
      return Arrays.asList("Admin");
   }
}