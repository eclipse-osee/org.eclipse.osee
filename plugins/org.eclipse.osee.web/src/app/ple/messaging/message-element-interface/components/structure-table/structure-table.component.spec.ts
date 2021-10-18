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
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatButtonHarness } from '@angular/material/button/testing';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatFormFieldHarness } from '@angular/material/form-field/testing';
import { MatInputModule } from '@angular/material/input';
import { MatInputHarness } from '@angular/material/input/testing';
import { MatMenuModule } from '@angular/material/menu';
import { MatMenuHarness } from '@angular/material/menu/testing';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSelectModule } from '@angular/material/select';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { switchMap, takeUntil } from 'rxjs/operators';
import { OseeStringUtilsDirectivesModule } from 'src/app/osee-utils/osee-string-utils/osee-string-utils-directives/osee-string-utils-directives.module';
import { OseeStringUtilsPipesModule } from 'src/app/osee-utils/osee-string-utils/osee-string-utils-pipes/osee-string-utils-pipes.module';
import { editAuthServiceMock } from '../../../connection-view/mocks/EditAuthService.mock';
import { ConvertMessageInterfaceTitlesToStringPipe } from '../../../shared/pipes/convert-message-interface-titles-to-string.pipe';
import { EditAuthService } from '../../../shared/services/edit-auth-service.service';
import { SharedMessagingModule } from '../../../shared/shared-messaging.module';
import { EditStructureFieldComponentMock } from '../../mocks/components/EditStructureField.mock';
import { SubElementTableComponentMock } from '../../mocks/components/sub-element-table.mock';
import { structuresMock } from '../../mocks/ReturnObjects/Structures.mock';
import { CurrentStateServiceMock } from '../../mocks/services/CurrentStateService.mock';
import { CurrentStateService } from '../../services/current-state.service';
import { structure } from '../../types/structure';
import { EditElementFieldComponent } from '../sub-element-table/edit-element-field/edit-element-field.component';

import { StructureTableComponent } from './structure-table.component';

