import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatMenuModule } from '@angular/material/menu';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatRadioModule } from '@angular/material/radio';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, convertToParamMap, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { ActionDropDownComponent } from './components/action-drop-down/action-drop-down.component';
import { ApplicabilityTableComponent } from './components/applicability-table/applicability-table.component';
import { BranchSelectorComponent } from './components/branch-selector/branch-selector.component';
import { BranchTypeSelectorComponent } from './components/branch-type-selector/branch-type-selector.component';
import { ConfigurationDropdownComponent } from './components/configuration-dropdown/configuration-dropdown.component';
import { ConfigurationGroupDropdownComponent } from './components/configuration-group-dropdown/configuration-group-dropdown.component';
import { FeatureDropdownComponent } from './components/feature-dropdown/feature-dropdown.component';

import { PlconfigComponent } from './plconfig.component';

describe('PlconfigComponent', () => {
  let component: PlconfigComponent;
  let fixture: ComponentFixture<PlconfigComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
        MatDialogModule,
        MatMenuModule,
        RouterTestingModule,
        MatTableModule,
        MatFormFieldModule,
        FormsModule,
        MatInputModule,
        MatSelectModule,
        MatRadioModule,
        MatTooltipModule,
        MatPaginatorModule,
        MatButtonModule,
        NoopAnimationsModule
      ],
      declarations: [
        PlconfigComponent,
        ApplicabilityTableComponent,
        BranchSelectorComponent,
        BranchTypeSelectorComponent,
        ConfigurationDropdownComponent,
        ActionDropDownComponent,
        ConfigurationGroupDropdownComponent,
        FeatureDropdownComponent,
      ],
      providers: [{ provide: Router, useValue: { navigate: () => { }}},
        {
          provide: ActivatedRoute, useValue: {
            paramMap: of(
              convertToParamMap(
                {
                  branchId: '10',
                  branchType: 'all'
                }
              )
            )
          }
        },
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PlconfigComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
