pub trait AsStr {
    type AsStrOutputType<'x>
    where
        Self: 'x;
    fn as_str<'x>(&self) -> Self::AsStrOutputType<'_>;
}

impl AsStr for String {
    type AsStrOutputType<'x> = &'x str;
    fn as_str<'x>(&self) -> Self::AsStrOutputType<'_> {
        self.as_str()
    }
}
// pub trait AsStr<'x>
// where
//     Self: 'x,
// {
//     type AsStrOutputType;
//     // where
//     //     Self: 'x;
//     fn as_str(&self) -> Self::AsStrOutputType
//     where
//         Self: 'x;
// }

// impl<'x> AsStr<'x> for String
// where
//     Self: 'x,
// {
//     type AsStrOutputType = &'x str;
//     fn as_str(&self) -> Self::AsStrOutputType
//     where
//         Self: 'x,
//     {
//         self.as_str()
//     }
// }
