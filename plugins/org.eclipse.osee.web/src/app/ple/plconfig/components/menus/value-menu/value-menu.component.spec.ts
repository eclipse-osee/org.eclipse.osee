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
import { MatMenuModule } from '@angular/material/menu';
import { MatMenuItemHarness } from '@angular/material/menu/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { DialogService } from '../../../services/dialog.service';
import { PlConfigCurrentBranchService } from '../../../services/pl-config-current-branch.service';
import { DialogServiceMock } from '../../../testing/mockDialogService.mock';
import { plCurrentBranchServiceMock } from '../../../testing/mockPlCurrentBranchService';

import { ValueMenuComponent } from './value-menu.component';

describe('ValueMenuComponent', () => {
  let component: ValueMenuComponent;
  let fixture: ComponentFixture<ValueMenuComponent>;
  let loader: HarnessLoader;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports:[MatMenuModule,NoopAnimationsModule,RouterTestingModule.withRoutes([{
        path: '',
        component: ValueMenuComponent,
        children: [
          {
            path: ':branchType',
            children: [
              {
                path: ':branchId',
                children: [
                  {
                    path: 'diff',
                    component:ValueMenuComponent
                  }
                ]
              },
            ]
          }
        ]
      },
      {
        path: 'diffOpen', component: ValueMenuComponent, outlet:'rightSideNav'
      }])],
      declarations: [ValueMenuComponent],
      providers: [
        { provide: DialogService, useValue: DialogServiceMock },
        { provide: PlConfigCurrentBranchService, useValue: plCurrentBranchServiceMock }
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ValueMenuComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    component.value={id:'',name:'abcd',value:'',values:[],changes:{value:{previousValue:'',currentValue:'abcd',transactionToken:{id:'10343213',branchId:'8'}}}}
    loader = TestbedHarnessEnvironment.loader(fixture);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('should open a diff sidenav', async () => {
    const spy = spyOn(component, 'viewDiff').and.callThrough();
    const menu = await loader.getHarness(MatMenuItemHarness.with({ text: 'View Diff for abcd' }));
    expect(menu).toBeDefined();
    await menu.focus();
    await menu.click();
    expect(spy).toHaveBeenCalled();
  })
});
