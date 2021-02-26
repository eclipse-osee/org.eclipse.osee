import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CopyConfigurationDialogComponent } from './copy-configuration-dialog.component';

describe('CopyConfigurationDialogComponent', () => {
  let component: CopyConfigurationDialogComponent;
  let fixture: ComponentFixture<CopyConfigurationDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CopyConfigurationDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CopyConfigurationDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
