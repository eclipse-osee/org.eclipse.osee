/*
 * Created on Aug 5, 2015
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.api.task;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Donald G. Dunne
 */
@XmlRootElement
public class JaxAtsTasks {

   public List<JaxAtsTask> tasks = new ArrayList<>();

   public List<JaxAtsTask> getTasks() {
      return tasks;
   }

   public void setTasks(List<JaxAtsTask> tasks) {
      this.tasks = tasks;
   }

}
