import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdateAttachmentDialogComponent } from './update-attachment-dialog.component';

describe('UpdateAttachmentDialogComponent', () => {
  let component: UpdateAttachmentDialogComponent;
  let fixture: ComponentFixture<UpdateAttachmentDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UpdateAttachmentDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UpdateAttachmentDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
