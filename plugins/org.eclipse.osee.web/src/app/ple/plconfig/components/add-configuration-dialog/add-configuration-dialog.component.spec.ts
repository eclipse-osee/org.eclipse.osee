import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { PlConfigBranchService } from '../../services/pl-config-branch-service.service';

import { AddConfigurationDialogComponent } from './add-configuration-dialog.component';

describe('AddConfigurationDialogComponent', () => {
  let component: AddConfigurationDialogComponent;
  let fixture: ComponentFixture<AddConfigurationDialogComponent>;

  beforeEach(async () => {
    const branchService = jasmine.createSpyObj('PlConfigBranchService', ['getBranchApplicability',]);    
    await TestBed.configureTestingModule({
      declarations: [AddConfigurationDialogComponent],
      providers:[{
        provide: PlConfigBranchService, useValue: branchService
      },
        { provide: MatDialogRef, useValue: {} },
        {
          provide: MAT_DIALOG_DATA, useValue: {
            currentBranch: "3182843164128526558",
            copyFrom: { id: '0', name: '' },
            title:''
        }}]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AddConfigurationDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
