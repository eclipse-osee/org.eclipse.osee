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
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatInputHarness } from '@angular/material/input/testing';
import { MatSelectModule } from '@angular/material/select';
import { MatSelectHarness } from '@angular/material/select/testing';
import { MatTableModule } from '@angular/material/table';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { of } from 'rxjs';
import { currentTypesServiceMock } from '../../mocks/services/current.types.service.mock';
import { CurrentTypesService } from '../../services/current-types.service';

import { EditEnumSetDialogComponent } from './edit-enum-set-dialog.component';

describe('EditEnumSetDialogComponent', () => {
  let component: EditEnumSetDialogComponent;
  let fixture: ComponentFixture<EditEnumSetDialogComponent>;
  let loader: HarnessLoader;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EditEnumSetDialogComponent],
      imports: [MatDialogModule,MatSelectModule,MatInputModule,MatFormFieldModule,FormsModule,MatTableModule,NoopAnimationsModule],
      providers: [{
        provide: MatDialogRef, useValue: {
          close() { return of(); }
        }
      },
        { provide: MAT_DIALOG_DATA, useValue: of('1234567890') },
        {provide: CurrentTypesService,useValue:currentTypesServiceMock}
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EditEnumSetDialogComponent);
    component = fixture.componentInstance;
    loader = TestbedHarnessEnvironment.loader(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  /**
   * @todo Tests for this dialog will be performed later, I believe there is a bug that got addressed in a later angular version that is preventing MatInputHarness.setValue() from triggering (change)
   */
  it('should select an applicability', async() => {
    const select = await loader.getHarness(MatSelectHarness);
    await select.open()
    const option = await select.getOptions({ text: 'Second' });
    await option?.[0].click();
    expect(component.enumSet.applicability).toEqual({id:'2',name:'Second'})
  })

  it('should close the dialog', async () => {
    const dialogRefClosure = spyOn(component.dialogRef, 'close').and.stub();
    const button = await loader.getHarness(MatButtonHarness.with({ text: 'Cancel' }));
    await button.click();
    expect(dialogRefClosure).toHaveBeenCalled();
  })
});
