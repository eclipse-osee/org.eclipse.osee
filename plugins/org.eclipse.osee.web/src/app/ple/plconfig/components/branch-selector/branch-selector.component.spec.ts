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
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { of } from 'rxjs';
import { PlConfigBranchService } from '../../services/pl-config-branch-service.service';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import { testBranchListing } from '../../testing/mockBranchService';

import { BranchSelectorComponent } from './branch-selector.component';

describe('BranchSelectorComponent', () => {
  let component: BranchSelectorComponent;
  let fixture: ComponentFixture<BranchSelectorComponent>;

  beforeEach(async () => {
    const branchService = jasmine.createSpyObj('PlConfigBranchService', ['getBranches']);
    const currentBranchService = jasmine.createSpyObj('PlConfigCurrentBranchService', [], ['viewBranchType','branchListing']);
    const getBranchSpy = branchService.getBranches.and.returnValue(of(testBranchListing));
    await TestBed.configureTestingModule({
      imports:[MatFormFieldModule,NoopAnimationsModule,MatSelectModule,MatProgressSpinnerModule, FormsModule],
      declarations: [BranchSelectorComponent],
      providers: [
        { provide: PlConfigBranchService, useValue: branchService },
        { provide: PlConfigCurrentBranchService, useValue: currentBranchService}
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BranchSelectorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
