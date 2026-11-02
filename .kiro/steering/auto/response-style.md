---
inclusion: always
---

# Response Style

How the user wants the AI to communicate.

## Analyze quietly, report conclusions

- When answering a question or investigating something (reading files, running searches/checks, tracing logic), do
  the work quietly. Do NOT narrate each step, thought, file read, or intermediate finding in the chat.
- After the analysis is done, give a concise summary: the conclusion/answer first, then just the supporting detail
  that matters. Skip the play-by-play of how it was reached.
- Tool calls themselves are already visible to the user, so there is no need to also describe them in prose.
- This is about discussion/analysis responses. When actually making code edits, still explain what changed and why
  (briefly) as usual.
- Keep it short by default. The user can ask for more detail or the full reasoning if they want it.
