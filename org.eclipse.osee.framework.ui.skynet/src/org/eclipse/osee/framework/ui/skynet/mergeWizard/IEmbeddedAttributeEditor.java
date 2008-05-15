/*
 * Created on Mar 7, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.mergeWizard;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridData;
/**
 * @author fw314c
 *
 */
public interface IEmbeddedAttributeEditor {
	

	public void update(Object value);
	

	public boolean create(Composite composite, GridData gd);
	

	public boolean commit();
	
	
	public boolean canClear();
	 
	
	public boolean canFinish();

}
