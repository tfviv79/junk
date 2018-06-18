use rusqlite::Connection;

pub struct WebInfo {
    id: i32,
    url: String,
    link: String,
    link_title: String,
    link_date: String,
}

impl WebInfo {
    pub fn new(
        url: String,
        link: String,
        link_title: String,
        link_date: String,
    ) -> WebInfo {
        WebInfo {
            id: 0,
            url: url,
            link: link,
            link_title: link_title,
            link_date: link_date,
        }
    }

    pub fn set_id(self, id: i32) -> Self {
        WebInfo { id: id, ..self }
    }

    pub fn set_link(self, link: String, link_date: String, link_title: String) -> Self {
        WebInfo {
            link: link,
            link_date: link_date,
            link_title: link_title,
            ..self
        }
    }

    pub fn load(url: &String) -> Option<WebInfo> {
        let conn = Connection::open("n.db").unwrap();
        let mut stmt = conn.prepare(
                "select id, url, link, link_title, link_date from upinfo where url = ?"
                ).unwrap();
        let mut val_iter = stmt.query(&[url]).unwrap();
        if let Some(v) = val_iter.next() {
            let val = v.unwrap();
            let id: i32 = val.get(0);
            let url: String = val.get(1);
            let link: String = val.get(2);
            let link_title: String = val.get(3);
            let link_date: String = val.get(4);
            let r = WebInfo::new(url, link, link_title, link_date).set_id(id);
            Some(r)
        } else {
            None
        }
    }

    pub fn save(&self) -> bool {
        let conn = Connection::open("n.db").unwrap();
        if let Some(v) = Self::load(&self.url) {
            let id: i32 = v.id;
            conn.execute(
                "update upinfo \
                    set url= ?,\
                        link=?,\
                        link_date = ?,\
                        link_title = ? \
                    where  id = ?", 
                &[
                    &self.url
                    , &self.link
                    , &self.link_date
                    , &self.link_title
                    , &id
                ]
                ).unwrap();
        } else {
            conn.execute(
                "INSERT INTO upinfo (\
                    url,\
                    link,\
                    link_date,\
                    link_title\
                ) values (?, ?, ?, ?)", 
                &[
                    &self.url
                    , &self.link
                    , &self.link_date
                    , &self.link_title
                ]
                ).unwrap();
        }
        true
    }
}
