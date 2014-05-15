/*
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ote.remote.messages;

import org.eclipse.osee.ote.message.elements.IEnumValue;

public enum SOCKET_ID implements IEnumValue<SOCKET_ID> {
	
   MSG_UPDATES(0),
   RECORDER(1),
   
	__UNDEFINED(-99999);

   private int value;
   private static int maxValue = 0;
   
   private static SOCKET_ID[] lbaValues;
   private static SOCKET_ID[] lookup = null;
   
   static {
	   lbaValues();
	   if(maxValue < 32000){
		   lookup = new SOCKET_ID[maxValue + 1];
		   for(int i = 0; i < lbaValues.length; i++){
			   lookup[lbaValues[i].value] = lbaValues[i];
		   }
	   }
   }
   
   private SOCKET_ID(int value) {
      this.value = value;
   }
   
   @Override
   public int getIntValue(){
      return value;
   }
   
   public static SOCKET_ID toEnum(int value){
	  	  if(lookup == null){
		  for(SOCKET_ID myEnum: values()){
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
		  SOCKET_ID enumeration = lookup[value];
		  if(enumeration == null){
			  __UNDEFINED.value = value;
			  return __UNDEFINED;
		  } else {
			  return enumeration;
		  }
	  }  
   }
   
   @Override
   public SOCKET_ID getEnum(int value){
	  return toEnum(value);	  
   }
   
   public static SOCKET_ID[] lbaValues(){
      if(lbaValues == null){
      	 int count = 0;
         lbaValues = new SOCKET_ID[values().length-1];
         for(SOCKET_ID myEnum: values()){
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