package org.eclipse.osee.ote.container;

import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.wizards.IClasspathContainerPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class OteContainerPage extends WizardPage 
               implements IClasspathContainerPage {

   
   public OteContainerPage() {
      super("OTE CONTAINER");
   }
   
   /* (non-Javadoc)
    * @see org.eclipse.jdt.ui.wizards.IClasspathContainerPage#finish()
    */
   @Override
   public boolean finish() {
      return true;
   }

   /* (non-Javadoc)
    * @see org.eclipse.jdt.ui.wizards.IClasspathContainerPage#getSelection()
    */
   @Override
   public IClasspathEntry getSelection() {
      return JavaCore.newContainerEntry(OteClasspathContainer.ID);
   }

   /* (non-Javadoc)
    * @see org.eclipse.jdt.ui.wizards.IClasspathContainerPage#setSelection(org.eclipse.jdt.core.IClasspathEntry)
    */
   @Override
   public void setSelection(IClasspathEntry containerEntry) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
    */
   @Override
   public void createControl(Composite parent) {
      Composite comp = new Composite(parent, SWT.None);
      GridLayout layout = new GridLayout(1, true);
      comp.setLayout(layout);
      
      GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
      comp.setLayoutData(data);
      
      Label label = new Label(comp, SWT.BORDER);
      label.setText("JUST CLICK FINISH YOU FOOL!!!");
      
      data = new GridData(SWT.FILL, SWT.FILL, true, true);
      label.setLayoutData(data);
      setControl(comp);    
      
      
   }

   
}
