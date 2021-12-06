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
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatFormFieldHarness } from '@angular/material/form-field/testing';
import { MatInputModule } from '@angular/material/input';
import { MatInputHarness } from '@angular/material/input/testing';
import { MatListModule } from '@angular/material/list';
import { MatMenuModule } from '@angular/material/menu';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { DialogService } from '../../services/dialog.service';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import { DialogServiceMock } from '../../testing/mockDialogService.mock';
import { plCurrentBranchServiceMock } from '../../testing/mockPlCurrentBranchService';

import { ApplicabilityTableComponent } from './applicability-table.component';

describe('ApplicabilityTableComponent', () => {
  let component: ApplicabilityTableComponent;
  let fixture: ComponentFixture<ApplicabilityTableComponent>;
  let loader: HarnessLoader;

  beforeEach(async () => {
    
    await TestBed.configureTestingModule({
      imports:[MatFormFieldModule,MatListModule,MatDialogModule,MatInputModule,FormsModule, MatSelectModule,MatMenuModule, NoopAnimationsModule,MatTableModule, MatPaginatorModule,MatTooltipModule, MatPaginatorModule,RouterTestingModule],
      declarations: [ApplicabilityTableComponent],
      providers: [
        { provide: DialogService, useValue: DialogServiceMock },
        { provide: MatDialog, useValue: {} },
        { provide: PlConfigCurrentBranchService, useValue: plCurrentBranchServiceMock }
      ]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ApplicabilityTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    loader = TestbedHarnessEnvironment.loader(fixture);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should filter the table', async () => {
    let spy = spyOn(component, 'applyFilter').and.callThrough();
    let input = await (await loader.getHarness(MatFormFieldHarness.with({ floatingLabelText: 'Filter Configuration Information' }))).getControl(MatInputHarness);
    expect(input).toBeDefined();
    await input?.focus();
    await input?.setValue('abcdef');
    await input?.blur();
    expect(spy).toHaveBeenCalled();
  })
});
