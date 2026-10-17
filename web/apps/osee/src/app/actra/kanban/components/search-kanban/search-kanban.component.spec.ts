import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SearchKanbanComponent } from './search-kanban.component';

describe('SearchKanbanComponent', () => {
  let component: SearchKanbanComponent;
  let fixture: ComponentFixture<SearchKanbanComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SearchKanbanComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SearchKanbanComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
