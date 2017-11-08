/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.config;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Donald G. Dunne
 */
public class AtsViews {
   private final List<AtsAttributeValueColumn> attrColumns = new ArrayList<>();

   public List<AtsAttributeValueColumn> getAttrColumns() {
      return attrColumns;
   }

}
