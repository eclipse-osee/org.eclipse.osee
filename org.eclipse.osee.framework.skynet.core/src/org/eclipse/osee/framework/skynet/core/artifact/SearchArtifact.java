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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.operation.WorkflowStep;
import org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.ISearchAttribute;
import org.eclipse.osee.framework.skynet.core.util.AttributeDoesNotExist;
import org.eclipse.osee.framework.skynet.core.util.MultipleAttributesExist;

public class SearchArtifact extends BasicArtifact implements WorkflowStep {
   public final static String ALL_FILTERS = "Match All Filters";
   public final static String STANDARD_SEARCH = "Standard Search";
   public final static String USER_SEARCH = "User Search";

   /**
    * @param parentFactory
    * @param guid
    * @param branch
    * @throws SQLException
    */
   public SearchArtifact(ArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch, ArtifactSubtypeDescriptor artifactType) {
      super(parentFactory, guid, humanReadableId, branch, artifactType);
   }

   public String getPreviewHtml() throws SQLException {
      StringBuilder html = new StringBuilder();

      for (ISearchPrimitive primitive : getCriteria()) {
         html.append(primitive);
         html.append("<br/>");
      }
      return html.toString();
   }

   @SuppressWarnings("unchecked")
   public List<ISearchPrimitive> getCriteria() throws SQLException {
      List<ISearchPrimitive> criteria = new LinkedList<ISearchPrimitive>();

      for (Attribute<?> attr : getAttributes()) {
         if (attr instanceof ISearchAttribute) {
            criteria.add(((ISearchAttribute) attr).getSearchPrimitive());
         }
      }
      return criteria;
   }

   public void addCriteria(List<ISearchPrimitive> criteria) throws SQLException {
      for (ISearchPrimitive primitive : criteria)
         addPrimitive(primitive);
   }

   @SuppressWarnings("unchecked")
   public void addPrimitive(ISearchPrimitive primitive) throws SQLException {
      addAttribute(primitive.getClass().getSimpleName(), primitive.getStorageString());
   }

   public boolean getMatchAll() throws SQLException, MultipleAttributesExist, AttributeDoesNotExist {
      return getSoleAttributeValue(ALL_FILTERS);
   }

   public void setMatchAll(boolean all) throws SQLException, MultipleAttributesExist {
      setSoleXAttributeValue(ALL_FILTERS, all);
   }

   /**
    * Convenience method to perform the search with the <code>ArtifactPersistenceManager</code> based on the
    * information stored in this <code>SearchArtifact</code>.
    * 
    * @throws MultipleAttributesExist
    * @throws AttributeDoesNotExist
    * @see ArtifactPersistenceManager#getArtifacts(List, boolean, Branch)
    */
   public Collection<Artifact> getArtifacts() throws SQLException, MultipleAttributesExist, AttributeDoesNotExist {
      return ArtifactPersistenceManager.getInstance().getArtifacts(getCriteria(), getMatchAll(), getBranch());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.artifact.operation.WorkflowStep#perform(java.util.List, org.eclipse.core.runtime.IProgressMonitor)
    */
   public List<Artifact> perform(List<Artifact> artifacts, IProgressMonitor monitor) throws IllegalArgumentException, Exception {
      Collection<Artifact> results = getArtifacts();
      if (results instanceof List) {
         return (List<Artifact>) results;
      }
      List<Artifact> list = new ArrayList<Artifact>(results.size());
      list.addAll(results);
      return list;
   }
}
