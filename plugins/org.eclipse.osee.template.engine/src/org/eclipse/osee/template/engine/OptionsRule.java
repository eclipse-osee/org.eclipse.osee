/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.template.engine;

import org.eclipse.osee.framework.core.data.Identity;
import org.eclipse.osee.framework.core.data.Named;

/**
 * @author Ryan D. Brooks
 */
public abstract class OptionsRule<T extends Identity<String> & Named> extends AppendableRule {
   private final String listId;

   protected String getListId() {
      return listId;
   }

   public OptionsRule(String ruleName) {
      this(ruleName, null);
   }

   public OptionsRule(String ruleName, String listId) {
      super(ruleName);
      this.listId = listId;
   }

   @Override
   public void applyTo(Appendable appendable) throws Exception {
      if (listId == null) {
         appendOptions(appendable);
      } else {
         appendable.append("\n<datalist id=\"");
         appendable.append(listId);
         appendable.append("\">\n");
         appendOptions(appendable);
         appendable.append("</datalist>\n");
      }
   }

   /**
    * This method is provided in the case where accessing the data returned by the implementation of getOptions()
    * requires I/O or is otherwise computationally intensive, because getOptions() is not called until absolutely
    * required. For simpler cases, the concrete class IdentifiableOptionsRule can be used where the options are just
    * supplied in its constructor
    * 
    * @return an Iterable that iterators over the list of options that each have a guid and a name
    * @throws Exception
    */
   public abstract Iterable<T> getOptions() throws Exception;

   private void appendOptions(Appendable appendable) throws Exception {
      for (T option : getOptions()) {
         appendable.append("<option value=\"");
         appendable.append(option.getName());
         appendable.append("\" guid=\"");
         appendable.append(option.getGuid());
         appendable.append("\">\n");
      }
   }
}