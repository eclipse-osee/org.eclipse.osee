import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatMenuModule } from '@angular/material/menu';
import { of } from 'rxjs';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import { PlConfigUIStateService } from '../../services/pl-config-uistate.service';
import { testBranchApplicability, testCfgGroups } from '../../testing/mockBranchService';

import { ConfigurationGroupDropdownComponent } from './configuration-group-dropdown.component';

describe('ConfigurationGroupDropdownComponent', () => {
  let component: ConfigurationGroupDropdownComponent;
  let fixture: ComponentFixture<ConfigurationGroupDropdownComponent>;

  beforeEach(async () => {
    const currentBranchService = jasmine.createSpyObj('PlConfigCurrentBranchService', [], ['cfgGroups', 'branchApplicability']);
    const uiService = jasmine.createSpyObj('PlConfigUIStateService', [], ['updateReqConfig']);
    await TestBed.configureTestingModule({
      imports:[MatMenuModule,MatButtonModule],
      declarations: [ConfigurationGroupDropdownComponent],
      providers: [
        { provide: MatDialog, useValue: {} },
        {
          provide: PlConfigCurrentBranchService, useValue: {
            branchApplicability: of(testBranchApplicability),
            cfgGroups: of(testCfgGroups)
          }
        },
        { provide: PlConfigUIStateService, useValue: uiService}
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfigurationGroupDropdownComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
