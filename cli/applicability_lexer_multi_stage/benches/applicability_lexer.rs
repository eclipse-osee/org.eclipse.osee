use applicability_lexer_config_markdown::ApplicabiltyMarkdownLexerConfig;
use applicability_lexer_multi_stage::parser::tokenize_comments;
use criterion::*;
use nom_locate::LocatedSpan;
use rand::distributions::Alphanumeric;
use rand::{thread_rng, Rng};

fn bench_parser(c: &mut Criterion) {
    let doc_config: ApplicabiltyMarkdownLexerConfig = ApplicabiltyMarkdownLexerConfig {};
    let mut group = c.benchmark_group("lexer");
    let rand_string: String = thread_rng()
        .sample_iter(&Alphanumeric)
        .take(100000)
        .map(char::from)
        .collect();
    group.bench_function("parse_large_string", |b| {
        b.iter(|| {
            let _ =
                std::hint::black_box(tokenize_comments::<ApplicabiltyMarkdownLexerConfig, &str>(
                    &doc_config,
                    LocatedSpan::new_extra(&rand_string, ((0usize, 0), (0usize, 0))),
                ));
        })
    });
    group.bench_function("sample_text", |b| {
        b.iter(|| {
            let _ =
                std::hint::black_box(tokenize_comments::<ApplicabiltyMarkdownLexerConfig, &str>(
                    &doc_config,
                    LocatedSpan::new_extra(
                        r#"Some other text
``Feature[SOMETHING] ``
Some text here  
``End Feature`` More text
``Configuration [SOME_CONFIGURATION]``
configuration text
``End Configuration``","``","``"#,
                        ((0usize, 0), (0usize, 0)),
                    ),
                ));
        });
    });
    group.finish();
}
criterion_group! {
    name = benches;
    config = Criterion::default().sample_size(1000);
    targets = bench_parser
}
criterion_main!(benches);
