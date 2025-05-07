#[derive(Debug, Clone, PartialEq, Eq)]
pub struct UpdatableValue<T>
where
    T: Copy + PartialEq,
{
    pub previous_value: T,
    pub current_value: T,
}
impl<T> UpdatableValue<T>
where
    T: Copy + PartialEq,
{
    pub fn new(value: T) -> Self {
        Self {
            previous_value: value,
            current_value: value,
        }
    }
    pub fn next(&mut self, value: T) {
        self.previous_value = self.current_value;
        self.current_value = value;
    }
    pub fn has_changed(&self) -> bool {
        self.previous_value != self.current_value
    }
    pub fn get_value(&self) -> T {
        self.current_value
    }
}
