OSEE contains an Eclipse extension point that allows features to be
added to OSEE without having to rebuild the application.

This is similar to "scripts" for other tools that you may have
encountered but, in OSEE's case, the scripts are actually written in the
same Java language as that used to develop the application.

This section of the Wiki, ***which is work in progress***, will describe
what is needed to create a new BLAM.

## What do I need to know?

  - Because BLAMs are added using an Eclipse extension point, you need
    to understand Java syntax to be able to create a new BLAM
  - BLAMs use an extension point called AbstractBlam defined in the
    package org.eclipse.osee.framework.ui.skynet.blam. It is therefore
    wise to look at the OSEE source for this package to understand how
    your code extends OSEE
  - The "built-in" BLAMs supplied in OSEE by default are located in the
    package org.eclipse.osee.framework.ui.skynet.blam.operation. Looking
    at the source for this package will give hints on BLAM construction

## How to create a BLAM

In Eclipse select File-\> New Project. From the list select "Plug-In
Project".

Put a suitable name in the "Project name" box and leave everything else
with default settings. Click Next to move to the Content dialog.

In the Content dialog, clear the following options:

  - Generate an activator
  - This plug-in will make contributions to the UI
  - Enable API analysis

Select the "No" box for "Would you like to create a rich client
application?" Click Next to move to the "Template" dialog.

Do not select a template, just click on Finish.

Open Manifest.MF in the new project and go to the "Dependencies" tab.
Add "org.eclipse.osee.framework.ui.skynet.blam" as a required imported
package. Go to the "Extension tab" and select "Add". Select the
extension point "org.eclipse.osee.framework.ui.skynet.BlamOperation"

Add a new class with the name of your BLAM

Modify the code to read something like

``` java
package org.eclipse.osee.blam;

import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;

/**

 * Put your description here.
 *
 * @author Put your name here
 */

public class MyNewBlam extends AbstractBlam
{
  public void runOperation(VariableMap variableMap, IProgressMonitor monitor)
    throws Exception
  {
    // This is where the code for the BLAM functionality itself goes
  }

  // Add "artifact" to the parameter list window
  public String getXWidgetsXml()
  {
    return "<xWidgets><XWidget xwidgetType=\"XListDropViewer\" displayName=\"artifact\" /></xWidgets>";
  }

  // We want this BLAM to appear in the "Define" category in the BLAM menu
  public Collection<String> getCategories()
  {
    return Arrays.asList(new String[] { "Define" });
  }
}
```