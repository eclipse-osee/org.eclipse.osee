package org.eclipse.osee.framework.ui.skynet.test.util.enumeration;

import java.util.Set;
import org.eclipse.osee.framework.ui.skynet.util.enumeration.AbstractEnumeration;

public class OneEnum extends AbstractEnumeration {

   public static OneEnum Endorse = new OneEnum("Endorse", "This is OneStates Endorse");
   public static OneEnum Cancelled = new OneEnum("Cancelled");
   public static OneEnum Completed = new OneEnum("Completed");

   public OneEnum(String pageName) {
      super(OneEnum.class, pageName);
   }

   public OneEnum(String pageName, String description) {
      super(OneEnum.class, pageName);
      setDescription(description);
   }

   public static OneEnum valueOf(String pageName) {
      return AbstractEnumeration.valueOfPage(OneEnum.class, pageName);
   }

   public static Set<OneEnum> values() {
      return AbstractEnumeration.pages(OneEnum.class);
   }

}
