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
   private final Component component;

   public NodeInfo(URI uri, Component component) {
      this.uri = uri;
      this.component = component;
   }

   public URI getUri() {
      return uri;
   }

   public Component getComponent() {
      return component;
   }
}
