import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialog } from '@angular/material/dialog';
import { Router, ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { PlConfigActionService } from '../../services/pl-config-action.service';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import { testBranchAction, testWorkFlow } from '../../testing/mockActionService';
import { testBranchInfo } from '../../testing/mockBranchService';

import { ActionDropDownComponent } from './action-drop-down.component';

describe('ActionDropDownComponent', () => {
  let component: ActionDropDownComponent;
  let fixture: ComponentFixture<ActionDropDownComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ActionDropDownComponent],
      providers: [
        { provide: MatDialog, useValue: {} },
        {
          provide: PlConfigCurrentBranchService, useValue: {
            branchAction: of(testBranchAction),
            branchState: of(testBranchInfo),
            branchWorkFlow: of(testWorkFlow),
          }
        },
        {
          provide: PlConfigActionService, useValue: {
            getAction(){return of(testBranchAction)}
          }
        },
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
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
