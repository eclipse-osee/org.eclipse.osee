import { ChangeDetectionStrategy, Component } from '@angular/core';
import { SearchKanbanComponent } from './components/search-kanban/search-kanban.component';

@Component({
	selector: 'osee-kanban',
	imports: [SearchKanbanComponent],
	templateUrl: './kanban.component.html',
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class KanbanComponent {}

export default KanbanComponent;
