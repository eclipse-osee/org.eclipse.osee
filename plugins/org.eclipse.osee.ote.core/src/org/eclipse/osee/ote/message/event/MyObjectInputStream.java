package org.eclipse.osee.ote.message.event;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

import org.eclipse.osee.framework.plugin.core.util.ExportClassLoader;

public class MyObjectInputStream extends ObjectInputStream {

   @SuppressWarnings({ "unchecked", "rawtypes" })
   @Override
   public Class resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
      try {
         return ExportClassLoader.getInstance().loadClass(desc.getName());
      } catch (Exception e) {
      }
      return super.resolveClass(desc);
   }

   public MyObjectInputStream(InputStream in) throws IOException {
      super(in);
   }

}