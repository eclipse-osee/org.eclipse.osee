package org.eclipse.osee.ote.properties;

public interface OteProperties {
   String getKey();
   void setValue(String value);  
   String getValue();
   String getValue(String defaultValue);
}
