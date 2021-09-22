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
import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { MatFormFieldHarness } from '@angular/material/form-field/testing';
import { MatSelectHarness } from '@angular/material/select/testing';
import { BranchSelectorComponent } from './branch-selector.component';
import { CurrentBranchTypeService } from '../../services/current-branch-type.service';
import { of } from 'rxjs';
import { BranchListing } from '../../types/BranchListing';
import { MatFormFieldModule } from '@angular/material/form-field';
import { FormsModule } from '@angular/forms';
import { MatSelectModule } from '@angular/material/select';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { BranchTypeService } from '../../services/router/branch-type.service';
import { RoutingService } from '../../services/router/routing.service';
import { RouterTestingModule } from '@angular/router/testing';

describe('BranchSelectorComponent', () => {
  let component: BranchSelectorComponent;
  let fixture: ComponentFixture<BranchSelectorComponent>;
  let loader: HarnessLoader;
  let testData: BranchListing[] = [{
    id: '8',
    viewId:'8',
    idIntValue: 8,
    name: 'SAW PL',
    associatedArtifact: '8',
    baselineTx: '',
    parentTx: '',
    parentBranch: {
      id: '0',
      viewId:'0'
    },
    branchState: '0',
    branchType: '0',
    inheritAccessControl: false,
    archived: false,
    shortName:''
  }]
  let currentBranchSpy: jasmine.SpyObj<CurrentBranchTypeService> = jasmine.createSpyObj('CurrentBranchTypeService', {}, { branches: of(testData) });
  let routingSpy: jasmine.SpyObj<RoutingService> = jasmine.createSpyObj('RoutingService', [], {branchId:of('8')})
  let typeService: BranchTypeService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MatFormFieldModule, FormsModule, MatSelectModule, NoopAnimationsModule, RouterTestingModule.withRoutes(
        [
          { path: ':branchType/:branchId/typeSearch', component: BranchSelectorComponent },
          { path: ':branchType/typeSearch', component: BranchSelectorComponent },
          { path: 'typeSearch', component: BranchSelectorComponent },
        ]
      )],
      providers:[{provide: CurrentBranchTypeService, useValue:currentBranchSpy}],
      declarations: [ BranchSelectorComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BranchSelectorComponent);
    component = fixture.componentInstance;
    loader = TestbedHarnessEnvironment.loader(fixture);
    typeService=TestBed.inject(BranchTypeService)
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('Core Functionality', () => {
    describe('Selection', () => {
      describe('Valid States', () => {
        it('should select SAW PL branch', async () => {
          typeService.type = 'product line';
          (await (await loader.getHarness(MatFormFieldHarness)).getControl(MatSelectHarness))?.open();
          (await (await (await loader.getHarness(MatFormFieldHarness)).getControl(MatSelectHarness))?.clickOptions({ text: 'SAW PL' }));
          expect(component.selectedBranchId).toEqual('8');
        });
      })
      describe('Non-interactive States', () => {
        it('should be disabled without a branch type', async () => {
          expect(await(await(await loader.getHarness(MatFormFieldHarness)).getControl(MatSelectHarness))?.isDisabled()).toEqual(true)
        });
      })
    })
    describe('Page Load', () => {
      it('should have a pre-selected type', async () => {
        typeService.type = 'product line';
        component.selectedBranchId = "8";
        expect(await(await(await loader.getHarness(MatFormFieldHarness)).getControl(MatSelectHarness))?.getValueText()).toEqual('SAW PL')
      });  
    })
  })
});
