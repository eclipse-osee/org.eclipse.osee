import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfigurationGroupDropdownComponent } from './configuration-group-dropdown.component';

describe('ConfigurationGroupDropdownComponent', () => {
  let component: ConfigurationGroupDropdownComponent;
  let fixture: ComponentFixture<ConfigurationGroupDropdownComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ConfigurationGroupDropdownComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfigurationGroupDropdownComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
