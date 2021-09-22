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
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { of } from 'rxjs';
import { PlConfigBranchService } from '../../services/pl-config-branch-service.service';
import { PlConfigTypesService } from '../../services/pl-config-types.service';
import { testBranchApplicability } from '../../testing/mockBranchService';
import { PlConfigApplicUIBranchMapping } from '../../types/pl-config-applicui-branch-mapping';

import { EditFeatureDialogComponent } from './edit-feature-dialog.component';

describe('EditFeatureDialogComponent', () => {
  let component: EditFeatureDialogComponent;
  let fixture: ComponentFixture<EditFeatureDialogComponent>;

  beforeEach(async () => {
    const typesService = jasmine.createSpyObj('PlConfigTypesService', [], ['productApplicabilityTypes']);
    await TestBed.configureTestingModule({
      imports:[MatFormFieldModule,MatInputModule,MatButtonModule,MatListModule,MatSelectModule,MatDialogModule,NoopAnimationsModule, MatSlideToggleModule, FormsModule],
      declarations: [EditFeatureDialogComponent],
      providers: [
        { provide: PlConfigTypesService, useValue: typesService },
        { provide: MatDialogRef, useValue: {} },
        {
          provide: MAT_DIALOG_DATA, useValue: {
            currentBranch: "01238082141",
            editable:true,
            feature: {
              id: '',
              idIntValue: 6451325,
              idString: '6451325',
              type: null ,
              name: 'feature1',
              description: 'lorem ipsum',
              valueType: 'string',
              valueStr: '',
              defaultValue: 'hello',
              productAppStr: 'OFP',
              values: ['hello','world'],
              productApplicabilities: ['OFP'],
              multiValued: false,
              setValueStr(): void {
        this.valueStr=this.values.toString();
              },
            setProductAppStr(): void {
        this.productAppStr = this.productApplicabilities.toString();
              } 
            }
        } },
        {
          provide: PlConfigBranchService, useValue: {
          getBranchApplicability(){return of(testBranchApplicability)}
        }}]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EditFeatureDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
