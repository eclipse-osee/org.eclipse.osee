import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatRadioChange, MatRadioGroup, MatRadioModule } from '@angular/material/radio';
import { RouterTestingModule } from '@angular/router/testing';

import { BranchTypeSelectorComponent } from './branch-type-selector.component';

describe('BranchTypeSelectorComponent', () => {
  let component: BranchTypeSelectorComponent;
  let fixture: ComponentFixture<BranchTypeSelectorComponent>;

  @Component({
    selector: 'dummy',
    template: '<div>Dummy</div>'
  })
  class DummyComponent { };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports:[RouterTestingModule.withRoutes(
        [
          { path: '', component: DummyComponent },
          { path: ':branchType', component: DummyComponent },
          { path: ':branchType/:branchId', component: DummyComponent }
        ]
      ),MatRadioModule,FormsModule],
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

  it('should change the branch type to hello', () => {
    component.changeBranchType("hello");
    expect(component.branchType).toEqual("hello");
  });
});
