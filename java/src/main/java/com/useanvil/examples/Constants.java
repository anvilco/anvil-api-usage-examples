package com.useanvil.examples;

public class Constants {

    public static final String BASE_URL = "https://app.useanvil.com";
    public static final String GRAPHQL_ENDPOINT = Constants.BASE_URL + "/graphql";
    public static final String REST_ENDPOINT = BASE_URL + "/api/v1/";

    // https://www.useanvil.com/docs/api/fill-pdf
    public static final String FillPdf = "fill/%s.pdf";

    // https://www.useanvil.com/docs/api/generate-pdf
    public static final String GeneratePdf = "generate-pdf";

    // https://www.useanvil.com/docs/api/e-signatures#downloading-documents
    // In this case _we want_ the initial `/` because this does not use the base endpoint
    // above of `/api/v1/`.
    public static final String DownloadDocuments = "/api/document-group/%s.zip";
}
