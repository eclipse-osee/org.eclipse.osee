package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;
import org.eclipse.osee.framework.ui.skynet.dbHealth.DatabaseHealthTask;
import org.eclipse.osee.framework.ui.skynet.dbHealth.DatabaseHealthTask.Operation;
import org.osgi.framework.Bundle;

/**
 * 
 * @author Jeff C. Phillips
 *
 */
public class DatabaseHealth extends AbstractBlam {
	private Map<String, DatabaseHealthTask> dbTasks = new HashMap<String, DatabaseHealthTask>();
	
	public DatabaseHealth(){
		loadExtensions();
	}
	
	@Override
	public void runOperation(BlamVariableMap variableMap,
			IProgressMonitor monitor) throws Exception {
		runTasks(variableMap, monitor);
	}
	
	private void loadExtensions() {
		IExtensionPoint point = Platform.getExtensionRegistry()
				.getExtensionPoint(
						"org.eclipse.osee.framework.ui.skynet.DBHealthTask");
		IExtension[] extensions = point.getExtensions();
		for (IExtension extension : extensions) {
			IConfigurationElement[] elements = extension
					.getConfigurationElements();
			String classname = null;
			String bundleName = null;
			for (IConfigurationElement element : elements) {
				classname = element.getAttribute("class");
				bundleName = element.getContributor().getName();

				if (classname != null && bundleName != null) {
					Bundle bundle = Platform.getBundle(bundleName);
					try {
						Class<?> taskClass = bundle.loadClass(classname);
						Object obj = taskClass.newInstance();
						DatabaseHealthTask task = (DatabaseHealthTask) obj;
						
						if(task.getVerifyTaskName() != null){
							dbTasks.put(task.getVerifyTaskName(), task);
						}
						if(task.getFixTaskName() != null){
							dbTasks.put(task.getFixTaskName(), task);
						}
					} catch (Exception ex) {
					}
				}
			}
		}
	}
	
	private void runTasks(BlamVariableMap variableMap,
			IProgressMonitor monitor) throws Exception{
		
		monitor.beginTask("Database Health", dbTasks.size());
	      StringBuilder builder = new StringBuilder();
		
        for (String taskName : dbTasks.keySet()) {
            if (variableMap.getBoolean(taskName)) {
            	monitor.setTaskName(taskName);
            	DatabaseHealthTask task = dbTasks.get(taskName);
            	Operation  operation;
               
            	if(taskName.equals(task.getFixTaskName())){
            	   operation = Operation.Fix;
            	} else{
            	   operation = Operation.Verify;
            	}
            	task.run(variableMap, monitor, operation, builder);
            	monitor.worked(1);
            }
         }
        appendResultLine(builder.toString());
	}
	
   public String getXWidgetsXml() {
      StringBuilder builder = new StringBuilder();
      builder.append("<xWidgets>");
      builder.append("<XWidget xwidgetType=\"XLabel\" displayName=\"Select Operations to Run:\"/>");
      for (String  taskName : dbTasks.keySet()) {
         builder.append(getOperationsCheckBoxes(taskName));
      }
      builder.append("</xWidgets>");
      return builder.toString();
	}

   private String getOperationsCheckBoxes(String checkboxName) {
      StringBuilder builder = new StringBuilder();
      builder.append("<XWidget xwidgetType=\"XCheckBox\" displayName=\"");
      builder.append(checkboxName);
      builder.append("\" labelAfter=\"true\" horizontalLabel=\"true\"/>");
      return builder.toString();
   }
}
