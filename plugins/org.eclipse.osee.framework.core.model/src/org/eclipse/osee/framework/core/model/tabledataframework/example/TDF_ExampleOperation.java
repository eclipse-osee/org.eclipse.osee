/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.model.tabledataframework.example;

import java.util.Arrays;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.osee.framework.core.model.tabledataframework.TableData;
import org.eclipse.osee.framework.core.model.tabledataframework.TableFormatter;
import org.eclipse.osee.framework.core.operation.ConsoleLogger;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.OperationLogger;

//@formatter:off
/**
 * @author Shawn F. Cook
 *
 * This class and the classes it uses including the various interfaces and abstract classes it
 * makes use of are meant to demonstrate how to use the suite of classes collectively known as The Table Data Framework
 * - a suite to generate tabular data and output it in a generic and usable form.
 *
 * The output of this class and the sample classes it uses is print outs to the Console that should look like this:
 * ExampleTableData1
Many Columns:4
[Number, Concat Number Letter, Errors (CSV)]
[1, 1A,  ]
[1, 1B,  ]
[1, 1C,  ]
[1, 1D,  ]
[1, 1E,  ]
[1, 1F,  ]
[1, 1G,  ]
[2, 2A,  ]
[2, 2B,  ]
[2, 2C,  ]
[2, 2D,  ]
[2, 2E,  ]
[2, 2F,  ]
[2, 2G,  ]
[3, 3A,  ]
[3, 3B,  ]
[3, 3C,  ]
[3, 3D,  ]
[3, 3E,  ]
[3, 3F,  ]
[3, 3G,  ]
[4, 4A,  ]
[4, 4B,  ]
[4, 4C,  ]
[4, 4D,  ]
[4, 4E,  ]
[4, 4F,  ]
[4, 4G,  ]
[5, 5A,  ]
[5, 5B,  ]
[5, 5C,  ]
[5, 5D,  ]
[5, 5E,  ]
[5, 5F,  ]
[5, 5G,  ]
[6, 6A,  ]
[6, 6B,  ]
[6, 6C,  ]
[6, 6D,  ]
[6, 6E,  ]
[6, 6F,  ]
[6, 6G,  ]
[7, 7A,  ]
[7, 7B,  ]
[7, 7C,  ]
[7, 7D,  ]
[7, 7E,  ]
[7, 7F,  ]
[7, 7G,  ]
[8, 8A,  ]
[8, 8B,  ]
[8, 8C,  ]
[8, 8D,  ]
[8, 8E,  ]
[8, 8F,  ]
[8, 8G,  ]
[9, 9A,  ]
[9, 9B,  ]
[9, 9C,  ]
[9, 9D,  ]
[9, 9E,  ]
[9, 9F,  ]
[9, 9G,  ]
[10, 10A,  ]
[10, 10B,  ]
[10, 10C,  ]
[10, 10D,  ]
[10, 10E,  ]
[10, 10F,  ]
[10, 10G,  ]


ExampleTableData2
Many Columns:2
[Number, Cumulative Sum]
[1, 1]
[2, 3]
[3, 6]
[4, 10]
[5, 15]
[6, 21]
[7, 28]
[8, 36]
[9, 45]
[10, 55]


Done.

 */
//@formatter:on

public class TDF_ExampleOperation implements IOperation {

   @Override
   public String getName() {
      return getClass().getSimpleName();
   }

   @Override
   public IStatus run(SubMonitor subMonitor) {
      TableFormatter tableFormatter = new ExampleTableFormatter();
      TableData exTableData1 = new ExampleTableData1();
      TableData exTableData2 = new ExampleTableData2();

      try {
         tableFormatter.writeReport(subMonitor, Arrays.asList(exTableData1, exTableData2));
      } catch (Exception ex) {
         //OseeLog.log(Activator.class, Level.SEVERE, ex.toString());
      }

      return Status.OK_STATUS;
   }

   @Override
   public OperationLogger getLogger() {
      return new ConsoleLogger();
   }

}
