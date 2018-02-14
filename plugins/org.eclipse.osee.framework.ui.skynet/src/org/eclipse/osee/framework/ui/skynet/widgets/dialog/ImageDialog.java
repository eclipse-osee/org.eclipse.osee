/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.framework.ui.skynet.widgets.dialog;

import java.io.File;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.core.data.OseeData;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Donald G. Dunne
 */
public class ImageDialog extends MessageDialog {
   private String title = "Image Viewer";
   private static String buttons[] = new String[] {"Export as JPG", "Export as PNG", "Close"};
   private final Image image;

   public ImageDialog(Image image, Shell parentShell) {
      this(image, parentShell, buttons, 3);
   }

   public ImageDialog(Image image, Shell parentShell, String[] buttons, int defaultButton) {
      super(parentShell, "", null, "", MessageDialog.NONE, buttons, defaultButton);
      setShellStyle(getShellStyle() | SWT.RESIZE);
      this.image = image;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   @Override
   protected Control createDialogArea(Composite parent) {
      getShell().setText(title);
      Composite comp = new Composite(parent, SWT.NONE);
      comp.setLayout(ALayout.getZeroMarginLayout());
      comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      comp.setBackground(Displays.getSystemColor(SWT.COLOR_BLUE));

      ScrolledComposite sc = new ScrolledComposite(comp, SWT.V_SCROLL | SWT.H_SCROLL);
      Canvas canvas = new Canvas(sc, SWT.BORDER);
      sc.setContent(canvas);
      canvas.setBounds(image.getBounds());
      canvas.addPaintListener(new PaintListener() {
         @Override
         public void paintControl(PaintEvent e) {
            e.gc.drawImage(image, 0, 0);
         }
      });
      comp.layout();
      return comp;
   }

   private enum ImageType {
      JPG,
      PNG
   };

   protected void handleSave(ImageType imageType) {
      ImageLoader loader = new ImageLoader();
      ImageData imgData = image.getImageData();
      loader.data = new ImageData[] {imgData};
      int format = 0;
      if (imageType == ImageType.JPG) {
         format = SWT.IMAGE_JPEG;
      } else if (imageType == ImageType.PNG) {
         format = SWT.IMAGE_PNG;
      }
      File file = OseeData.getFile(GUID.create() + "." + imageType.name());
      loader.save(file.getAbsolutePath(), format);
      Program.launch(file.getAbsolutePath());
   }

   @Override
   protected void buttonPressed(int buttonId) {
      if (buttonId == 0) {
         handleSave(ImageType.JPG);
      } else if (buttonId == 1) {
         handleSave(ImageType.PNG);
      } else {
         close();
      }
      setReturnCode(buttonId);
   }

   public String getTitle() {
      return title;
   }

}
