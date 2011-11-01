/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rightsimport com.vaadin.Application;
import com.vaadin.service.ApplicationContext.TransactionListener;
he Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.display.view.web.components;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.osee.display.api.components.PagingComponent;
import org.eclipse.osee.display.view.web.CssConstants;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

/**
 * @author Shawn F. Cook
 */
@SuppressWarnings("serial")
public class OseePagingComponent extends HorizontalLayout implements PagingComponent {

   private int manyItemsTotal = 0;
   private int manyItemsPerPage = 15;
   private boolean allItemsPerPage = false;//If TRUE then manyItemsPerPage is ignored.
   private int manyPages = 0;
   private int currentPage = 0;
   private final int MAX_PAGE_NUMBERS_SHOWN = 4;
   private final Button firstButton = new Button("<< First");
   private final Button previousButton = new Button("< Prev");
   private final Button nextButton = new Button("Next >");
   private final Button lastButton = new Button("Last >>");
   private final HorizontalLayout hLayout_PageNumbers = new HorizontalLayout();

   public OseePagingComponent() {
      super();
      createLayout();
   }

   private void updateManyPages() {
      if (manyItemsPerPage > 0) {
         manyPages = manyItemsTotal / manyItemsPerPage;
         int remainder = manyItemsTotal % manyItemsPerPage;
         if (remainder > 0) {
            manyPages += 1; //round up.
         }
      }
      setCurrentPage(currentPage);
   }

   @Override
   public void setManyItemsTotal(int manyItemsTotal) {
      this.manyItemsTotal = manyItemsTotal;
      updateManyPages();
      //      updateLayout();<--Not needed because it is called in updateManyPages()
   }

   public void setCurrentPage(int currentPage) {
      this.currentPage = currentPage;
      if (this.currentPage < 0) {
         this.currentPage = 0;
      }

      if (this.currentPage >= manyPages) {
         this.currentPage = manyPages - 1;
      }

      if (manyPages == 0) {
         this.currentPage = 0;
      }
      updateLayout();
   }

   private void createLayout() {
      setSizeUndefined();

      Label spacer1 = new Label();
      spacer1.setWidth(15, UNITS_PIXELS);

      Label spacer2 = new Label();
      spacer2.setWidth(15, UNITS_PIXELS);

      Label spacer3 = new Label();
      spacer3.setWidth(15, UNITS_PIXELS);

      Label spacer4 = new Label();
      spacer4.setWidth(15, UNITS_PIXELS);

      firstButton.addListener(new Button.ClickListener() {
         @Override
         public void buttonClick(ClickEvent event) {
            OseePagingComponent.this.setCurrentPage(0);
            fireEvent(new PageSelectedEvent(OseePagingComponent.this));
         }
      });

      previousButton.addListener(new Button.ClickListener() {
         @Override
         public void buttonClick(ClickEvent event) {
            OseePagingComponent.this.setCurrentPage(currentPage - 1);
            fireEvent(new PageSelectedEvent(OseePagingComponent.this));
         }
      });

      nextButton.addListener(new Button.ClickListener() {
         @Override
         public void buttonClick(ClickEvent event) {
            OseePagingComponent.this.setCurrentPage(currentPage + 1);
            fireEvent(new PageSelectedEvent(OseePagingComponent.this));
         }
      });

      lastButton.addListener(new Button.ClickListener() {
         @Override
         public void buttonClick(ClickEvent event) {
            OseePagingComponent.this.setCurrentPage(manyPages - 1);
            fireEvent(new PageSelectedEvent(OseePagingComponent.this));
         }
      });

      addComponent(firstButton);
      addComponent(spacer1);
      addComponent(previousButton);
      addComponent(spacer2);
      addComponent(hLayout_PageNumbers);
      addComponent(spacer3);
      addComponent(nextButton);
      addComponent(spacer4);
      addComponent(lastButton);
   }

