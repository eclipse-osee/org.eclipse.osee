/*
 * Created on May 15, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.definitions;

import java.io.Serializable;

import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.messaging.Message;
import org.eclipse.osee.framework.messaging.Source;
import org.eclipse.osee.framework.messaging.id.MessageId;

/**
 * @author b1541174
 */
public class PropertyStoreMessage implements Message, Serializable {

   private static final long serialVersionUID = -8736301654726742145L;
   
   private MessageId messageId;
   private Source source;
   private PropertyStore store;

   public PropertyStoreMessage(MessageId messageId, Source source)
   {
      this.messageId = messageId;
      this.source = source;
      this.store = new PropertyStore("org.eclipse.osee.framework.messaging.definitions.PropertyStoreMessage");
   }
   
   @Override
   public MessageId getId() {
      return messageId;
   }

   @Override
   public Source getSource() {
      return source;
   }
   
   public PropertyStore getStore() {
      return store;
   }

}
