/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.integration.tests.ats.workflow.review.defect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.util.IValueProvider;
import org.eclipse.osee.ats.ide.workflow.review.ReviewDefectItem;
import org.eclipse.osee.framework.jdk.core.util.AXml;

/**
 * @author Donald G. Dunne
 */
public class MockDefectValueProvider implements IValueProvider {

   private final List<ReviewDefectItem> defectItems;

   public MockDefectValueProvider(List<ReviewDefectItem> defectItems) {
      this.defectItems = defectItems;
   }

   @Override
   public String getName() {
      return "Defects";
   }

   @Override
   public boolean isEmpty() {
      return defectItems.isEmpty();
   }

   @Override
   public Collection<String> getValues() {
      List<String> values = new ArrayList<>();
      for (ReviewDefectItem item : defectItems) {
         values.add(AXml.addTagData("Item", item.toXml()));
      }
      return values;
   }

   @Override
   public Collection<Date> getDateValues() {
      return null;
   }

}
