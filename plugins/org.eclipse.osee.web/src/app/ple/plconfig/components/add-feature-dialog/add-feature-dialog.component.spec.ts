import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { PlConfigBranchService } from '../../services/pl-config-branch-service.service';
import { PlConfigTypesService } from '../../services/pl-config-types.service';
import { writeFeature } from '../../types/pl-config-features';

import { AddFeatureDialogComponent } from './add-feature-dialog.component';

describe('AddFeatureDialogComponent', () => {
  let component: AddFeatureDialogComponent;
  let fixture: ComponentFixture<AddFeatureDialogComponent>;

  beforeEach(async () => {
    const branchService = jasmine.createSpyObj('PlConfigBranchService', ['getBranchApplicability',]);
    const typesService = jasmine.createSpyObj('PlConfigTypesService', [], ['productApplicabilityTypes']);
    await TestBed.configureTestingModule({
      imports:[MatFormFieldModule,MatListModule,MatDialogModule,MatInputModule, MatSelectModule,FormsModule, NoopAnimationsModule, MatSlideToggleModule],
      declarations: [AddFeatureDialogComponent],
      providers: [
        { provide: PlConfigBranchService, useValue: branchService },
        { provide: PlConfigTypesService, useValue: typesService },
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
