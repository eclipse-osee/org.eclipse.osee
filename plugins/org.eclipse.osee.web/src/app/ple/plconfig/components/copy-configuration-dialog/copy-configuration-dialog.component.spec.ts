import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { PlConfigBranchService } from '../../services/pl-config-branch-service.service';

import { CopyConfigurationDialogComponent } from './copy-configuration-dialog.component';

describe('CopyConfigurationDialogComponent', () => {
  let component: CopyConfigurationDialogComponent;
  let fixture: ComponentFixture<CopyConfigurationDialogComponent>;

  beforeEach(async () => {
    const branchService = jasmine.createSpyObj('PlConfigBranchService', ['getBranchApplicability']);
    await TestBed.configureTestingModule({
      imports:[MatSelectModule,MatFormFieldModule,NoopAnimationsModule,MatDialogModule,MatButtonModule],
      declarations: [CopyConfigurationDialogComponent],
      providers: [
        { provide: PlConfigBranchService, useValue: branchService },
        { provide: MatDialogRef, useValue: {} },
        {
          provide: MAT_DIALOG_DATA, useValue: {
            currentConfig:
            {
              id: '',
              name: ''
            },
            currentBranch:'10'
          }
        }
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CopyConfigurationDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
