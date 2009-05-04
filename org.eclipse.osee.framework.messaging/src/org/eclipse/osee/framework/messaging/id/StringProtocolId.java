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
public class StringProtocolId extends StringId implements ProtocolId {

   private static final long serialVersionUID = 2526404617710228921L;
   
   public StringProtocolId(Namespace namespace, Name name) {
      super(namespace, name);
   }
}
