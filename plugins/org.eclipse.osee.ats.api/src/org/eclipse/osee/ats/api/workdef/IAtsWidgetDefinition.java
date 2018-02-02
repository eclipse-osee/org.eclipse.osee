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
package org.eclipse.osee.ats.api.workdef;

/**
 * @author Donald G. Dunne
 */
public interface IAtsWidgetDefinition extends IAtsLayoutItem {

   /**
    * Identification
    */
   @Override
   public String getName();

   public String getToolTip();

   public String getDescription();

   /**
    * Storage returns name of the attribute type used for storage, if there is no attribute storage, this will return
    * null
    */
   public String getAtrributeName();

   public String getDefaultValue();

   /**
    * Options
    */
   public boolean is(WidgetOption widgetOption);

   public IAtsWidgetOptionHandler getOptions();

   public void setConstraint(double min, double max);

   public Double getMax();

   public Double getMin();

   public void set(WidgetOption widgetOption);

   /**
    * Widget Type
    */
   public String getXWidgetName();

   public void setXWidgetName(String xWidgetName);

   public int getHeight();

   public void setHeight(int height);

   @Override
   public String toString();

}