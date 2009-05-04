/*
 * Created on Apr 11, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.id;


/**
 * @author b1528444
 *
 */
public class StringMessageId extends StringId implements MessageId {

   private static final long serialVersionUID = 5645233194938964314L;

   public StringMessageId(Namespace namespace, Name name) {
      super(namespace, name);
   }   
}