   private void updateLayout() {
      if (getApplication() == null) {
         return;
      }
      synchronized (getApplication()) {
         if (allItemsPerPage) {
            firstButton.setEnabled(false);
            previousButton.setEnabled(false);
            nextButton.setEnabled(false);
            lastButton.setEnabled(false);
         } else {
            if (manyPages <= 0) {
               firstButton.setEnabled(false);
               previousButton.setEnabled(false);
               nextButton.setEnabled(false);
               lastButton.setEnabled(false);
            }

            if (currentPage <= 0) {
               firstButton.setEnabled(false);
               previousButton.setEnabled(false);
            } else {
               firstButton.setEnabled(true);
               previousButton.setEnabled(true);
            }

            if (currentPage >= manyPages - 1) {
               nextButton.setEnabled(false);
               lastButton.setEnabled(false);
            } else {
               nextButton.setEnabled(true);
               lastButton.setEnabled(true);
            }
         }

         //Update page numbers
         hLayout_PageNumbers.removeAllComponents();
         if (allItemsPerPage) {
            Label pageLabel = new Label(String.format("1"));
            pageLabel.setStyleName(CssConstants.OSEE_CURRENTPAGELABEL);

            Label spacer = new Label();
            spacer.setWidth(7, UNITS_PIXELS);

            hLayout_PageNumbers.addComponent(pageLabel);
            hLayout_PageNumbers.addComponent(spacer);
         } else {
            int startPage = 0;
            int endPage = manyPages - 1;

            //If there are more pages than MAX_PAGE_NUMBERS_SHOWN, then we need to reduce
            //  the number of pages shown.
            //      if (manyPages > 0 && manyPages > currentPage && MAX_PAGE_NUMBERS_SHOWN > (manyPages - currentPage)) {
            int pageSetIndex = currentPage / MAX_PAGE_NUMBERS_SHOWN;
            startPage = pageSetIndex * MAX_PAGE_NUMBERS_SHOWN;
            endPage = startPage + MAX_PAGE_NUMBERS_SHOWN;
            //      }

            if (endPage >= manyPages) {
               endPage = manyPages - 1;
            }

            if (startPage != 0) {
               Label pageLabel = new Label("...");

               Label spacer = new Label();
               spacer.setWidth(7, UNITS_PIXELS);

               hLayout_PageNumbers.addComponent(pageLabel);
               hLayout_PageNumbers.addComponent(spacer);
            }

            for (int i = startPage; i <= endPage; i++) {
               if (i == currentPage) {
                  Label pageLabel = new Label(String.format("%d", i + 1));
                  pageLabel.setStyleName(CssConstants.OSEE_CURRENTPAGELABEL);

                  hLayout_PageNumbers.addComponent(pageLabel);
               } else {
                  Button pageButton = new Button(String.format("%d", i + 1));
                  pageButton.setStyleName("link");
                  final int index = i;//needs to be 'final' for use with listener below
                  pageButton.addListener(new Button.ClickListener() {
                     @Override
                     public void buttonClick(ClickEvent event) {
                        OseePagingComponent.this.setCurrentPage(index);
                        fireEvent(new PageSelectedEvent(OseePagingComponent.this));
                     }
                  });

                  hLayout_PageNumbers.addComponent(pageButton);
               }

               if (i <= endPage) {
                  Label spacer = new Label();
                  spacer.setWidth(7, UNITS_PIXELS);
                  hLayout_PageNumbers.addComponent(spacer);
               }
            }

            if (endPage != manyPages - 1) {
               Label pageLabel = new Label("...");

               Label spacer = new Label();
               spacer.setWidth(7, UNITS_PIXELS);

               hLayout_PageNumbers.addComponent(pageLabel);
               hLayout_PageNumbers.addComponent(spacer);
            }
         }
      }
   }

   public interface PageSelectedListener extends Serializable {
      public void pageSelected(PageSelectedEvent source);
   }

   public class PageSelectedEvent extends Component.Event {

      public PageSelectedEvent(Component source) {
         super(source);
      }
   }

   private static Method PAGE_SELECTED_METHOD;

   static {
      try {
         PAGE_SELECTED_METHOD = PageSelectedListener.class.getDeclaredMethod("pageSelected", PageSelectedEvent.class);
      } catch (final java.lang.NoSuchMethodException e) {
         // This should never happen
         throw new java.lang.RuntimeException("Internal error finding methods in PageSelectedListener");
      }
   }

   public void addListener(PageSelectedListener listener) {
      addListener(PageSelectedEvent.class, listener, PAGE_SELECTED_METHOD);
   }

   public void removeListener(PageSelectedListener listener) {
      removeListener(PageSelectedEvent.class, listener, PAGE_SELECTED_METHOD);
   }

   @Override
   public void gotoFirstPage() {
      this.setCurrentPage(0);
      updateLayout();
   }

   @Override
   public void gotoPrevPage() {
      this.setCurrentPage(currentPage - 1);
      updateLayout();
   }

   @Override
   public void gotoNextPage() {
      this.setCurrentPage(currentPage + 1);
      updateLayout();
   }

   @Override
   public void gotoLastPage() {
      this.setCurrentPage(manyPages - 1);
      updateLayout();
   }

   @Override
   public Collection<Integer> getCurrentVisibleItemIndices() {
      Collection<Integer> ret = new ArrayList<Integer>();
      if (allItemsPerPage) {
         for (int i = 0; i < manyItemsTotal; i++) {
            ret.add(new Integer(i));
         }
      } else if (currentPage <= manyPages) {
         for (int i = 0; i < manyItemsPerPage; i++) {
            int itemIndex = (currentPage * manyItemsPerPage) + i;
            if (itemIndex < manyItemsTotal) {
               ret.add(new Integer(itemIndex));
            }
         }
      }
      return ret;
   }

   @Override
   public void setManyItemsPerPage(int manyItemsPerPage) {
      this.manyItemsPerPage = manyItemsPerPage;
      this.allItemsPerPage = false;
      updateManyPages();
   }

   @Override
   public int getManyItemsPerPage() {
      return this.manyItemsPerPage;
   }

   @Override
   public void setAllItemsPerPage() {
      this.allItemsPerPage = true;
      updateManyPages();
   }
}
