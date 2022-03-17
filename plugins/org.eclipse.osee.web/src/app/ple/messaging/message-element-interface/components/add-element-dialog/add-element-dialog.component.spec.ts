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
import { DebugElement } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatButtonHarness } from '@angular/material/button/testing';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatFormFieldHarness } from '@angular/material/form-field/testing';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatInputHarness } from '@angular/material/input/testing';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatStepperModule } from '@angular/material/stepper';
import { By } from '@angular/platform-browser';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { TransactionBuilderService } from '../../../../../transactions/transaction-builder.service';
import { transactionBuilderMock } from '../../../../../transactions/transaction-builder.service.mock';
import { UserDataAccountService } from '../../../../../userdata/services/user-data-account.service';
import { userDataAccountServiceMock } from '../../../../plconfig/testing/mockUserDataAccountService';
import { NewTypeDialogComponent } from '../../../shared/components/dialogs/new-type-dialog/new-type-dialog.component';
import { applicabilityListServiceMock } from '../../../shared/mocks/ApplicabilityListService.mock';
import { enumerationSetServiceMock } from '../../../shared/mocks/enumeration.set.service.mock';
import { enumsServiceMock } from '../../../shared/mocks/EnumsService.mock';
import { MimPreferencesServiceMock } from '../../../shared/mocks/MimPreferencesService.mock';
import { MockNewTypeDialog } from '../../../shared/mocks/NewTypeDialog.mock';
import { typesServiceMock } from '../../../shared/mocks/types.service.mock';
import { ApplicabilityListService } from '../../../shared/services/http/applicability-list.service';
import { EnumerationSetService } from '../../../shared/services/http/enumeration-set.service';
import { EnumsService } from '../../../shared/services/http/enums.service';
import { MimPreferencesService } from '../../../shared/services/http/mim-preferences.service';
import { TypesService } from '../../../shared/services/http/types.service';
import { AddElementDialog } from '../../types/AddElementDialog';

import { AddElementDialogComponent } from './add-element-dialog.component';

describe('AddElementDialogComponent', () => {
  let component: AddElementDialogComponent;
  let fixture: ComponentFixture<AddElementDialogComponent>;
  let dialogData: AddElementDialog = {
    id: '12345',
    name: 'structure',
    type: { id: '', name: '' },
    element: {
      id: '-1',
      name: '',
      description: '',
      notes: '',
      interfaceElementAlterable: true,
      interfaceElementIndexEnd: 0,
      interfaceElementIndexStart: 0,
      units:''
    }
  }
  let loader: HarnessLoader;
  let nestedDialog: DebugElement;
  
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports:[HttpClientTestingModule,MatStepperModule,MatDialogModule,MatButtonModule,FormsModule,MatFormFieldModule,MatSelectModule,MatInputModule,MatSlideToggleModule,NoopAnimationsModule,MatIconModule],
      declarations: [AddElementDialogComponent,MockNewTypeDialog],
      providers: [{
        provide: MatDialogRef, useValue: {
        
        }
      },
        {
          provide: MAT_DIALOG_DATA, useValue: dialogData
        },
        { provide: TransactionBuilderService, useValue: transactionBuilderMock },
        { provide: MimPreferencesService, useValue: MimPreferencesServiceMock },
        { provide: UserDataAccountService, useValue: userDataAccountServiceMock },
        { provide: TypesService, useValue: typesServiceMock },
        { provide: EnumsService, useValue: enumsServiceMock },
        { provide: EnumerationSetService, useValue: enumerationSetServiceMock },
        { provide: ApplicabilityListService, useValue: applicabilityListServiceMock}]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AddElementDialogComponent);
    component = fixture.componentInstance;
    loader = TestbedHarnessEnvironment.loader(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('Page 2', () => {
    beforeEach(async() => {
      const createNewBtn = await loader.getHarness(MatButtonHarness.with({ text: 'Create new Element' }))
      createNewBtn.click();
    })
    it('should fill out the form', async () => {
      const spy = spyOn(component, 'receivePlatformTypeData').and.callThrough();
      const addTypeBtn = await loader.getHarness(MatButtonHarness.with({text:new RegExp('add')}))
      await addTypeBtn.click();
      nestedDialog = fixture.debugElement.query(By.directive(MockNewTypeDialog))
      fixture.detectChanges();
      nestedDialog.componentInstance.closeDialog();
      expect(spy).toHaveBeenCalled();
    })
  })
});
