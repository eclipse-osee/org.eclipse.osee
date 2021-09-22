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
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { of } from 'rxjs';
import { UserDataAccountService } from 'src/app/userdata/services/user-data-account.service';
import { PlConfigActionService } from '../../services/pl-config-action.service';
import { PlConfigBranchService } from '../../services/pl-config-branch-service.service';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import { plActionServiceMock } from '../../testing/mockActionService';
import { plCurrentBranchServiceMock } from '../../testing/mockPlCurrentBranchService';
import { userDataAccountServiceMock } from '../../testing/mockUserDataAccountService';
import { ActionDropDownComponent } from './action-drop-down.component';


describe('ActionDropDownComponent', () => {
  let component: ActionDropDownComponent;
  let fixture: ComponentFixture<ActionDropDownComponent>;
  let loader: HarnessLoader;
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports:[MatButtonModule],
      declarations: [ActionDropDownComponent],
      providers: [
        { provide: MatDialog, useValue: {} },
        { provide: UserDataAccountService, useValue: userDataAccountServiceMock},
        { provide: PlConfigActionService, useValue: plActionServiceMock},
        { provide: PlConfigCurrentBranchService, useValue: plCurrentBranchServiceMock},
        { provide: Router, useValue: { navigate: () => { } } },
        {
          provide: ActivatedRoute, useValue: {
            paramMap: of(
              {
                branchId: '10',
                branchType: 'all'
              }
            )
          }
        },
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ActionDropDownComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    loader = TestbedHarnessEnvironment.loader(fixture);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
