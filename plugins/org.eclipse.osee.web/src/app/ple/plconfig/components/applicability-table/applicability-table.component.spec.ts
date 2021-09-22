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
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { of } from 'rxjs';
import { PlConfigBranchService } from '../../services/pl-config-branch-service.service';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import { testBranchApplicability } from '../../testing/mockBranchService';

import { ApplicabilityTableComponent } from './applicability-table.component';

describe('ApplicabilityTableComponent', () => {
  let component: ApplicabilityTableComponent;
  let fixture: ComponentFixture<ApplicabilityTableComponent>;

  beforeEach(async () => {
    const branchServiceSpy = jasmine.createSpyObj('PlConfigBranchService',['modifyConfiguration'])
    
    await TestBed.configureTestingModule({
      imports:[MatFormFieldModule,MatListModule,MatDialogModule,MatInputModule,FormsModule, MatSelectModule, NoopAnimationsModule,MatTableModule, MatPaginatorModule,MatTooltipModule, MatPaginatorModule],
      declarations: [ApplicabilityTableComponent],
      providers: [
        { provide: MatDialog, useValue: {} },
        { provide: PlConfigBranchService, useValue: branchServiceSpy },
        {
          provide: PlConfigCurrentBranchService, useValue: {
          branchApplicability: of(testBranchApplicability)
        }}
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ApplicabilityTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
