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
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { SimpleChange } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatButtonModule } from '@angular/material/button';
import { MatButtonHarness } from '@angular/material/button/testing';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatMenuHarness } from '@angular/material/menu/testing';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatTableHarness } from '@angular/material/table/testing';
import { MatTooltipModule } from '@angular/material/tooltip';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { Router, ActivatedRoute, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { TestScheduler } from 'rxjs/testing';
import { HighlightFilteredTextDirective } from 'src/app/osee-utils/osee-string-utils/osee-string-utils-directives/highlight-filtered-text.directive';
import { OseeStringUtilsDirectivesModule } from 'src/app/osee-utils/osee-string-utils/osee-string-utils-directives/osee-string-utils-directives.module';
import { OseeStringUtilsPipesModule } from 'src/app/osee-utils/osee-string-utils/osee-string-utils-pipes/osee-string-utils-pipes.module';
import { MimSingleDiffDummy } from 'src/app/ple/diff-views/mocks/mim-single-diff.mock';
import { messagesMock } from '../../mocks/ReturnObjects/messages.mock';
import { subMessagesMock } from '../../mocks/ReturnObjects/submessages.mock';
import { CurrentMessageServiceMock } from '../../mocks/services/CurrentMessageService.mock';
import { ConvertMessageTableTitlesToStringPipe } from '../../pipes/convert-message-table-titles-to-string.pipe';
import { ConvertSubMessageTitlesToStringPipe } from '../../pipes/convert-sub-message-titles-to-string.pipe';
import { CurrentMessagesService } from '../../services/current-messages.service';
import { AddSubMessageDialog } from '../../types/AddSubMessageDialog';
import { AddSubMessageDialogComponent } from './add-sub-message-dialog/add-sub-message-dialog.component';
import { EditSubMessageFieldComponent } from './edit-sub-message-field/edit-sub-message-field.component';

import { SubMessageTableComponent } from './sub-message-table.component';

describe('SubMessageTableComponent', () => {
  let component: SubMessageTableComponent;
  let fixture: ComponentFixture<SubMessageTableComponent>;
  let loader: HarnessLoader;
  let scheduler: TestScheduler;
  let expectedData = [
    {
        name: "Name",
        description: "description adslkfj;asjfadkljf;lajdfla;jsdfdlkasjf;lkajslfjad;ljfkladjsf;",
        interfaceSubMessageNumber: "0",
        interfaceMessageRate:"1Hz"
    },
    {
        name: "Name2",
        description: "description2",
        interfaceSubMessageNumber: "1",
        interfaceMessageRate:"1Hz"
    }
]

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MatTableModule,MatIconModule, MatTooltipModule, MatButtonModule, OseeStringUtilsDirectivesModule, OseeStringUtilsPipesModule, RouterTestingModule.withRoutes([{path:'',component:SubMessageTableComponent},{ path: 'diffOpen', component: MimSingleDiffDummy,outlet:'rightSideNav' }]), MatMenuModule, MatDialogModule, HttpClientTestingModule,NoopAnimationsModule],
      declarations: [SubMessageTableComponent, ConvertMessageTableTitlesToStringPipe, ConvertSubMessageTitlesToStringPipe, EditSubMessageFieldComponent, AddSubMessageDialogComponent,HighlightFilteredTextDirective],
      providers: [{provide: CurrentMessagesService, useValue:CurrentMessageServiceMock},
        // { provide: Router, useValue: router },
        // {
        //   provide: ActivatedRoute, useValue: route  
        // },
    ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SubMessageTableComponent);
    component = fixture.componentInstance;
    component.element={id:'5',name:'blah',description:'abcdef',interfaceMessageNumber:'1234',interfaceMessagePeriodicity:'Aperiodic',interfaceMessageRate:'5Hz',interfaceMessageType:'Connection',interfaceMessageWriteAccess:true,subMessages:[]}
    component.dataSource = new MatTableDataSource();
    component.data = expectedData;
    component.editMode = true;
    fixture.detectChanges();
    loader = TestbedHarnessEnvironment.loader(fixture);
  });

  beforeEach(() => scheduler = new TestScheduler((actual, expected) => {
    expect(actual).toEqual(expected);
  }));

  // beforeEach(function () {
  //   let window1 = spyOn(window, 'open').and.callFake((url,target,replace) => {
  //     return null;
  //   })
  // });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // it('should navigate to Hello/10/10/elements', () => {
  //   component.navigateToElementsTable("10","10",'Hello');
  //   expect(router.navigate).toHaveBeenCalledWith(['10','10','Hello','elements'],{relativeTo: Object({parent:'' }), queryParamsHandling:'merge'});
  // });

  it('should update the datasource filter', () => {
    scheduler.run(({ expectObservable }) => {
      component.filter = "sub message: Name2";
      component.ngOnChanges({
        data: new SimpleChange(component.data, component.data, false),
        filter: new SimpleChange('', component.filter, false)
      })
      const expectedMarble = { a: component.element }
      const expectedObservable=''
      expectObservable(component.expandRow).toBe(expectedObservable, expectedMarble);
    })
    // component.filter = "sub message: Name2";
    // component.ngOnChanges({
    //   data: new SimpleChange(component.data, component.data, false),
    //   filter: new SimpleChange('', component.filter, false)
    // })
    // expect(component.dataSource.filter).toEqual(component.filter.replace('sub message: ', ''));
  });

  it('should relate a new submessage', async () => {
    let spy = spyOn(component, 'createNewSubMessage').and.callThrough();
    let dialogRefSpy = jasmine.createSpyObj({ afterClosed: of<AddSubMessageDialog>({id:'2',name:'blah',subMessage:{id:'5',name:'abcdef',description:'qwerty',interfaceSubMessageNumber:'12345'}}), close: null });
    let dialogSpy = spyOn(TestBed.inject(MatDialog), 'open').and.returnValue(dialogRefSpy);
    let serviceSpy = spyOn(TestBed.inject(CurrentMessagesService), 'relateSubMessage').and.stub();
    let button = (await (await (await (await loader.getHarness(MatTableHarness)).getFooterRows())[0].getCells())[0].getHarness(MatButtonHarness.with({ selector:'.add-button' })));
    expect(button).toBeDefined();
    await(await (await (await (await loader.getHarness(MatTableHarness)).getFooterRows())[0].getCells())[0].getHarness(MatButtonHarness.with({selector:'.add-button'}))).click();
    expect(spy).toHaveBeenCalled();
    expect(serviceSpy).toHaveBeenCalled();
  })

  it('should create a new submessage', async () => {
    let spy = spyOn(component, 'createNewSubMessage').and.callThrough();
    let dialogRefSpy = jasmine.createSpyObj({ afterClosed: of<AddSubMessageDialog>({id:'2',name:'blah',subMessage:{name:'abcdef',description:'qwerty',interfaceSubMessageNumber:'12345'}}), close: null });
    let dialogSpy = spyOn(TestBed.inject(MatDialog), 'open').and.returnValue(dialogRefSpy);
    let serviceSpy = spyOn(TestBed.inject(CurrentMessagesService), 'relateSubMessage').and.stub();
    let button = (await (await (await (await loader.getHarness(MatTableHarness)).getFooterRows())[0].getCells())[0].getHarness(MatButtonHarness.with({ selector:'.add-button' })));
    expect(button).toBeDefined();
    await(await (await (await (await loader.getHarness(MatTableHarness)).getFooterRows())[0].getCells())[0].getHarness(MatButtonHarness.with({selector:'.add-button'}))).click();
    expect(spy).toHaveBeenCalled();
    expect(serviceSpy).toHaveBeenCalled();
  })

  describe('Menu Tests', () => {
    let mEvent:MouseEvent
    beforeEach(() => {
      mEvent = document.createEvent("MouseEvent");
    })
    // it('should open the menu and open sub message details', async () => {
    //   component.openMenu(mEvent, messagesMock[0], subMessagesMock[0], 'string','','');
    //   await fixture.whenStable();
    //   let menu = await loader.getHarness(MatMenuHarness);
    //   let spy = spyOn(component, 'navigateToElementsTable').and.callThrough();
    //   await menu.clickItem({ text: "Open submessage details" });
    //   expect(spy).toHaveBeenCalled();
    // })

    // it('should open the menu and open sub message details in new tab', async () => {
    //   component.openMenu(mEvent, messagesMock[0], subMessagesMock[0], 'string','','');
    //   await fixture.whenStable();
    //   let menu = await loader.getHarness(MatMenuHarness);
    //   let spy = spyOn(component, 'navigateToElementsTableInNewTab').and.callThrough();
    //   await menu.clickItem({ text: "Open submessage details in new tab" });
    //   expect(spy).toHaveBeenCalled();
    // })

    it('should open the menu and open the view diff sidenav', async () => {
      component.openMenu(mEvent, messagesMock[0], subMessagesMock[0], 'string','field','name');
      await fixture.whenStable();
      let menu = await loader.getHarness(MatMenuHarness);
      let spy = spyOn(component, 'viewDiff').and.callThrough();
      await menu.clickItem({ text: new RegExp("View Diff") });
      expect(spy).toHaveBeenCalled();
    })

    it('should open the menu and dismiss a description', async () => {
      component.openMenu(mEvent, messagesMock[0], subMessagesMock[0], 'string','',' ');
      await fixture.whenStable();
      let menu = await loader.getHarness(MatMenuHarness);
      let spy = spyOn(component, 'openDescriptionDialog').and.callThrough();
      let dialogRefSpy = jasmine.createSpyObj({ afterClosed: of('ok'), close: null });
      let dialogSpy = spyOn(TestBed.inject(MatDialog), 'open').and.returnValue(dialogRefSpy);
      let serviceSpy = spyOn(TestBed.inject(CurrentMessagesService), 'partialUpdateSubMessage').and.stub();
      await menu.clickItem({ text: new RegExp("Open Description") });
      expect(spy).toHaveBeenCalled();
      expect(serviceSpy).toHaveBeenCalled();
    })

    it('should open the menu and edit a description', async () => {
      component.openMenu(mEvent, messagesMock[0], subMessagesMock[0], 'string','',' ');
      await fixture.whenStable();
      let menu = await loader.getHarness(MatMenuHarness);
      let spy = spyOn(component, 'openDescriptionDialog').and.callThrough();
      let dialogRefSpy = jasmine.createSpyObj({ afterClosed: of({original:'abcdef',type:'description',return:'jkl'}), close: null });
      let dialogSpy = spyOn(TestBed.inject(MatDialog), 'open').and.returnValue(dialogRefSpy);
      let serviceSpy = spyOn(TestBed.inject(CurrentMessagesService), 'partialUpdateSubMessage').and.stub();
      await menu.clickItem({ text: new RegExp("Open Description") });
      expect(spy).toHaveBeenCalled();
      expect(serviceSpy).toHaveBeenCalled();
    })

    it('should open the menu and remove a sub message', async () => {
      component.openMenu(mEvent, messagesMock[0], subMessagesMock[0], 'string','',' ');
      await fixture.whenStable();
      let menu = await loader.getHarness(MatMenuHarness);
      let spy = spyOn(component, 'removeSubMessage').and.callThrough();
      let dialogRefSpy = jasmine.createSpyObj({ afterClosed: of('ok'), close: null });
      let dialogSpy = spyOn(TestBed.inject(MatDialog), 'open').and.returnValue(dialogRefSpy);
      let serviceSpy = spyOn(TestBed.inject(CurrentMessagesService), 'removeSubMessage').and.stub();
      await menu.clickItem({ text: new RegExp("Remove submessage from message") });
      expect(spy).toHaveBeenCalled();
      expect(serviceSpy).toHaveBeenCalled();
    })

    it('should open the menu and not remove a sub message', async () => {
      component.openMenu(mEvent, messagesMock[0], subMessagesMock[0], 'string','',' ');
      await fixture.whenStable();
      let menu = await loader.getHarness(MatMenuHarness);
      let spy = spyOn(component, 'removeSubMessage').and.callThrough();
      let dialogRefSpy = jasmine.createSpyObj({ afterClosed: of(), close: null });
      let dialogSpy = spyOn(TestBed.inject(MatDialog), 'open').and.returnValue(dialogRefSpy);
      let serviceSpy = spyOn(TestBed.inject(CurrentMessagesService), 'removeSubMessage').and.stub();
      await menu.clickItem({ text: new RegExp("Remove submessage from message") });
      expect(spy).toHaveBeenCalled();
      expect(serviceSpy).not.toHaveBeenCalled();
    })

    it('should open the menu and delete a sub message', async () => {
      component.openMenu(mEvent, messagesMock[0], subMessagesMock[0], 'string','',' ');
      await fixture.whenStable();
      let menu = await loader.getHarness(MatMenuHarness);
      let spy = spyOn(component, 'deleteSubMessage').and.callThrough();
      let dialogRefSpy = jasmine.createSpyObj({ afterClosed: of('ok'), close: null });
      let dialogSpy = spyOn(TestBed.inject(MatDialog), 'open').and.returnValue(dialogRefSpy);
      let serviceSpy = spyOn(TestBed.inject(CurrentMessagesService), 'deleteSubMessage').and.stub();
      await menu.clickItem({ text: new RegExp("Delete submessage globally") });
      expect(spy).toHaveBeenCalled();
      expect(serviceSpy).toHaveBeenCalled();
    })

    it('should open the menu and not delete a sub message', async () => {
      component.openMenu(mEvent, messagesMock[0], subMessagesMock[0], 'string','',' ');
      await fixture.whenStable();
      let menu = await loader.getHarness(MatMenuHarness);
      let spy = spyOn(component, 'deleteSubMessage').and.callThrough();
      let dialogRefSpy = jasmine.createSpyObj({ afterClosed: of(), close: null });
      let dialogSpy = spyOn(TestBed.inject(MatDialog), 'open').and.returnValue(dialogRefSpy);
      let serviceSpy = spyOn(TestBed.inject(CurrentMessagesService), 'deleteSubMessage').and.stub();
      await menu.clickItem({ text: new RegExp("Delete submessage globally") });
      expect(spy).toHaveBeenCalled();
      expect(serviceSpy).not.toHaveBeenCalled();
    })

    afterEach(() => {
      component.matMenuTrigger.closeMenu();
    })
  })
});
