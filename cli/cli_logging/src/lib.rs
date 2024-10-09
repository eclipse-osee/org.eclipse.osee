/*********************************************************************
 * Copyright (c) 2025 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
use clap_verbosity_flag::{Verbosity, WarnLevel};
use indicatif::{ProgressState, ProgressStyle};
use std::time::Duration;
use tracing::{Level, Span, info_span};
use tracing_indicatif::IndicatifLayer;
use tracing_indicatif::span_ext::IndicatifSpanExt;
use tracing_subscriber::fmt::writer::MakeWriterExt;
use tracing_subscriber::layer::SubscriberExt;
use tracing_subscriber::util::SubscriberInitExt;

pub fn initialize_logging(verbosity: &Verbosity<WarnLevel>, starting_command: &str) -> Span {
    let debug_output = match verbosity.log_level_filter() {
        clap_verbosity_flag::LevelFilter::Debug => true,
        clap_verbosity_flag::LevelFilter::Trace => true,
        _rest => false,
    };
    let indicatif_layer = IndicatifLayer::new().with_progress_style(
ProgressStyle::with_template(
"{color_start}{span_child_prefix}{span_fields} -- {span_name} {wide_msg} {elapsed_subsec}{color_end}",
)
.unwrap()
.with_key(
"elapsed_subsec",
elapsed_subsec,
)
.with_key(
"color_start",
|state: &ProgressState, writer: &mut dyn std::fmt::Write| {
    let elapsed = state.elapsed();

    if elapsed > Duration::from_secs(8) {
        // Red
        let _ = write!(writer, "\x1b[{}m", 1 + 30);
    } else if elapsed > Duration::from_secs(4) {
        // Yellow
        let _ = write!(writer, "\x1b[{}m", 3 + 30);
    }
},
)
.with_key(
"color_end",
|state: &ProgressState, writer: &mut dyn std::fmt::Write| {
    if state.elapsed() > Duration::from_secs(4) {
        let _ =write!(writer, "\x1b[0m");
    }
},
),
).with_span_child_prefix_symbol("↳ ").with_span_child_prefix_indent("\t");
    tracing_subscriber::registry()
        .with(
            tracing_subscriber::fmt::layer()
                .with_writer(indicatif_layer.get_stderr_writer().with_max_level(
                    match verbosity.log_level_filter() {
                        clap_verbosity_flag::LevelFilter::Error => Level::ERROR,
                        clap_verbosity_flag::LevelFilter::Warn => Level::WARN,
                        clap_verbosity_flag::LevelFilter::Info => Level::INFO,
                        clap_verbosity_flag::LevelFilter::Debug => Level::DEBUG,
                        clap_verbosity_flag::LevelFilter::Trace => Level::TRACE,
                        clap_verbosity_flag::LevelFilter::Off => Level::ERROR,
                    },
                ))
                .with_line_number(debug_output)
                .with_thread_ids(debug_output)
                .with_thread_names(debug_output)
                .compact(),
        )
        .with(indicatif_layer)
        .init();
    let header_span = info_span!("header");
    header_span.pb_set_style(
        &ProgressStyle::with_template(
            &("{spinner}\t\tWorking on tasks for command: `".to_string()
                + starting_command
                + "`. {wide_msg} {elapsed_subsec}\n{wide_bar}"),
        )
        .unwrap()
        .with_key("elapsed_subsec", elapsed_subsec)
        .progress_chars("---"),
    );
    header_span.pb_start();

    // Bit of a hack to show a full "-----" line underneath the header.
    header_span.pb_set_length(20);
    header_span.pb_set_position(1);
    let _header_span_enter = header_span.enter();
    header_span.clone()
}
fn elapsed_subsec(state: &ProgressState, writer: &mut dyn std::fmt::Write) {
    let seconds = state.elapsed().as_secs();
    let sub_seconds = (state.elapsed().as_millis() % 1000) / 100;
    let _ = writer.write_str(&format!("{seconds}.{sub_seconds}s"));
}
