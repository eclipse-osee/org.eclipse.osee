import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { PlConfigBranchService } from '../../services/pl-config-branch-service.service';
import { writeFeature } from '../../types/pl-config-features';

import { AddFeatureDialogComponent } from './add-feature-dialog.component';

describe('AddFeatureDialogComponent', () => {
  let component: AddFeatureDialogComponent;
  let fixture: ComponentFixture<AddFeatureDialogComponent>;

  beforeEach(async () => {
    const branchService = jasmine.createSpyObj('PlConfigBranchService', ['getBranchApplicability',]);
    await TestBed.configureTestingModule({
      declarations: [AddFeatureDialogComponent],
      providers:[{
        provide: PlConfigBranchService, useValue: branchService
      },
        { provide: MatDialogRef, useValue: {} },
        {
          provide: MAT_DIALOG_DATA, useValue: {
            currentBranch: "3182843164128526558",
            feature:new writeFeature()
        }}]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AddFeatureDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
