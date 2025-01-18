use applicability_parser::parse_applicability;
use criterion::*;
use rand::distributions::Alphanumeric;
use rand::{thread_rng, Rng};

fn bench_parser(c: &mut Criterion) {
    let mut group = c.benchmark_group("sample-size-example");
    let rand_string: String = thread_rng()
        .sample_iter(&Alphanumeric)
        .take(100000)
        .map(char::from)
        .collect();
    group.bench_function("parse_large_string", |b| {
        b.iter(|| {
            let _ = std::hint::black_box(parse_applicability(&rand_string, "``", "``"));
        })
    });
    group.bench_function("sample_text", |b| {
        b.iter(|| {
            let _ = std::hint::black_box(parse_applicability(
                r#"Some other text
``Feature[SOMETHING] ``
Some text here  
``End Feature`` More text
``Configuration [SOME_CONFIGURATION]``
configuration text
``End Configuration``","``","``"#,
                "``",
                "``",
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
