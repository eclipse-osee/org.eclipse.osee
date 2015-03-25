package org.eclipse.osee.ote.collections;

import java.util.concurrent.ArrayBlockingQueue;

public class ObjectPool<T> {
   
   private final ArrayBlockingQueue<T> objs;
   private final ObjectPoolConfiguration<T> config;
   
   public ObjectPool(ObjectPoolConfiguration<T> config) {
      this.config = config;
      objs = new ArrayBlockingQueue<T>(config.getMaxSize());
      if(config.preallocate()){
         for(int i = 0; i < config.getMaxSize(); i++){
            objs.offer(config.make());
         }
      }
   }
   
   public T getObject(){
      T obj = objs.poll();
      if(obj == null){
         obj = config.make();
      }
      return obj;
   }
   
   public void returnObj(T obj){
      objs.offer(obj);
   }
   
}
