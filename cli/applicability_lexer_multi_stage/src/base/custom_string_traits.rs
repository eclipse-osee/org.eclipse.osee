//note: this is a workaround for the stdlib not having a standard ToString for &[u8] & vec<char>
pub(crate) trait CustomToString {
    fn custom_to_string(self) -> String;
}

impl<'a> CustomToString for &'a [u8] {
    fn custom_to_string(self) -> String {
        match String::from_utf8_lossy(self) {
            std::borrow::Cow::Borrowed(x) => x.to_string(),
            std::borrow::Cow::Owned(x) => x,
        }
    }
}

impl<'a> CustomToString for Vec<char> {
    fn custom_to_string(self) -> String {
        String::from_iter(self)
    }
}
// impl<'a, T> CustomToString for T
// where
//     T: IntoIterator<Item = &'a char>,
// {
//     fn custom_to_string(self) -> String {
//         String::from_iter(self)
//     }
// }
