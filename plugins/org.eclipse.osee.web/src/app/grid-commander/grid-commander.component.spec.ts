import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GridCommanderComponent } from './grid-commander.component';

describe('GridCommanderComponent', () => {
  let component: GridCommanderComponent;
  let fixture: ComponentFixture<GridCommanderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GridCommanderComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GridCommanderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
