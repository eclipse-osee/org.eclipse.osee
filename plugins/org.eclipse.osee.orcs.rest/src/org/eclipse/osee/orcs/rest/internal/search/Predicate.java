/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.internal.search;

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.orcs.rest.internal.search.dsl.SearchFlag;
import org.eclipse.osee.orcs.rest.internal.search.dsl.SearchMethod;
import org.eclipse.osee.orcs.rest.internal.search.dsl.SearchOp;

/**
 * @author John R. Misinco
 * @author Roberto E. Escobar
 */
@XmlRootElement(name = "predicate")
public class Predicate {
   private SearchMethod type;
   private List<String> typeParameters;
   private SearchOp op;
   private List<SearchFlag> flags;
   private List<String> values;

   public Predicate() {
   }

   public Predicate(SearchMethod type, List<String> typeParameters, SearchOp op, List<SearchFlag> flags, List<String> values) {
      this.type = type;
      this.typeParameters = typeParameters;
      this.op = op;
      this.flags = flags;
      this.values = values;
   }

   public void setType(SearchMethod type) {
      this.type = type;
   }

   public void setTypeParameters(List<String> typeParameters) {
      this.typeParameters = typeParameters;
   }

   public void setOp(SearchOp op) {
      this.op = op;
   }

   public void setFlags(List<SearchFlag> flags) {
      this.flags = flags;
   }

   public void setValues(List<String> values) {
      this.values = values;
   }

   public SearchMethod getType() {
      return type;
   }

   public List<String> getTypeParameters() {
      return typeParameters;
   }

   public SearchOp getOp() {
      return op;
   }

   public List<SearchFlag> getFlags() {
      return flags;
   }

   public List<String> getValues() {
      return values;
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("type:[");
      sb.append(type);
      sb.append("],typeParameters:[");
      sb.append(Collections.toString(",", typeParameters));
      sb.append("],");
      sb.append("op:[");
      sb.append(op);
      sb.append("],flags:[");
      sb.append(Collections.toString(",", flags));
      sb.append("],values:[");
      sb.append(Collections.toString(",", values));
      sb.append("]");
      return sb.toString();
   }

}