pub trait HasLength {
    fn len(&self) -> usize;
}

impl HasLength for String {
    fn len(&self) -> usize {
        self.len()
    }
}

impl<T> HasLength for [T] {
    fn len(&self) -> usize {
        self.len()
    }
}

impl<T> HasLength for Vec<T> {
    fn len(&self) -> usize {
        self.len()
    }
}
