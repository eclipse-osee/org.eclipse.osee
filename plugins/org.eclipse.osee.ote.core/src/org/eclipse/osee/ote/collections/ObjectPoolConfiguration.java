package org.eclipse.osee.ote.collections;

public abstract class ObjectPoolConfiguration<T> {

   private int maxSize;
   private boolean preallocate;

   public ObjectPoolConfiguration(int maxSize, boolean preallocate){
      this.maxSize = maxSize; 
      this.preallocate = preallocate;
   }
   
   public int getMaxSize(){
      return maxSize;
   }
   
   public boolean preallocate(){
      return preallocate;
   }

   abstract public T make();

}
