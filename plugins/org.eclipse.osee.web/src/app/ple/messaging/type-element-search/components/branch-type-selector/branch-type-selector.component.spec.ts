import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { MatRadioGroupHarness } from '@angular/material/radio/testing';
import { MatRadioButtonHarness } from '@angular/material/radio/testing';
import { BranchTypeSelectorComponent } from './branch-type-selector.component';
import { MatRadioModule } from '@angular/material/radio';
import { RouterTestingModule } from '@angular/router/testing';
import { MatFormFieldModule } from '@angular/material/form-field';
import { FormsModule } from '@angular/forms';

describe('BranchTypeSelectorComponent', () => {
  let component: BranchTypeSelectorComponent;
  let fixture: ComponentFixture<BranchTypeSelectorComponent>;
  let loader: HarnessLoader;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports:[MatRadioModule,FormsModule,MatFormFieldModule,RouterTestingModule.withRoutes(
        [
          { path: ':branchType/:branchId/typeSearch', component: BranchTypeSelectorComponent },
          { path: ':branchType/typeSearch', component: BranchTypeSelectorComponent },
          { path: 'typeSearch', component: BranchTypeSelectorComponent },
        ]
      )],
      declarations: [ BranchTypeSelectorComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BranchTypeSelectorComponent);
    loader = TestbedHarnessEnvironment.loader(fixture);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('Core Functionality', () => {
    it('should set the type to product line',async () => {
      await (await loader.getHarness(MatRadioButtonHarness.with({ label: 'Product Line' }))).check();
      expect(component.branchType).toEqual('product line')
    });
  
    it('should set the type to working',async () => {
      await (await loader.getHarness(MatRadioButtonHarness.with({ label: 'Working' }))).check();
      expect(component.branchType).toEqual('working')
    });
  })
});
