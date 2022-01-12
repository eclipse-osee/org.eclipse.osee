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
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { of } from 'rxjs';
import { userDataAccountServiceMock } from 'src/app/ple/plconfig/testing/mockUserDataAccountService';
import { UserDataAccountService } from 'src/app/userdata/services/user-data-account.service';
import { MimPreferencesServiceMock } from '../../../mocks/MimPreferencesService.mock';
import { MimPreferencesService } from '../../../services/http/mim-preferences.service';

import { EditViewFreeTextFieldDialogComponent } from './edit-view-free-text-field-dialog.component';

describe('EditViewFreeTextFieldDialogComponent', () => {
  let component: EditViewFreeTextFieldDialogComponent;
  let fixture: ComponentFixture<EditViewFreeTextFieldDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports:[FormsModule, MatIconModule,MatInputModule,MatDialogModule,MatFormFieldModule,NoopAnimationsModule],
      declarations: [EditViewFreeTextFieldDialogComponent],
      providers:[{
        provide: MatDialogRef, useValue: {
          close() { return of(); }
        }
      },
        { provide: MAT_DIALOG_DATA, useValue: { original: 'abcdef', type: 'Description', return: 'abcdef' } },
        { provide: UserDataAccountService, useValue: userDataAccountServiceMock },
        {provide:MimPreferencesService,useValue:MimPreferencesServiceMock}]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EditViewFreeTextFieldDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
