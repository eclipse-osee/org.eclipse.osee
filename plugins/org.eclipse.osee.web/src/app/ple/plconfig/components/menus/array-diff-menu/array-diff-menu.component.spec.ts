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
import { MatDialog } from '@angular/material/dialog';
import { MatMenuModule } from '@angular/material/menu';
import { MatMenuItemHarness } from '@angular/material/menu/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { DialogService } from '../../../services/dialog.service';
import { PlConfigCurrentBranchService } from '../../../services/pl-config-current-branch.service';
import { DialogServiceMock } from '../../../testing/mockDialogService.mock';
import { plCurrentBranchServiceMock } from '../../../testing/mockPlCurrentBranchService';

import { ArrayDiffMenuComponent } from './array-diff-menu.component';

describe('ArrayDiffMenuComponent', () => {
  let component: ArrayDiffMenuComponent;
  let fixture: ComponentFixture<ArrayDiffMenuComponent>;
  let loader: HarnessLoader;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports:[MatMenuModule,RouterTestingModule.withRoutes([{
        path: '',
        component: ArrayDiffMenuComponent,
        children: [
          {
            path: ':branchType',
            children: [
              {
                path: ':branchId',
                children: [
                  {
                    path: 'diff',
                    component:ArrayDiffMenuComponent
                  }
                ]
              },
            ]
          }
        ]
      },
      {
        path: 'diffOpen', component: ArrayDiffMenuComponent, outlet:'rightSideNav'
      }])],
      declarations: [ArrayDiffMenuComponent],
      providers: [
        { provide: DialogService, useValue: DialogServiceMock },
        { provide: PlConfigCurrentBranchService, useValue: plCurrentBranchServiceMock }
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ArrayDiffMenuComponent);
    component = fixture.componentInstance;
    component.array=[{previousValue:'123',currentValue:'123',transactionToken:{id:'12',branchId:'12345'}}]
    fixture.detectChanges();
    loader = TestbedHarnessEnvironment.loader(fixture);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should open a diff sidenav', async () => {
    const spy = spyOn(component, 'viewDiff').and.callThrough();
    const button = await loader.getHarness(MatMenuItemHarness.with({ text: '123' }));
    expect(button).toBeDefined();
    await button.click();
    expect(spy).toHaveBeenCalled();
  })
});
