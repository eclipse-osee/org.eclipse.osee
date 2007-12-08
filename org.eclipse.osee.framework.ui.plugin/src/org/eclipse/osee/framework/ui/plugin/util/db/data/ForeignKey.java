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
package org.eclipse.osee.framework.ui.plugin.util.db.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.eclipse.osee.framework.jdk.core.persistence.Xmlizable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Roberto E. Escobar
 */
public class ForeignKey extends ConstraintElement implements Xmlizable {

   List<ReferenceClause> references;

   public ForeignKey(ConstraintTypes constraintType, String schema, String id, boolean deferrable) {
      super(constraintType, schema, id, deferrable);
      references = new ArrayList<ReferenceClause>();
   }

   public void addReference(ReferenceClause reference) {
      references.add(reference);
   }

   public List<ReferenceClause> getReferences() {
      return references;
   }

   public Set<String> getReferencedTables() {
      Set<String> refTables = new TreeSet<String>();
      for (ReferenceClause ref : references) {
         refTables.add(ref.getFullyQualifiedTableName());
      }
      return refTables;
   }

   @Override
   public String toString() {
      String toReturn = super.toString();
      for (ReferenceClause reference : references) {
         toReturn += "\n\t\t" + reference.toString();
      }
      return toReturn;
   }

   @Override
   public Element toXml(Document doc) {
      Element parent = super.toXml(doc);
      for (ReferenceClause reference : references) {
         parent.appendChild(reference.toXml(doc));
      }
      return parent;
   }
}
