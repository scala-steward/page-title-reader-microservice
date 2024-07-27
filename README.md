# page-title-reader-microservice

Coding assessment (Scala 2, Akka HTTP).

Completed in 2018, and currently maintained as a demonstration project.

## What the microservice does

Give urls, get page titles

1.  It gets page title for each of given url's in a list.
1.  If URL is presented several times, it will be fetched once, but result will be printed several times, in the same order as in a source list.
1.  It does not follow redirects immediately. Instead, it responds with a new location, so you can handle redirection before getting a final result.
1.  It handles non-urls, empty strings, inaccessible hosts and http codes in a predictable manner

It works well against cases like these:

```sh
curl -H 'Content-Type: application/json' \
    -d '[
          "https://ya.ru",
          "www.dictionary.com/browse/http",
          "https://api.github.com/",
          "http://ya.ru",
          "nonsense://yandex.ru",
          "https://github.com/ladsgfadg",
          "adsfgasdfg",
          "",
          "https://ya.ru"
          ]' \
    -X POST \
    http://localhost:8080

```

... which result in:

```json
[
  { "url": "https://ya.ru", "title": "Яндекс — быстрый поиск в интернете" },
  {
    "url": "www.dictionary.com/browse/http",
    "error": "Redirect: https://www.dictionary.com/browse/http"
  },
  { "url": "https://api.github.com/", "error": "Page has no title" },
  { "url": "http://ya.ru", "error": "Redirect: https://ya.ru/" },
  { "url": "nonsense://yandex.ru", "error": "Bad url" },
  { "url": "https://github.com/ladsgfadg", "error": "Status code: 404" },
  { "url": "adsfgasdfg", "error": "Inaccessible host" },
  { "url": "", "error": "Inaccessible host" },
  { "url": "https://ya.ru", "title": "Яндекс — быстрый поиск в интернете" }
]
```

## How to use it

1.  Install **[sbt](https://www.scala-sbt.org/)** (and **[Postman](https://www.getpostman.com/)** to compose requests manually)
1.  In project directory execute `sbt run`
1.  Send a **POST** request to `http://localhost:8080/`
