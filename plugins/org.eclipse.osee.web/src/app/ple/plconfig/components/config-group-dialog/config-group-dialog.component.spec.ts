import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfigGroupDialogComponent } from './config-group-dialog.component';

describe('ConfigGroupDialogComponent', () => {
  let component: ConfigGroupDialogComponent;
  let fixture: ComponentFixture<ConfigGroupDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ConfigGroupDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfigGroupDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
