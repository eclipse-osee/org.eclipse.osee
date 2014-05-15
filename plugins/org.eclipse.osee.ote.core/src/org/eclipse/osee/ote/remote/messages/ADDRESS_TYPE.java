/*
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ote.remote.messages;

import org.eclipse.osee.ote.message.elements.IEnumValue;

public enum ADDRESS_TYPE implements IEnumValue<ADDRESS_TYPE> {
	
   IPV4(0),
   IPV6(1),
   
	__UNDEFINED(-99999);

   private int value;
   private static int maxValue = 0;
   
   private static ADDRESS_TYPE[] lbaValues;
   private static ADDRESS_TYPE[] lookup = null;
   
   static {
	   lbaValues();
	   if(maxValue < 32000){
		   lookup = new ADDRESS_TYPE[maxValue + 1];
		   for(int i = 0; i < lbaValues.length; i++){
			   lookup[lbaValues[i].value] = lbaValues[i];
		   }
	   }
   }
   
   private ADDRESS_TYPE(int value) {
      this.value = value;
   }
   
   @Override
   public int getIntValue(){
      return value;
   }
   
   public static ADDRESS_TYPE toEnum(int value){
	  	  if(lookup == null){
		  for(ADDRESS_TYPE myEnum: values()){
			 if(myEnum.getIntValue() == value){
			    return myEnum;
			 }
		  }
		  __UNDEFINED.value = value;
		  return __UNDEFINED;
	  } else if(value < 0 || value >= lookup.length ){
		  __UNDEFINED.value = value;
		  return __UNDEFINED;
	  } else {
		  ADDRESS_TYPE enumeration = lookup[value];
		  if(enumeration == null){
			  __UNDEFINED.value = value;
			  return __UNDEFINED;
		  } else {
			  return enumeration;
		  }
	  }  
   }
   
   @Override
   public ADDRESS_TYPE getEnum(int value){
	  return toEnum(value);	  
   }
   
   public static ADDRESS_TYPE[] lbaValues(){
      if(lbaValues == null){
      	 int count = 0;
         lbaValues = new ADDRESS_TYPE[values().length-1];
         for(ADDRESS_TYPE myEnum: values()){
			 if(!"__UNDEFINED".equals(myEnum.name())){
			    lbaValues[count++] = myEnum;
			    if(myEnum.value > maxValue){
			    	maxValue = myEnum.value;
			    }
			 }
	  	 }
      }
      return lbaValues;
   }
}