import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { MatMenuModule } from '@angular/material/menu';
import { of } from 'rxjs';
import { PlConfigBranchService } from '../../services/pl-config-branch-service.service';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import { testBranchApplicability } from '../../testing/mockBranchService';
import { PlConfigApplicUIBranchMapping } from '../../types/pl-config-applicui-branch-mapping';
import { response } from '../../types/pl-config-responses';

import { FeatureDropdownComponent } from './feature-dropdown.component';

describe('FeatureDropdownComponent', () => {
  let component: FeatureDropdownComponent;
  let fixture: ComponentFixture<FeatureDropdownComponent>;

  beforeEach(async () => {
    const testResponse:response = {
      empty: false,
      errorCount: 0,
      errors: false,
      failed: false,
      ids: [],
      infoCount: 0,
      numErrors: 0,
      numErrorsViaSearch: 0,
      numWarnings: 0,
      numWarningsViaSearch: 0,
      results: [],
      success: true,
      tables: [],
      title: "",
      txId: "2",
      warningCount:0,
    }
    const branchService = jasmine.createSpyObj('PlConfigBranchService', ['deleteFeature', 'modifyFeature', 'addFeature']);
    const addFeatureSpy = branchService.addFeature.and.returnValue(of(testResponse));
    const deleteFeatureSpy = branchService.deleteFeature.and.returnValue(of(testResponse));
    const modifyFeatureSpy = branchService.modifyFeature.and.returnValue(of(testResponse));
    await TestBed.configureTestingModule({
      imports:[MatMenuModule,MatButtonModule],
      declarations: [FeatureDropdownComponent],
      providers: [
        {
          provide: PlConfigCurrentBranchService, useValue: {
            branchApplicability: of(testBranchApplicability),
        } },
        { provide: PlConfigBranchService, useValue: branchService },
        { provide: MatDialog, useValue: {}}
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FeatureDropdownComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
