package com.useanvil.examples;

public class Constants {

//    public static final String GRAPHQL_ENDPOINT = "https://graphql.useanvil.com";
    public static final String GRAPHQL_ENDPOINT = "http://localhost:3000/graphql";

    // Remember to end the URI string here with a `/` since it will be appended onto
    // when the HTTP clients are used. *ALSO* when providing a URI path
    // (i.e. `RestClient.SendRequest("path/here", data);`), the path must _NOT_
    // have a starting `/` or the path will be rewritten.
    // For example: Using "/fill/my.pdf" will have a final URI of
    // "http://app.useanvil.com/fill/my.pdf", removing the "api/v1".
//    public static final String REST_ENDPOINT = "https://app.useanvil.com/api/v1/";
    public static final String REST_ENDPOINT = "http://localhost:3000/api/v1/";

    // https://www.useanvil.com/docs/api/fill-pdf
    public static final String FillPdf = "fill/{0}.pdf";

    // https://www.useanvil.com/docs/api/generate-pdf
    public static final String GeneratePdf = "generate-pdf";

    // https://www.useanvil.com/docs/api/e-signatures#downloading-documents
    // In this case _we want_ the initial `/` because this does not use the base endpoint
    // above of `/api/v1/`.
    public static final String DownloadDocuments = "/api/document-group/{0}.zip";
}
