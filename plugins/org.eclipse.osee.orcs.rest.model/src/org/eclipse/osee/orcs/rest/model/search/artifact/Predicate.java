/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.orcs.rest.model.search.artifact;

import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.util.Collections;

/**
 * @author John R. Misinco
 * @author Roberto E. Escobar
 */
@XmlRootElement(name = "predicate")
public class Predicate {
   private SearchMethod type;
   private List<String> typeParameters;
   private List<String> values;
   private QueryOption[] options;

   public Predicate() {
   }

   public Predicate(SearchMethod type, List<String> typeParameters, List<String> values, QueryOption... options) {
      this.type = type;
      this.typeParameters = typeParameters;
      this.values = values;
      this.options = options;
   }

   public void setType(SearchMethod type) {
      this.type = type;
   }

   public void setTypeParameters(List<String> typeParameters) {
      this.typeParameters = typeParameters;
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

   public List<String> getValues() {
      return values;
   }

   public QueryOption[] getOptions() {
      return options;
   }

   public void setOptions(QueryOption[] options) {
      this.options = options;
   }

   @Override
   public String toString() {
      return String.format("type:[%s],typeParameters:[%s],values[%s],options[%s]", type,
         Collections.toString(",", typeParameters), Collections.toString(",", values),
         Collections.toString(",", options));
   }

}