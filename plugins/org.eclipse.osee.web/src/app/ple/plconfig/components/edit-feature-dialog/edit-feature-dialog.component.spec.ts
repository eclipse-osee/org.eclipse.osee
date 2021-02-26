import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { of } from 'rxjs';
import { PlConfigBranchService } from '../../services/pl-config-branch-service.service';
import { testBranchApplicability } from '../../testing/mockBranchService';
import { PlConfigApplicUIBranchMapping } from '../../types/pl-config-applicui-branch-mapping';

import { EditFeatureDialogComponent } from './edit-feature-dialog.component';

describe('EditFeatureDialogComponent', () => {
  let component: EditFeatureDialogComponent;
  let fixture: ComponentFixture<EditFeatureDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EditFeatureDialogComponent],
      providers: [{ provide: MatDialogRef, useValue: {} },
        {
          provide: MAT_DIALOG_DATA, useValue: {
            currentBranch: "01238082141",
            feature: {
              id: '',
              idIntValue: 6451325,
              idString: '6451325',
              type: null ,
              name: 'feature1',
              description: 'lorem ipsum',
              valueType: 'string',
              valueStr: '',
              defaultValue: 'hello',
              productAppStr: 'OFP',
              values: ['hello','world'],
              productApplicabilities: ['OFP'],
              multiValued: false,
              setValueStr(): void {
        this.valueStr=this.values.toString();
              },
            setProductAppStr(): void {
        this.productAppStr = this.productApplicabilities.toString();
              } 
            }
        } },
        {
          provide: PlConfigBranchService, useValue: {
          getBranchApplicability(){return of(testBranchApplicability)}
        }}]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EditFeatureDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
