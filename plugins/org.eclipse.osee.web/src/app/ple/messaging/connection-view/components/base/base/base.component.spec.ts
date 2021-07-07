import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BranchDummySelector} from '../../../testing/MockComponents/BranchSelector.mock'
import { BranchTypeDummySelector } from '../../../testing/MockComponents/BranchTypeSelector.mock';
import { GraphDummy } from '../../../testing/MockComponents/Graph.mock';


import { BaseComponent } from './base.component';

describe('BaseComponent', () => {
  let component: BaseComponent;
  let fixture: ComponentFixture<BaseComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports:[],
      declarations: [ BaseComponent, BranchDummySelector, BranchTypeDummySelector, GraphDummy ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BaseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
