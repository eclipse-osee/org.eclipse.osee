import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PleComponent } from './ple.component';

describe('PleComponent', () => {
  let component: PleComponent;
  let fixture: ComponentFixture<PleComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PleComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
