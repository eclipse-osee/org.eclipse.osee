import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NativeContentEditorComponent } from './native-content-editor.component';

describe('NativeContentEditorComponent', () => {
  let component: NativeContentEditorComponent;
  let fixture: ComponentFixture<NativeContentEditorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NativeContentEditorComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NativeContentEditorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
