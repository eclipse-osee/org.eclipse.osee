use nom::Input;
use nom_locate::LocatedSpan;

pub trait LocatedSpanToInput {
    fn get_input(&self) -> &impl Input;
}

impl<T, X> LocatedSpanToInput for LocatedSpan<T, X>
where
    T: Input,
{
    fn get_input(&self) -> &impl Input {
        return self.fragment();
    }
}
