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
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatStepperModule } from '@angular/material/stepper';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { AddSubMessageDialog } from '../../../types/AddSubMessageDialog';
import { subMessage } from '../../../types/sub-messages';

import { AddSubMessageDialogComponent } from './add-sub-message-dialog.component';

describe('AddSubMessageDialogComponent', () => {
  let component: AddSubMessageDialogComponent;
  let fixture: ComponentFixture<AddSubMessageDialogComponent>;
  let dialogRef = jasmine.createSpyObj('MatDialogRef', ['close']);
  let dialogData: AddSubMessageDialog = {
    id: '123456',
    name: 'message',
    subMessage: {
      name: '',
      description: '',
      interfaceSubMessageNumber:''
    }
  }
  let dummySubmessage: subMessage = {
    id: '10',
    name: '',
    description: '',
    interfaceSubMessageNumber:''
  }
  let httpTestingController: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports:[HttpClientTestingModule,NoopAnimationsModule,MatDialogModule,MatStepperModule,FormsModule,MatFormFieldModule,MatInputModule,MatButtonModule,MatSelectModule,],
      declarations: [AddSubMessageDialogComponent],
      providers:[{provide:MatDialogRef,useValue:dialogRef},{provide:MAT_DIALOG_DATA,useValue:dialogData}]
    })
      .compileComponents();
      httpTestingController = TestBed.inject(HttpTestingController);
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AddSubMessageDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should movetoStep', () => {
    let stepper = jasmine.createSpyObj('stepper', {}, { selectedIndex: 0 });
    spyOn(stepper, 'selectedIndex').and.callThrough();
    component.moveToStep(3, stepper);
    expect(stepper.selectedIndex).toEqual(0);
  });

  it('should create new  by setting id to -1', () => {
    component.createNew();
    expect(component.data.subMessage.id).toEqual('-1');
  });

  it('should store the id', () => {
    component.storeId(dummySubmessage);
    expect(component.storedId).toEqual('10');
  });

  it('should movetoStep 3', () => {
    let stepper = jasmine.createSpyObj('stepper', {}, { selectedIndex: 0 });
    spyOn(stepper, 'selectedIndex').and.callThrough();
    let spy = spyOn(component, 'moveToStep').and.stub();
    component.moveToReview(stepper);
    expect(spy).toHaveBeenCalled();
    expect(spy).toHaveBeenCalledWith(3, stepper);
  });
});
