import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BranchTypeSelectorComponent } from './branch-type-selector.component';

describe('BranchTypeSelectorComponent', () => {
  let component: BranchTypeSelectorComponent;
  let fixture: ComponentFixture<BranchTypeSelectorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BranchTypeSelectorComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BranchTypeSelectorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
