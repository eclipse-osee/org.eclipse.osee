/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Donald G. Dunne
 */
public class JsonRelations {

   public List<JsonRelation> relations = new ArrayList<JsonRelation>();

   public List<JsonRelation> getRelations() {
      return relations;
   }

   public void setRelations(List<JsonRelation> relations) {
      this.relations = relations;
   }

   public Object add(JsonRelation relation) {
      return relations.add(relation);
   }

}
