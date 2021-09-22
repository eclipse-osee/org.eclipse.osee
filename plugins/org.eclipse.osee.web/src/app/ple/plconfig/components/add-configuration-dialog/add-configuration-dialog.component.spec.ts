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
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatSelectModule } from '@angular/material/select';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { PlConfigBranchService } from '../../services/pl-config-branch-service.service';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import { PlConfigTypesService } from '../../services/pl-config-types.service';

import { AddConfigurationDialogComponent } from './add-configuration-dialog.component';

describe('AddConfigurationDialogComponent', () => {
  let component: AddConfigurationDialogComponent;
  let fixture: ComponentFixture<AddConfigurationDialogComponent>;

  beforeEach(async () => {
    const branchService = jasmine.createSpyObj('PlConfigBranchService', ['getBranchApplicability',]);
    const currentBranchService = jasmine.createSpyObj('PlConfigCurrentBranchService', [], ['cfgGroups']);
    const typesService = jasmine.createSpyObj('PlConfigTypesService', [], ['productApplicabilityTypes']);
    await TestBed.configureTestingModule({
      imports:[MatFormFieldModule,MatListModule,MatDialogModule,MatInputModule, MatSelectModule, NoopAnimationsModule,FormsModule],
      declarations: [AddConfigurationDialogComponent],
      providers: [
        { provide: PlConfigBranchService, useValue: branchService },
        { provide: PlConfigCurrentBranchService, useValue: currentBranchService },
        { provide: PlConfigTypesService, useValue: typesService },
        { provide: MatDialogRef, useValue: {} },
        {
          provide: MAT_DIALOG_DATA, useValue: {
            currentBranch: "3182843164128526558",
            copyFrom: { id: '0', name: '' },
            title:''
        }}]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AddConfigurationDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
