import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { MatFormFieldHarness } from '@angular/material/form-field/testing';
import { MatInputHarness } from '@angular/material/input/testing';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { Router, ActivatedRoute, convertToParamMap } from '@angular/router';
import { BehaviorSubject, of } from 'rxjs';
import { OseeStringUtilsDirectivesModule } from 'src/app/osee-utils/osee-string-utils/osee-string-utils-directives/osee-string-utils-directives.module';
import { OseeStringUtilsPipesModule } from 'src/app/osee-utils/osee-string-utils/osee-string-utils-pipes/osee-string-utils-pipes.module';
import { SubElementTableComponent } from './components/sub-element-table/sub-element-table.component';

import { MessageElementInterfaceComponent } from './message-element-interface.component';
import { ConvertMessageInterfaceTitlesToStringPipe } from '../shared/pipes/convert-message-interface-titles-to-string.pipe';
import { MatDialogModule } from '@angular/material/dialog';
import { CurrentStateService } from './services/current-state.service';
import { SharedMessagingModule } from '../shared/shared-messaging.module';
import { EditElementFieldComponent } from './components/sub-element-table/edit-element-field/edit-element-field.component';
import { EditStructureFieldComponentMock } from './mocks/components/EditStructureField.mock';

let loader: HarnessLoader;

describe('MessageElementInterfaceComponent', () => {
  let component: MessageElementInterfaceComponent;
  let fixture: ComponentFixture<MessageElementInterfaceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        MatFormFieldModule,
        MatDialogModule,
        MatInputModule,
        MatSelectModule,
        FormsModule,
        NoopAnimationsModule,
        MatTableModule,
        MatTooltipModule,
        OseeStringUtilsPipesModule,
        OseeStringUtilsDirectivesModule,
        SharedMessagingModule
      ],
      declarations: [
        MessageElementInterfaceComponent,
        SubElementTableComponent,
        ConvertMessageInterfaceTitlesToStringPipe,
        EditElementFieldComponent,
        EditStructureFieldComponentMock
      ],
      providers: [
        { provide: Router, useValue: { navigate: () => {} } },
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
          provide: CurrentStateService, useValue: {
            structures: of([{
              id: '0',
              name: 'name',
              elements: [{
                id: '1',
                name: 'name2',
                description: 'description2',
                notes: 'notes',
                interfaceElementIndexEnd: 1,
                interfaceElementIndexStart: 0,
                interfaceElementAlterable: true,
                platformTypeName: 'boolean',
                platformTypeId:9
              }],
              description: 'description',
              interfaceMaxSimultaneity: '0',
              interfaceMinSimultaneity: '1',
              interfaceTaskFileType: 1,
              interfaceStructureCategory: 'Category 1'
            }]),
            BranchId:new BehaviorSubject("10")
          }
        },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    localStorage.setItem(
      '10',
      JSON.stringify({
        mim: {
          editMode: true,
          StructureHeaders: ["1"],
          ElementHeaders: ["2"],
        }
      }
      )
    );
    fixture = TestBed.createComponent(MessageElementInterfaceComponent);
    component = fixture.componentInstance;
    loader = TestbedHarnessEnvironment.loader(fixture);
    fixture.detectChanges();
  });

  beforeEach(function () {
    var store:any = {10:'{mim:{editMode:true}}'};
  
    spyOn(localStorage, 'getItem').and.callFake(function (key) {
      return store[key];
    });
    spyOn(localStorage, 'setItem').and.callFake(function (key, value) {
      return store[key] = value + '';
    });
    spyOn(localStorage, 'clear').and.callFake(function () {
        store = {};
    });
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should remove element from expandedElement', () => {
    //@todo: Refactor once types come in
    component.expandedElement = [1, 2, 3, 4, 5, 7];
    component.hideRow(4);
    expect(!component.expandedElement.includes(4)).toBeTruthy();
  });

  it('should add element from expandedElement', () => {
    //@todo: Refactor once types come in
    component.expandedElement = [1, 2, 3, 4, 5, 7];
    component.expandRow(9);
    expect(component.expandedElement.includes(9)).toBeTruthy();
  });

  it('should find a truncatedElement', () => {
    //@todo: Refactor once types come in
    component.truncatedSections = ['hello', 'world'];
    let result = component.isTruncated('world');
    expect(result).toBeTruthy();
  });

  it('should not find a truncatedElement', () => {
    //@todo: Refactor once types come in
    component.truncatedSections = ['hello', 'world'];
    let result = component.isTruncated('abcdef');
    expect(result).toBeFalsy();
  });

  it('should filter text', async () => {
    //@todo: Refactor once types come in
    const form = await loader.getHarness(MatFormFieldHarness);
    const control = await form.getControl(MatInputHarness);
    await control?.setValue('CCS Audio File');
    expect(
      fixture.componentInstance.filter ===
        'CCS Audio File'.trim().toLowerCase()
    ).toBeTruthy();
  });

  // it('should filter sub-element text',async () => {
  //   //@todo: Refactor once types come in
  //throws error?
  //   const form = await loader.getHarness(MatFormFieldHarness);
  //   const control = await form.getControl(MatInputHarness);
  //   await control?.setValue("element: Audio File ID");
  //   expect(component.filter==="element: Audio File ID".trim().toLowerCase()).toBeTruthy();
  // });
});
