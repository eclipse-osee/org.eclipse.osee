/*
 * Created on Jan 15, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.future;

import java.net.URI;
import org.eclipse.osee.framework.messaging.Component;

/**
 * @author b1122182
 */
public class NodeInfo {

   private final URI uri;
   private final String name;
   private String nameWithColon;

   public NodeInfo(String name, URI uri) {
      this.uri = uri;
      this.name = name;
   }

   public URI getUri() {
      return uri;
   }

   @Override
   public String toString() {
      return name + ":" + uri;
   }

   public String getComponentName() {
      return name;
   }

   public String getComponentNameForRoutes() {
      return nameWithColon;
   }

   public boolean isVMComponent() {
      return getComponentName().equals(Component.VM);
   }
}
