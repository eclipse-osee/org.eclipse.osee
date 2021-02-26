import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { PlConfigBranchService } from '../../services/pl-config-branch-service.service';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import { testBranchApplicability } from '../../testing/mockBranchService';

import { ApplicabilityTableComponent } from './applicability-table.component';

describe('ApplicabilityTableComponent', () => {
  let component: ApplicabilityTableComponent;
  let fixture: ComponentFixture<ApplicabilityTableComponent>;

  beforeEach(async () => {
    const branchServiceSpy = jasmine.createSpyObj('PlConfigBranchService',['modifyConfiguration'])
    
    await TestBed.configureTestingModule({
      declarations: [ApplicabilityTableComponent],
      providers: [
        { provide: PlConfigBranchService, useValue: branchServiceSpy },
        {
          provide: PlConfigCurrentBranchService, useValue: {
          branchApplicability: of(testBranchApplicability)
        }}
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ApplicabilityTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
