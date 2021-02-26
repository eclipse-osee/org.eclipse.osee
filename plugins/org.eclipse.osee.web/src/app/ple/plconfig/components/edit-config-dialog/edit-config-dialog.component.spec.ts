import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { of } from 'rxjs';
import { PlConfigBranchService } from '../../services/pl-config-branch-service.service';
import { testBranchApplicability } from '../../testing/mockBranchService';
import { PlConfigApplicUIBranchMapping } from '../../types/pl-config-applicui-branch-mapping';

import { EditConfigurationDialogComponent } from './edit-config-dialog.component';

describe('EditDialogComponent', () => {
  let component: EditConfigurationDialogComponent;
  let fixture: ComponentFixture<EditConfigurationDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EditConfigurationDialogComponent],
      providers: [
        { provide: MatDialogRef, useValue: {} },
        {
          provide: MAT_DIALOG_DATA, useValue: {
            currentBranch: "3182843164128526558",
            currentConfig: {
              id:"200045",
              name: "Product A",
              ConfigurationToCopyFrom: {
                id: "",
                name:""
              }
            }
          }
        },
        {
          provide: PlConfigBranchService, useValue: {
            getBranchApplicability() { return of(testBranchApplicability) }
          }
        },
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EditConfigurationDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
