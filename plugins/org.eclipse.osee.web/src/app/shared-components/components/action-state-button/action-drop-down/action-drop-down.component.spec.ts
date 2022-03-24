/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { ActivatedRoute, Router } from '@angular/router';
import { of } from 'rxjs';
import { UserDataAccountService } from 'src/app/userdata/services/user-data-account.service';
import { ActionService } from '../../../../ple-services/http/action.service';
import { PlConfigBranchService } from '../../../../ple/plconfig/services/pl-config-branch-service.service';
import { PlConfigCurrentBranchService } from '../../../../ple/plconfig/services/pl-config-current-branch.service';
import { actionServiceMock } from '../../../../ple-services/http/action.service.mock';
import { plCurrentBranchServiceMock } from '../../../../ple/plconfig/testing/mockPlCurrentBranchService';
import { userDataAccountServiceMock } from '../../../../ple/plconfig/testing/mockUserDataAccountService';
import { ActionDropDownComponent } from './action-drop-down.component';
import { ActionStateButtonService } from '../../../services/action-state-button.service';
import { actionStateButtonServiceMock } from '../../../services/action-state-button.service.mock';
import { MatButtonHarness } from '@angular/material/button/testing';
import { PLConfigCreateAction } from '../../../../ple/plconfig/types/pl-config-actions';
import { testDataUser } from '../../../../ple/plconfig/testing/mockTypes';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';


describe('ActionDropDownComponent', () => {
  let component: ActionDropDownComponent;
  let fixture: ComponentFixture<ActionDropDownComponent>;
  let loader: HarnessLoader;
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports:[MatButtonModule,MatIconModule,MatDialogModule,NoopAnimationsModule],
      declarations: [ActionDropDownComponent],
      providers: [
        { provide: ActionStateButtonService, useValue: actionStateButtonServiceMock},
        { provide: UserDataAccountService, useValue: userDataAccountServiceMock},
        { provide: ActionService, useValue: actionServiceMock},
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
  it('should have 3 buttons', async () => {
    expect(await (await loader.getAllHarnesses(MatButtonHarness)).length).toEqual(3)
  })

  it('should transition to review', async () => {
    const spy = spyOn(component, 'transitionToReview').and.callThrough();
    const btn = await loader.getHarness(MatButtonHarness.with({ text: new RegExp('Transition To Review') }))
    expect(btn).toBeDefined();
    await btn.click();
    expect(spy).toHaveBeenCalled();
  })

  it('should approve working branch', async () => {
    const spy = spyOn(component, 'approveBranch').and.callThrough();
    const btn = await loader.getHarness(MatButtonHarness.with({ text: new RegExp('Approve Working Branch') }))
    expect(btn).toBeDefined();
    await btn.click();
    expect(spy).toHaveBeenCalled();
  })

  it('should commit working branch', async () => {
    const spy = spyOn(component, 'commitBranch').and.callThrough();
    const btn = await loader.getHarness(MatButtonHarness.with({ text: new RegExp('Commit Working Branch') }))
    expect(btn).toBeDefined();
    await btn.click();
    expect(spy).toHaveBeenCalled();
  })

  describe('non-component tests', () => {
    it('should add an action', () => {
      let dialogRefSpy = jasmine.createSpyObj({ afterClosed: of(new PLConfigCreateAction(testDataUser)), close: null });
      let dialogSpy = spyOn(TestBed.inject(MatDialog), 'open').and.returnValue(dialogRefSpy);
      const spy = spyOn(component.doAddAction, 'subscribe').and.callThrough();
      component.addAction();
      expect(spy).toHaveBeenCalled();
    })
  })
});
