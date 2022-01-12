/*********************************************************************
 * Copyright (c) 2021 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { CommonModule } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { SimpleChange } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatButtonHarness } from '@angular/material/button/testing';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatMenuModule } from '@angular/material/menu';
import { MatMenuHarness } from '@angular/material/menu/testing';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatTableHarness } from '@angular/material/table/testing';
import { MatTooltipModule } from '@angular/material/tooltip';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, convertToParamMap, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { OseeStringUtilsDirectivesModule } from 'src/app/osee-utils/osee-string-utils/osee-string-utils-directives/osee-string-utils-directives.module';
import { OseeStringUtilsPipesModule } from 'src/app/osee-utils/osee-string-utils/osee-string-utils-pipes/osee-string-utils-pipes.module';
import { ConvertMessageInterfaceTitlesToStringPipe } from '../../../shared/pipes/convert-message-interface-titles-to-string.pipe';
import { SharedMessagingModule } from '../../../shared/shared-messaging.module';
import { SubElementTableRowComponentMock } from '../../mocks/components/sub-element-table-row.component.mock';
import { elementsMock } from '../../mocks/ReturnObjects/element.mock';
import { CurrentStateService } from '../../services/current-state.service';
import { AddElementDialog } from '../../types/AddElementDialog';
import { EditElementFieldComponent } from './edit-element-field/edit-element-field.component';

import { SubElementTableComponent } from './sub-element-table.component';

describe('SubElementTableComponent', () => {
  let component: SubElementTableComponent;
  let fixture: ComponentFixture<SubElementTableComponent>;
  let loader: HarnessLoader;
  let expectedData = [
    {
      beginWord: 'BEGIN',
      endWord: "END",
      BeginByte: '0',
      EndByte: '32',
      Sequence: 'Sequence',
      ElementName: 'name1',
      Units: 'N/A',
      MinValue: '0',
      MaxValue: '1',
      AlterableAfterCreationValid: false,
      Description: "A description",
      EnumLiteralsDesc: "Description of enum literals",
      Notes: "Notes go here",
      DefaultValue: "0",
      isArray: false,
    },
    {
      beginWord: 'Hello',
      endWord: "World",
      BeginByte: '0',
      EndByte: '32',
      Sequence: 'Sequence',
      ElementName: 'name2',
      Units: 'N/A',
      MinValue: '0',
      MaxValue: '1',
      AlterableAfterCreationValid: false,
      Description: "A description",
      EnumLiteralsDesc: "Description of enum literals",
      Notes: "Notes go here",
      DefaultValue: "0",
      isArray: false,
    }
  ];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports:[CommonModule,MatIconModule,MatDialogModule,MatTableModule,MatTooltipModule,MatMenuModule,MatFormFieldModule,MatInputModule,FormsModule,NoopAnimationsModule, OseeStringUtilsDirectivesModule, OseeStringUtilsPipesModule, RouterTestingModule,SharedMessagingModule, HttpClientTestingModule],
      declarations: [SubElementTableComponent, ConvertMessageInterfaceTitlesToStringPipe, EditElementFieldComponent,SubElementTableRowComponentMock],
      providers: [{
        provide: ActivatedRoute, useValue: {
          paramMap: of(convertToParamMap({ branchId: "10",branchType:"working" }))
      }}]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SubElementTableComponent);
    component = fixture.componentInstance;
    component.editMode = true;
    component.data = expectedData;
    component.dataSource = new MatTableDataSource(expectedData);
    component.dataSource.filter="name1"
    component.filter="element: name1"
    fixture.detectChanges();
    loader = TestbedHarnessEnvironment.loader(fixture);
  });

  // beforeEach(function () {
  //   let window1 = spyOn(window, 'open').and.callFake((url,target,replace) => {
  //     return null;
  //   })
  // });

  it('should create',async () => {
    fixture.detectChanges();
    await fixture.whenStable();
    expect(component).toBeTruthy();
    expect(component.data === expectedData).toBeTruthy();
    expect(component.filter === 'element: name1').toBeTruthy();
    expect(component.dataSource.filter === 'name1').toBeTruthy();
  });
  it('should update filter on changes',async () => {
    fixture.detectChanges();
    await fixture.whenStable();
    component.filter = "element: name2";
    component.ngOnChanges({
      data: new SimpleChange(expectedData, expectedData, false),
      filter: new SimpleChange('element: name1', 'element: name2', false)
    });
    await fixture.whenStable();
    expect(component.dataSource.filter === 'name2').toBeTruthy();
    expect(component).toBeTruthy();
  });

  it('should open create element dialog', async () => {
    let dialogRefSpy = jasmine.createSpyObj({ afterClosed: of<AddElementDialog>({id:'2',name:'abcdef',type:{id:'123',name:'abcd'},element:{id:'3',name:'abcdef',description:'qwerty',notes:'uiop',interfaceElementIndexEnd:0,interfaceElementIndexStart:0,interfaceElementAlterable:true}}), close: null });
    let dialogSpy = spyOn(TestBed.inject(MatDialog), 'open').and.returnValue(dialogRefSpy);
    let cells = (await (await (await loader.getHarness(MatTableHarness)).getFooterRows())[0].getCells());
    let button = await (cells[cells.length - 1].getHarness(MatButtonHarness));
    expect(button).toBeDefined();
    await button.click();

  })
  describe("Menu Testing", () => {
    let mEvent:MouseEvent
    beforeEach(() => {
      mEvent = document.createEvent("MouseEvent");
    })

    it('should open the menu and open the enum dialog', async () => {
      component.openGeneralMenu(mEvent, elementsMock[0],'');
      await fixture.whenStable();
      let menu = await loader.getHarness(MatMenuHarness);
      let spy = spyOn(component, 'openEnumDialog').and.callThrough();
      let dialogRefSpy = jasmine.createSpyObj({ afterClosed: of('ok'), close: null });
      let dialogSpy = spyOn(TestBed.inject(MatDialog), 'open').and.returnValue(dialogRefSpy);
      //let serviceSpy = spyOn(TestBed.inject(CurrentStateService), 'partialUpdateElement').and.stub();
      await menu.clickItem({ text: "Open Enumeration Details(view only)" });
      expect(spy).toHaveBeenCalled();
      //expect(serviceSpy).toHaveBeenCalled();
    })

    // it('should open the menu and open diff sidenav', async () => {
    //   component.openGeneralMenu(mEvent, elementsMock[0],'field','header');
    //   await fixture.whenStable();
    //   let menu = await loader.getHarness(MatMenuHarness);
    //   let spy = spyOn(component, 'viewDiff').and.callThrough();
    //   await menu.clickItem({ text: "View Diff" });
    //   expect(spy).toHaveBeenCalled();
    //   //expect(serviceSpy).toHaveBeenCalled();
    // })

    it('should open the menu and dismiss a description', async () => {
      component.openGeneralMenu(mEvent, elementsMock[0],'');
      await fixture.whenStable();
      let menu = await loader.getHarness(MatMenuHarness);
      let spy = spyOn(component, 'openDescriptionDialog').and.callThrough();
      let dialogRefSpy = jasmine.createSpyObj({ afterClosed: of('ok'), close: null });
      let dialogSpy = spyOn(TestBed.inject(MatDialog), 'open').and.returnValue(dialogRefSpy);
      let serviceSpy = spyOn(TestBed.inject(CurrentStateService), 'partialUpdateElement').and.stub();
      await menu.clickItem({ text: new RegExp("Open Description") });
      expect(spy).toHaveBeenCalled();
      expect(serviceSpy).toHaveBeenCalled();
    })

    it('should open the menu and edit a description', async () => {
      component.openGeneralMenu(mEvent, elementsMock[0],'');
      await fixture.whenStable();
      let menu = await loader.getHarness(MatMenuHarness);
      let spy = spyOn(component, 'openDescriptionDialog').and.callThrough();
      let dialogRefSpy = jasmine.createSpyObj({ afterClosed: of({original:'abcdef',type:'description',return:'jkl'}), close: null });
      let dialogSpy = spyOn(TestBed.inject(MatDialog), 'open').and.returnValue(dialogRefSpy);
      let serviceSpy = spyOn(TestBed.inject(CurrentStateService), 'partialUpdateElement').and.stub();
      await menu.clickItem({ text: new RegExp("Open Description") });
      expect(spy).toHaveBeenCalled();
      expect(serviceSpy).toHaveBeenCalled();
    })

    it('should open the menu and dismiss a notes popup', async () => {
      component.openGeneralMenu(mEvent, elementsMock[0],'');
      await fixture.whenStable();
      let menu = await loader.getHarness(MatMenuHarness);
      let spy = spyOn(component, 'openNotesDialog').and.callThrough();
      let dialogRefSpy = jasmine.createSpyObj({ afterClosed: of('ok'), close: null });
      let dialogSpy = spyOn(TestBed.inject(MatDialog), 'open').and.returnValue(dialogRefSpy);
      let serviceSpy = spyOn(TestBed.inject(CurrentStateService), 'partialUpdateElement').and.stub();
      await menu.clickItem({ text: new RegExp("Open Notes") });
      expect(spy).toHaveBeenCalled();
      expect(serviceSpy).toHaveBeenCalled();
    })

    it('should open the menu and edit a notes popup', async () => {
      component.openGeneralMenu(mEvent, elementsMock[0],'');
      await fixture.whenStable();
      let menu = await loader.getHarness(MatMenuHarness);
      let spy = spyOn(component, 'openNotesDialog').and.callThrough();
      let dialogRefSpy = jasmine.createSpyObj({ afterClosed: of({original:'abcdef',type:'description',return:'jkl'}), close: null });
      let dialogSpy = spyOn(TestBed.inject(MatDialog), 'open').and.returnValue(dialogRefSpy);
      let serviceSpy = spyOn(TestBed.inject(CurrentStateService), 'partialUpdateElement').and.stub();
      await menu.clickItem({ text: new RegExp("Open Notes") });
      expect(spy).toHaveBeenCalled();
      expect(serviceSpy).toHaveBeenCalled();
    })

    it('should open the remove element dialog', async() => {
      component.openGeneralMenu(mEvent, elementsMock[0],'');
      await fixture.whenStable();
      let menu = await loader.getHarness(MatMenuHarness);
      let spy = spyOn(component, 'removeElement').and.callThrough();
      let dialogRefSpy = jasmine.createSpyObj({ afterClosed: of('ok'), close: null });
      let dialogSpy = spyOn(TestBed.inject(MatDialog), 'open').and.returnValue(dialogRefSpy);
      let serviceSpy = spyOn(TestBed.inject(CurrentStateService), 'removeElementFromStructure').and.stub();
      await menu.clickItem({ text: new RegExp("Remove element from structure") });
      expect(spy).toHaveBeenCalled();
      expect(serviceSpy).toHaveBeenCalled();
    })

    it('should open the delete element dialog', async() => {
      component.openGeneralMenu(mEvent, elementsMock[0],'');
      await fixture.whenStable();
      let menu = await loader.getHarness(MatMenuHarness);
      let spy = spyOn(component, 'deleteElement').and.callThrough();
      let dialogRefSpy = jasmine.createSpyObj({ afterClosed: of('ok'), close: null });
      let dialogSpy = spyOn(TestBed.inject(MatDialog), 'open').and.returnValue(dialogRefSpy);
      let serviceSpy = spyOn(TestBed.inject(CurrentStateService), 'deleteElement').and.stub();
      await menu.clickItem({ text: new RegExp("Delete element globally") });
      expect(spy).toHaveBeenCalled();
      expect(serviceSpy).toHaveBeenCalled();
    })

    afterEach(() => {
      component.generalMenuTrigger.closeMenu();
    })
  })
});
