/*
 * Created on Jul 27, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging;

/**
 * @author Andrew M. Finkbeiner
 */
public enum Component {

   VM("osee-vm"),
   JMS("osee-jms");

   private String name;
   private String nameWithColon;

   private Component(String name) {
      this.name = name;
      this.nameWithColon = name + ":";
   }

   @Override
   public String toString() {
      return name + ":";
   }

   public String getComponentName() {
      return name;
   }

   public String getComponentNameForRoutes() {
      return nameWithColon;
   }

   public boolean isVMComponent() {
      return this.equals(Component.VM);
   }
}
