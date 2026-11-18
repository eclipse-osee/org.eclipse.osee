import { KanbanCard } from './kanban-card';
import { KanbanColumnId } from './kanban-column-id';

export interface KanbanColumn {
	id: KanbanColumnId;
	title: string;
	cards: KanbanCard[];
}
