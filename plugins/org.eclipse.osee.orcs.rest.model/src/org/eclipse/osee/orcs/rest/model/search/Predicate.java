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
package org.eclipse.osee.orcs.rest.model.search;

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import org.eclipse.osee.framework.core.enums.TokenDelimiterMatch;
import org.eclipse.osee.framework.jdk.core.util.Collections;

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
   private TokenDelimiterMatch delimiter;

   public Predicate() {
   }

   public Predicate(SearchMethod type, List<String> typeParameters, SearchOp op, List<SearchFlag> flags, TokenDelimiterMatch delimiter, List<String> values) {
      this.type = type;
      this.typeParameters = typeParameters;
      this.op = op;
      this.flags = flags;
      this.values = values;
      this.delimiter = delimiter;
   }

   public void setDelimiter(TokenDelimiterMatch delimiter) {
      this.delimiter = delimiter;
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

   public TokenDelimiterMatch getDelimiter() {
      return delimiter;
   }

   @Override
   public String toString() {
      return String.format("type:[%s],typeParameters:[%s],op[%s],flags[%s],values[%s],delimiter[%s]", type,
         Collections.toString(",", typeParameters), op, Collections.toString(",", flags),
         Collections.toString(",", values), delimiter);
   }

}