import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialog } from '@angular/material/dialog';
import { MatMenuModule } from '@angular/material/menu';
import { of } from 'rxjs';
import { PlConfigBranchService } from '../../services/pl-config-branch-service.service';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import { testBranchApplicability } from '../../testing/mockBranchService';
import { PlConfigApplicUIBranchMapping } from '../../types/pl-config-applicui-branch-mapping';
import { response } from '../../types/pl-config-responses';

import { ConfigurationDropdownComponent } from './configuration-dropdown.component';

describe('ConfigurationDropdownComponent', () => {
  let component: ConfigurationDropdownComponent;
  let fixture: ComponentFixture<ConfigurationDropdownComponent>;

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
    const branchService = jasmine.createSpyObj('PlConfigBranchService', ['deleteConfiguration', 'copyConfiguration', 'addConfiguration']);
    var addConfigurationSpy = branchService.addConfiguration.and.returnValue(of(testResponse));
    var copyConfigurationSpy = branchService.copyConfiguration.and.returnValue(of(testResponse));
    var delteConfigurationSpy = branchService.deleteConfiguration.and.returnValue(of(testResponse));
    await TestBed.configureTestingModule({
      imports:[MatMenuModule],
      declarations: [ConfigurationDropdownComponent],
      providers: [
        { provide: MatDialog, useValue: {} },
        {
          provide: PlConfigCurrentBranchService, useValue: {
            branchApplicability: of(testBranchApplicability),
          }
        },
        { provide: PlConfigBranchService, useValue: branchService },
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfigurationDropdownComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
