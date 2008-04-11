/*
 * Created on Apr 4, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.attribute;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.logging.Level;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;

/**
 * @author Theron Virgin
 */
public class AttributeObjectConverter {

   @SuppressWarnings( {"unchecked"})
   public static Object stringToObject(Attribute attribute, String value) {
      Class clas = attribute.getAttributeType().getBaseAttributeClass();

      if (clas.equals(BooleanAttribute.class)) {
         return new Boolean(value.equals(BooleanAttribute.booleanChoices[0]));
      }
      if (clas.equals(IntegerAttribute.class)) {
         if (value.equals("")) return new Integer(0);
         return new Integer(value);
      }
      if (clas.equals(DateAttribute.class)) {
         if (value.equals("")) return new Date(1);
         return new Date(Long.parseLong(value));
      }
      if (clas.equals(FloatingPointAttribute.class)) {
         if (value.equals("")) return new Double(0);
         return new Double(value);
      }
      if (clas.equals(SimpleDateAttribute.class)) {
         if (value.equals("")) return new Date(1);
         try{
            return DateFormat.getDateInstance().parse(value);
         }catch (ParseException ex){
            SkynetActivator.getLogger().log(Level.SEVERE,
                  "Could not parse the string value: " + value + " into a date");
         }
      }
      if (clas.equals(EnumeratedAttribute.class)) {
         return value;
      }
      if (clas.equals(StringAttribute.class)) {
         return value;
      }
      if (clas.equals(BlobWordAttribute.class)) {
         return value;
      }
      if (clas.equals(JavaObjectAttribute.class)) {
         return value;
      }
      if (clas.equals(CompressedContentAttribute.class)) {
         return value;
      }

      SkynetActivator.getLogger().log(Level.SEVERE,
            "The Attribute Object Creator for the " + clas + " is not implemented yet");
      return value;
   }

}
