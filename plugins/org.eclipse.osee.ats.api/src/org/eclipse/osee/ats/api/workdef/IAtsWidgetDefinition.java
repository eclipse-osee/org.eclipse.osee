/*
 * Created on Jun 20, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.api.workdef;

import java.util.List;

/**
 * @author Donald G. Dunne
 */
public interface IAtsWidgetDefinition extends IAtsLayoutItem {

   /**
    * Identification
    */
   @Override
   public abstract String getName();

   public abstract String getToolTip();

   public abstract String getDescription();

   /**
    * Storage
    */
   public abstract String getAtrributeName();

   public abstract String getDefaultValue();

   /**
    * Options
    */
   public abstract boolean is(WidgetOption widgetOption);

   public abstract IAtsWidgetOptionHandler getOptions();

   public abstract List<IAtsWidgetConstraint> getConstraints();

   public abstract void set(WidgetOption widgetOption);

   /**
    * Widget Type
    */
   public abstract String getXWidgetName();

   public abstract void setXWidgetName(String xWidgetName);

   public abstract int getHeight();

   public abstract void setHeight(int height);

   @Override
   public abstract String toString();

}