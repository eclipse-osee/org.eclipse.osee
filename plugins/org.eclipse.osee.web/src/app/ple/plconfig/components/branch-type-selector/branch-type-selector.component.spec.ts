import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatRadioModule } from '@angular/material/radio';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

import { BranchTypeSelectorComponent } from './branch-type-selector.component';

describe('BranchTypeSelectorComponent', () => {
  let component: BranchTypeSelectorComponent;
  let fixture: ComponentFixture<BranchTypeSelectorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports:[NoopAnimationsModule,MatRadioModule, FormsModule],
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
