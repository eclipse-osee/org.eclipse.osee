package org.eclipse.osee.ote.core;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class is useful for cases where frequent looping and thread safety happen. 
 * It forces you to get a reference to the backing array and expects you to do a 
 * non Iterator based loop so that no Iterator objects are created to minimized GC churn. 
 * 
 * @author Andrew M Finkbeiner
 *
 * @param <E>
 */
public class CopyOnWriteNoIteratorList<E> {
   
   private ReentrantLock lock = new ReentrantLock(); 
   private AtomicReference<E[]> data = new AtomicReference<>();
   private Class<E> type;
   
   public CopyOnWriteNoIteratorList(Class<E> type){
      this.type = type;
      data.set(newArray(0));
   }
   
   @SuppressWarnings("unchecked")
   private E[] newArray(int size){
      return (E[]) Array.newInstance(type, size);
   }
   
   public void add(E item){
      try{
         lock.lock();
         E[] ref = data.get();
         E[] newdata = newArray(ref.length + 1);
         System.arraycopy(ref, 0, newdata, 0, ref.length);
         newdata[newdata.length-1] = item;
         data.set(newdata);
      } finally{
         lock.unlock();
      }
   }
   
   public boolean remove(E item){
      boolean returnvalue = false;
      try{
         lock.lock();
         int index = -1;
         E[] ref = data.get();
         for(int i = 0; i < ref.length; i++){
            if(item.equals(ref[i])){
               index = i;
               break;
            }
         }
         if(index > -1){
            E[] newdata = newArray(ref.length - 1);
            if(index == 0){
               System.arraycopy(ref, 1, newdata, 0, newdata.length);   
            } else if (index == (ref.length - 1)){
               System.arraycopy(ref, 0, newdata, 0, newdata.length);
            } else {
               System.arraycopy(ref, 0, newdata, 0, index);
               System.arraycopy(ref, index+1, newdata, index, ref.length - (index + 1));
            }
            data.set(newdata);
            returnvalue = true;
         }
      } finally {
         lock.unlock();
      }
      return returnvalue;
   }
   
   public boolean contains(E item){
      boolean returnValue = false;
      try{
         lock.lock();
         int index = -1;
         E[] ref = data.get();
         for(int i = 0; i < ref.length; i++){
            if(item.equals(ref[i])){
               index = i;
               break;
            }
         }
         if(index > -1){
           returnValue = true;
         }
      } finally {
         lock.unlock();
      } 
      return returnValue;
   }
   
   public E[] get(){
      return data.get();
   }

   public int length() {
      return data.get().length;
   }

   public void clear() {
      try{
         lock.lock();
         data.set(newArray(0));
      } finally{
         lock.unlock();
      }
   }

   public Collection<E> fillCollection(Collection<E> arrayList) {
      try{
         lock.lock();
         E[] ref = data.get();
         for(int i = 0; i < ref.length; i++){
            arrayList.add(ref[i]);
         }
      } finally {
         lock.unlock();
      }
      return arrayList;
   }
   
}
