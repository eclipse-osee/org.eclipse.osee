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
package org.eclipse.osee.framework.manager.servlet.ats;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.resource.management.IResource;
import org.w3c.dom.Node;

/**
 * @author Roberto E. Escobar
 */
public class AtsXmlSearch {

   public AtsXmlSearch() {
   }

   public Collection<Node> findPrograms(IResource resource) throws OseeCoreException {
      return XmlUtil.findInResource(resource, "//program");
   }

   public Collection<Node> findBuildsByProgramId(IResource resource, String programId) throws OseeCoreException {
      return XmlUtil.findInResource(resource, "//build[buildProgramId=\"" + programId + "\"]");
   }

   public Collection<Node> findWorkflowsByProgramAndBuild(IResource resource, String programId, String buildId) throws OseeCoreException {
      return XmlUtil.findInResource(resource,
         "//workflow[workflowProgramId=\"" + programId + "\" and workflowBuildId=\"" + buildId + "\"]");
   }

   public Collection<Node> findWorkflowsById(IResource resource, String id) throws OseeCoreException {
      Collection<Node> toReturn = null;
      String predicate = multiIdsToXPath(id);
      if (Strings.isValid(predicate)) {
         StringBuilder builder = new StringBuilder();
         builder.append("//workflow[");
         builder.append(predicate);
         builder.append("]");
         toReturn = XmlUtil.findInResource(resource, builder.toString());
      } else {
         toReturn = java.util.Collections.emptyList();
      }
      return toReturn;
   }

   public String multiIdsToXPath(String idsWithCommas) throws OseeCoreException {
      List<String> predicates = new ArrayList<String>();
      if (Strings.isValid(idsWithCommas)) {
         String[] ids = idsWithCommas.split("[\\s,]+");
         for (String id : ids) {
            predicates.add(idToXPath(id));
         }
      }
      return Collections.toString(predicates, " or ");
   }

   public String idToXPath(String id) throws OseeCoreException {
      StringBuilder builder = new StringBuilder();
      if (IdUtils.isValidLegacyId(id)) {
         builder.append("workflowPcrId=");
      } else if (IdUtils.isValidGUID(id)) {
         builder.append("workflowId=");
      } else if (IdUtils.isValidHRID(id)) {
         builder.append("workflowHrid=");
      } else {
         throw new OseeCoreException(String.format("Invalid id [%s]", id));
      }
      if (builder.length() > 0) {
         builder.append("\"" + id + "\"");
      }
      return builder.toString();
   }
}
