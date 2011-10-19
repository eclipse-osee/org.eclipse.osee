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
   private int manyPages = 0;
   private int currentPage = 0;
   private final int MAX_PAGE_NUMBERS_SHOWN = 4;

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
   }

   @Override
   public void setManyItemsTotal(int manyItemsTotal) {
      this.manyItemsTotal = manyItemsTotal;
      updateManyPages();
      createLayout();
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
   }

   private void createPageNumberLayout() {
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
         this.addComponent(pageLabel);

         Label spacer = new Label();
         spacer.setWidth(7, UNITS_PIXELS);
         this.addComponent(spacer);
      }

      for (int i = startPage; i <= endPage; i++) {
         if (i == currentPage) {
            Label pageLabel = new Label(String.format("%d", i + 1));
            this.addComponent(pageLabel);
            pageLabel.setStyleName(CssConstants.OSEE_CURRENTPAGELABEL);
         } else {
            Button pageButton = new Button(String.format("%d", i + 1));
            pageButton.setStyleName("link");
            this.addComponent(pageButton);
            final int index = i;//needs to be 'final' for use with listener below
            pageButton.addListener(new Button.ClickListener() {
               @Override
               public void buttonClick(ClickEvent event) {
                  OseePagingComponent.this.setCurrentPage(index);
                  fireEvent(new PageSelectedEvent(OseePagingComponent.this));
               }
            });
         }

         if (i <= endPage) {
            Label spacer = new Label();
            spacer.setWidth(7, UNITS_PIXELS);
            this.addComponent(spacer);
         }
      }

      if (endPage != manyPages - 1) {
         Label pageLabel = new Label("...");
         this.addComponent(pageLabel);

         Label spacer = new Label();
         spacer.setWidth(7, UNITS_PIXELS);
         this.addComponent(spacer);
      }
   }

   private void createLayout() {
      this.removeAllComponents();
      this.setSizeUndefined();

      Button firstButton = new Button("<< First");
      this.addComponent(firstButton);

      Label spacer1 = new Label();
      spacer1.setWidth(15, UNITS_PIXELS);
      this.addComponent(spacer1);

      Button previousButton = new Button("< Prev");
      this.addComponent(previousButton);

      Label spacer2 = new Label();
      spacer2.setWidth(15, UNITS_PIXELS);
      this.addComponent(spacer2);

      createPageNumberLayout();

      Label spacer3 = new Label();
      spacer3.setWidth(15, UNITS_PIXELS);
      this.addComponent(spacer3);

      Button nextButton = new Button("Next >");
      this.addComponent(nextButton);

      Label spacer4 = new Label();
      spacer4.setWidth(15, UNITS_PIXELS);
      this.addComponent(spacer4);

      Button lastButton = new Button("Last >>");
      this.addComponent(lastButton);

      if (manyPages <= 0) {
         firstButton.setEnabled(false);
         previousButton.setEnabled(false);
         nextButton.setEnabled(false);
         lastButton.setEnabled(false);
      }

      if (currentPage <= 0) {
         firstButton.setEnabled(false);
         previousButton.setEnabled(false);
      }

      if (currentPage >= manyPages - 1) {
         nextButton.setEnabled(false);
         lastButton.setEnabled(false);
      }

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
      createLayout();
   }

   @Override
   public void gotoPrevPage() {
      this.setCurrentPage(currentPage - 1);
      createLayout();
   }

   @Override
   public void gotoNextPage() {
      this.setCurrentPage(currentPage + 1);
      createLayout();
   }

   @Override
   public void gotoLastPage() {
      this.setCurrentPage(manyPages - 1);
      createLayout();
   }

   @Override
   public Collection<Integer> getCurrentVisibleItemIndices() {
      Collection<Integer> ret = new ArrayList<Integer>();
      if (currentPage <= manyPages) {
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
      createLayout();
   }

   @Override
   public int getManyItemsPerPage() {
      return this.manyItemsPerPage;
   }
}