describe('StructureTableComponent', () => {
  let component: StructureTableComponent;
  let fixture: ComponentFixture<StructureTableComponent>;
  let loader: HarnessLoader;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports:[MatFormFieldModule,
        MatDialogModule,
        MatInputModule,
        MatSelectModule,
        MatMenuModule,
        MatProgressBarModule,
        FormsModule,
        NoopAnimationsModule,
        MatTableModule,
        MatTooltipModule,
        OseeStringUtilsPipesModule,
        OseeStringUtilsDirectivesModule,
        RouterTestingModule,
        SharedMessagingModule
      ],
      declarations: [ StructureTableComponent,SubElementTableComponentMock,
        ConvertMessageInterfaceTitlesToStringPipe,
        EditElementFieldComponent,
        EditStructureFieldComponentMock],
      providers: [
        { provide: EditAuthService,useValue:editAuthServiceMock },
        {
          provide: ActivatedRoute,
          useValue: {
            paramMap: of(
              convertToParamMap({
                name: 'Name > Name',
              })
            ),
          },
        },
        {
          provide: CurrentStateService, useValue: CurrentStateServiceMock
        },
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StructureTableComponent);
    component = fixture.componentInstance;
    component.messageData = CurrentStateServiceMock.structures!.pipe(
      switchMap((data) => of(new MatTableDataSource<structure>(data))),
    );
    component.hasFilter = true;
    fixture.detectChanges();
    loader = TestbedHarnessEnvironment.loader(fixture);
  });
  beforeEach(function () {
    let window1 = spyOn(window, 'open').and.callFake((url,target,replace) => {
      return null;
    })
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('should remove element from expandedElement', () => {
    component.expandedElement = [1, 2, 3, 4, 5, 7];
    component.hideRow(4);
    expect(!component.expandedElement.includes(4)).toBeTruthy();
  });

  it('should add element from expandedElement', () => {
    component.expandedElement = [1, 2, 3, 4, 5, 7];
    component.expandRow(9);
    expect(component.expandedElement.includes(9)).toBeTruthy();
  });

  it('should find a truncatedElement', () => {
    component.truncatedSections = ['hello', 'world'];
    let result = component.isTruncated('world');
    expect(result).toBeTruthy();
  });

  it('should not find a truncatedElement', () => {
    component.truncatedSections = ['hello', 'world'];
    let result = component.isTruncated('abcdef');
    expect(result).toBeFalsy();
  });

  it('should filter text', async () => {
    const form = await loader.getHarness(MatFormFieldHarness);
    const control = await form.getControl(MatInputHarness);
    await control?.setValue('Some text');
    expect(
      fixture.componentInstance.filter ===
        'Some text'.trim().toLowerCase()
    ).toBeTruthy();
  });

  it('should expand row', () => {
    component.rowChange({
      id: '1',
      name: 'name2',
      description: 'description2',
      notes: 'notes',
      interfaceElementIndexEnd: 1,
      interfaceElementIndexStart: 0,
      interfaceElementAlterable: true,
      platformTypeName: 'boolean',
      platformTypeId: 9
    }, true);
    expect(component.expandedElement).toEqual([{
      id: '1',
      name: 'name2',
      description: 'description2',
      notes: 'notes',
      interfaceElementIndexEnd: 1,
      interfaceElementIndexStart: 0,
      interfaceElementAlterable: true,
      platformTypeName: 'boolean',
      platformTypeId: 9
    }]);
  });

  it('should open and close a sub table', async () => {
    const spy = spyOn(component, 'expandRow').and.callThrough();
    const button = await loader.getHarness(MatButtonHarness.with({ text: 'V' }));
    await button.click();
    expect(spy).toHaveBeenCalled();
    await button.click();
    expect(spy).toHaveBeenCalled();
  });

  it('should open settings dialog', async () => {
    let dialogRefSpy = jasmine.createSpyObj({ afterClosed: of({branchId:'10',allowedHeaders1:[],allowedHeaders2:[],allHeaders1:[],allHeaders2:[],editable:true,headers1Label:'',headers2Label:'',headersTableActive:false}), close: null });
    let dialogSpy = spyOn(TestBed.inject(MatDialog),'open').and.returnValue(dialogRefSpy)
    const spy = spyOn(component, 'openSettingsDialog').and.callThrough();
    const button = await loader.getHarness(MatButtonHarness.with({ text: 'Settings' }));
    await button.click();
    expect(spy).toHaveBeenCalled();
  })

  it('should open add structure dialog', async () => {
    let dialogRefSpy = jasmine.createSpyObj({ afterClosed: of({id:'10',name:'New Structure',structure:{id:'10216532',name:'New Structure',elements:[],description:'',interfaceMaxSimultaneity:'',interfaceMinSimultaneity:'',interfaceTaskFileType:0,interfaceStructureCategory:'',numElements:'10',sizeInBytes:'10',bytesPerSecondMinimum:10,bytesPerSecondMaximum:10}}), close: null });
    let dialogSpy = spyOn(TestBed.inject(MatDialog),'open').and.returnValue(dialogRefSpy)
    const spy = spyOn(component, 'openAddStructureDialog').and.callThrough();
    const button = await loader.getHarness(MatButtonHarness.with({ text: '+' }));
    await button.click();
    expect(spy).toHaveBeenCalled();
  })

  describe("Menu Testing", () => {
    let mEvent:MouseEvent
    beforeEach(() => {
      mEvent = document.createEvent("MouseEvent");
    })
    it('should open the menu and navigate to a new tab', async() => {
      component.openMenu(mEvent, structuresMock[0].id,structuresMock[0].name,structuresMock[0].description);
      await fixture.whenStable();
      let menu = await loader.getHarness(MatMenuHarness);
      let spy = spyOn(component, 'navigateToInNewTab').and.callThrough();
      await menu.clickItem({ text: "Open structure table in new tab" });
      expect(spy).toHaveBeenCalled();
    })
    it('should open the menu and dismiss a description', async () => {
      component.openMenu(mEvent, structuresMock[0].id,structuresMock[0].name,structuresMock[0].description);
      await fixture.whenStable();
      let menu = await loader.getHarness(MatMenuHarness);
      let spy = spyOn(component, 'openDescriptionDialog').and.callThrough();
      let dialogRefSpy = jasmine.createSpyObj({ afterClosed: of('ok'), close: null });
      let dialogSpy = spyOn(TestBed.inject(MatDialog), 'open').and.returnValue(dialogRefSpy);
      let serviceSpy = spyOn(TestBed.inject(CurrentStateService), 'partialUpdateStructure').and.stub();
      await menu.clickItem({ text: "Open Description" });
      expect(spy).toHaveBeenCalled();
      expect(serviceSpy).toHaveBeenCalled();
    })

    it('should open the menu and edit a description', async () => {
      component.openMenu(mEvent, structuresMock[0].id,structuresMock[0].name,structuresMock[0].description);
      await fixture.whenStable();
      let menu = await loader.getHarness(MatMenuHarness);
      let spy = spyOn(component, 'openDescriptionDialog').and.callThrough();
      let dialogRefSpy = jasmine.createSpyObj({ afterClosed: of({original:'abcdef',type:'description',return:'jkl'}), close: null });
      let dialogSpy = spyOn(TestBed.inject(MatDialog), 'open').and.returnValue(dialogRefSpy);
      let serviceSpy = spyOn(TestBed.inject(CurrentStateService), 'partialUpdateStructure').and.stub();
      await menu.clickItem({ text: "Open Description" });
      expect(spy).toHaveBeenCalled();
      expect(serviceSpy).toHaveBeenCalled();
    })

    it('should open the remove structure dialog', async() => {
      component.openMenu(mEvent, structuresMock[0].id,structuresMock[0].name,structuresMock[0].description);
      await fixture.whenStable();
      let menu = await loader.getHarness(MatMenuHarness);
      let spy = spyOn(component, 'removeStructureDialog').and.callThrough();
      let dialogRefSpy = jasmine.createSpyObj({ afterClosed: of('ok'), close: null });
      let dialogSpy = spyOn(TestBed.inject(MatDialog), 'open').and.returnValue(dialogRefSpy);
      let serviceSpy = spyOn(TestBed.inject(CurrentStateService), 'removeStructureFromSubmessage').and.stub();
      await menu.clickItem({ text: "Remove structure from submessage" });
      expect(spy).toHaveBeenCalled();
      expect(serviceSpy).toHaveBeenCalled();
    })
    it('should open the delete structure dialog', async() => {
      component.openMenu(mEvent, structuresMock[0].id,structuresMock[0].name,structuresMock[0].description);
      await fixture.whenStable();
      let menu = await loader.getHarness(MatMenuHarness);
      let spy = spyOn(component, 'deleteStructureDialog').and.callThrough();
      let dialogRefSpy = jasmine.createSpyObj({ afterClosed: of('ok'), close: null });
      let dialogSpy = spyOn(TestBed.inject(MatDialog), 'open').and.returnValue(dialogRefSpy);
      let serviceSpy = spyOn(TestBed.inject(CurrentStateService), 'deleteStructure').and.stub();
      await menu.clickItem({ text: "Delete structure globally" });
      expect(spy).toHaveBeenCalled();
      expect(serviceSpy).toHaveBeenCalled();
    })
    afterEach(() => {
      component.matMenuTrigger.closeMenu();
    })
  })
});
