package org.eclipse.osee.framework.ui.skynet.test.util.enumeration;

import java.util.Set;
import org.eclipse.osee.framework.ui.skynet.util.enumeration.AbstractEnumeration;

public class OrderedEnum extends AbstractEnumeration {

   public static OrderedEnum One = new OrderedEnum("One");
   public static OrderedEnum Two = new OrderedEnum("Two");
   public static OrderedEnum Three = new OrderedEnum("Three");
   public static OrderedEnum Four = new OrderedEnum("Four");
   public static OrderedEnum Five = new OrderedEnum("Five");
   public static OrderedEnum Six = new OrderedEnum("Six");
   public static OrderedEnum Cancelled = new OrderedEnum("Cancelled");
   public static OrderedEnum Completed = new OrderedEnum("Completed");

   public OrderedEnum(String pageName) {
      super(OrderedEnum.class, pageName);
   }

   public static OrderedEnum valueOf(String pageName) {
      return AbstractEnumeration.valueOfPage(OrderedEnum.class, pageName);
   }

   public static Set<OrderedEnum> values() {
      return AbstractEnumeration.pages(OrderedEnum.class);
   }

}
