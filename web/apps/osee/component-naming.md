dropdown = field(select) with multiple options
selector = field(select) with multiple options, does send to database(OBE)
input = field(text field)
toggle = field(boolean) on/off state

Modifiers:
persisted- = If starts with persisted, it saves to DB
current- = apply selection to current global state(i.e. view, branch ETC.)