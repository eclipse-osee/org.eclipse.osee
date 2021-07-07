import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { ConnectionViewComponent } from './connection-view.component';
import { BaseDummy } from './testing/MockComponents/Base.mock';
import { BranchDummySelector } from './testing/MockComponents/BranchSelector.mock';
import { BranchTypeDummySelector } from './testing/MockComponents/BranchTypeSelector.mock';
import { GraphDummy } from './testing/MockComponents/Graph.mock';

describe('ConnectionViewComponent', () => {
  let component: ConnectionViewComponent;
  let fixture: ComponentFixture<ConnectionViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports:[RouterTestingModule],
      declarations: [ ConnectionViewComponent, BaseDummy, BranchDummySelector, BranchTypeDummySelector, GraphDummy ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ConnectionViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

});
