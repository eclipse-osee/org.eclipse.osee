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
package org.eclipse.osee.orcs.core.ds;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.IOseeBranch;

/**
 * @author Roberto E. Escobar
 */
public class CriteriaSet implements Cloneable, Iterable<Criteria> {

   private final List<Criteria> criterias = new LinkedList<Criteria>();
   private final IOseeBranch branch;

   public CriteriaSet(IOseeBranch branch) {
      this.branch = branch;
   }

   public IOseeBranch getBranch() {
      return branch;
   }

   public void add(Criteria criteria) {
      criterias.add(criteria);
   }

   public boolean remove(Criteria criteria) {
      return criterias.remove(criteria);
   }

   public Collection<Criteria> getCriterias() {
      return criterias;
   }

   public void reset() {
      criterias.clear();
   }

   @Override
   public CriteriaSet clone() {
      CriteriaSet clone = new CriteriaSet(this.branch);
      clone.criterias.addAll(this.criterias);
      return clone;
   }

   @Override
   public Iterator<Criteria> iterator() {
      return criterias.iterator();
   }

}
