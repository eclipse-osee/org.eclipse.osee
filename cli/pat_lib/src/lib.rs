use anyhow::Result;
use tracing::Span;

mod cli_options;
mod initial;
mod util;
mod watch;

pub use cli_options::{PatCliOptions, PatInternalCliOptions};
pub use initial::project_repository_initial;

pub fn project_repository(args: PatInternalCliOptions, header_span: &Span) -> Result<()> {
    project_repository_initial(args, header_span)
}
