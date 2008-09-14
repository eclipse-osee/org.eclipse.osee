/*
 * Created on Sep 13, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.plugin.event;

/**
 * @author Donald G. Dunne
 */
public class Sender {

   public enum Source {
      Local, Remote
   };

   Source source;

   /**
    * @return the source
    */
   public Source getSource() {
      return source;
   }

   /**
    * @param source the source to set
    */
   public void setSource(Source source) {
      this.source = source;
   }

   Object sourceObject;
   int author;

   public Sender(Source source, Object sourceObject, int author) {
      this.source = source;
      this.sourceObject = sourceObject;
      this.author = author;
   }

   /**
    * @return the sender
    */
   public Object getSourceObject() {
      return sourceObject;
   }

   /**
    * @param sender the sender to set
    */
   public void setSourceObject(Object sourceObject) {
      this.sourceObject = sourceObject;
   }

   /**
    * @return the author
    */
   public int getAuthor() {
      return author;
   }

   /**
    * @param author the author to set
    */
   public void setAuthor(int author) {
      this.author = author;
   }

}
