import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatStepperModule } from '@angular/material/stepper';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { AddStructureDialog } from '../../types/AddStructureDialog';
import { structure } from '../../types/structure';

import { AddStructureDialogComponent } from './add-structure-dialog.component';

describe('AddStructureDialogComponent', () => {
  let component: AddStructureDialogComponent;
  let fixture: ComponentFixture<AddStructureDialogComponent>;
  let dialogRef = jasmine.createSpyObj('MatDialogRef', ['close']);
  let dialogData: AddStructureDialog = {
    id: '123456',
    name: 'submessage',
    structure: {
      id: '',
      name: '',
      description: '',
      elements: [],
      interfaceMaxSimultaneity: "1",
      interfaceMinSimultaneity: "0",
      interfaceStructureCategory: "",
      interfaceTaskFileType:0
    }
  }
  let dummyStructure: structure = {
    id: '10',
    name: '',
    description: '',
    elements: [],
    interfaceMaxSimultaneity: '1',
    interfaceMinSimultaneity: '0',
    interfaceStructureCategory: '',
    interfaceTaskFileType:0
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports:[HttpClientTestingModule,MatStepperModule,MatDialogModule,MatButtonModule,FormsModule,MatFormFieldModule,MatSelectModule,MatInputModule,MatSlideToggleModule,NoopAnimationsModule],
      declarations: [AddStructureDialogComponent],
      providers:[{provide:MAT_DIALOG_DATA,useValue:dialogData},{provide:MatDialogRef,useValue:dialogRef}]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AddStructureDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should movetoStep', () => {
    let stepper = jasmine.createSpyObj('stepper', {}, { selectedIndex: 0 });
    spyOn(stepper, 'selectedIndex').and.callThrough();
    component.moveToStep(3, stepper);
    expect(stepper.selectedIndex).toEqual(0);
  });

  it('should create new  by setting id to -1', () => {
    component.createNew();
    expect(component.data.structure.id).toEqual('-1');
  });

  it('should store the id', () => {
    component.storeId(dummyStructure);
    expect(component.storedId).toEqual('10');
  });

  it('should movetoStep 3', () => {
    let stepper = jasmine.createSpyObj('stepper', {}, { selectedIndex: 0 });
    spyOn(stepper, 'selectedIndex').and.callThrough();
    let spy = spyOn(component, 'moveToStep').and.stub();
    component.moveToReview(stepper);
    expect(spy).toHaveBeenCalled();
    expect(spy).toHaveBeenCalledWith(3, stepper);
  });
});