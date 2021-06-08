import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { AddMessageDialog } from '../../../types/AddMessageDialog';

import { AddMessageDialogComponent } from './add-message-dialog.component';

describe('AddMessageDialogComponent', () => {
  let component: AddMessageDialogComponent;
  let fixture: ComponentFixture<AddMessageDialogComponent>;
  let dialogRef = jasmine.createSpyObj('MatDialogRef', ['close']);
  let dialogData: AddMessageDialog = {
    id: '-1',
    name: '',
    description: '',
    interfaceMessageNumber: '',
    interfaceMessagePeriodicity: '',
    interfaceMessageRate: '',
    interfaceMessageType: '',
    interfaceMessageWriteAccess:''
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports:[MatDialogModule,FormsModule,MatFormFieldModule,MatInputModule,MatButtonModule,NoopAnimationsModule],
      declarations: [AddMessageDialogComponent],
      providers: [{
        provide: MatDialogRef, useValue: dialogRef
      },{provide:MAT_DIALOG_DATA,useValue:dialogData}]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AddMessageDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
