import java.util.*;

public class HttpHeaderParser {
    public static final String DELIMITER = ": ";
    public static final String SPACE_DELIMITER = " ";
    public static final String NEXT_LINE_DELIMITER = "\n";

    // Set of all the http versions
    public static Set<String> VALID_HTTP_VERSIONS = Set.of("HTTP/0.9", "HTTP/1.0", "HTTP/1.1", "HTTP/2.0");

    // Keeping a default set of header values which are supported.
    private static final Set<String> DEFAULT_HEADER_KEYS = Set.of(
            "cache-control",
            "content-length",
            "content-type",
            "date");

    // HTTP codes using ref: https://en.wikipedia.org/wiki/List_of_HTTP_status_codes. Assumption: each status code will
    // have a set of valid head names (status codes may have separate set of valid headers keys). For now considering all
    // status code have same set of headers names.
    public static final Map<String, Set<String>> STATUS_TO_HEADER_MAPPING = Map.ofEntries(
            Map.entry("100", DEFAULT_HEADER_KEYS),
            Map.entry("101", DEFAULT_HEADER_KEYS),
            Map.entry("102", DEFAULT_HEADER_KEYS),
            Map.entry("103", DEFAULT_HEADER_KEYS),
            Map.entry("200", DEFAULT_HEADER_KEYS),
            Map.entry("201", DEFAULT_HEADER_KEYS),
            Map.entry("202", DEFAULT_HEADER_KEYS),
            Map.entry("203", DEFAULT_HEADER_KEYS),
            Map.entry("204", DEFAULT_HEADER_KEYS),
            Map.entry("205", DEFAULT_HEADER_KEYS),
            Map.entry("206", DEFAULT_HEADER_KEYS),
            Map.entry("207", DEFAULT_HEADER_KEYS),
            Map.entry("208", DEFAULT_HEADER_KEYS),
            Map.entry("226", DEFAULT_HEADER_KEYS),
            Map.entry("300", DEFAULT_HEADER_KEYS),
            Map.entry("301", DEFAULT_HEADER_KEYS),
            Map.entry("302", DEFAULT_HEADER_KEYS),
            Map.entry("303", DEFAULT_HEADER_KEYS),
            Map.entry("304", DEFAULT_HEADER_KEYS),
            Map.entry("305", DEFAULT_HEADER_KEYS),
            Map.entry("306", DEFAULT_HEADER_KEYS),
            Map.entry("307", DEFAULT_HEADER_KEYS),
            Map.entry("308", DEFAULT_HEADER_KEYS),
            Map.entry("400", DEFAULT_HEADER_KEYS),
            Map.entry("401", DEFAULT_HEADER_KEYS),
            Map.entry("402", DEFAULT_HEADER_KEYS),
            Map.entry("403", DEFAULT_HEADER_KEYS),
            Map.entry("404", DEFAULT_HEADER_KEYS),
            Map.entry("405", DEFAULT_HEADER_KEYS),
            Map.entry("406", DEFAULT_HEADER_KEYS),
            Map.entry("407", DEFAULT_HEADER_KEYS),
            Map.entry("408", DEFAULT_HEADER_KEYS),
            Map.entry("409", DEFAULT_HEADER_KEYS),
            Map.entry("410", DEFAULT_HEADER_KEYS),
            Map.entry("411", DEFAULT_HEADER_KEYS),
            Map.entry("412", DEFAULT_HEADER_KEYS),
            Map.entry("413", DEFAULT_HEADER_KEYS),
            Map.entry("414", DEFAULT_HEADER_KEYS),
            Map.entry("415", DEFAULT_HEADER_KEYS),
            Map.entry("416", DEFAULT_HEADER_KEYS),
            Map.entry("417", DEFAULT_HEADER_KEYS),
            Map.entry("418", DEFAULT_HEADER_KEYS),
            Map.entry("421", DEFAULT_HEADER_KEYS),
            Map.entry("422", DEFAULT_HEADER_KEYS),
            Map.entry("423", DEFAULT_HEADER_KEYS),
            Map.entry("424", DEFAULT_HEADER_KEYS),
            Map.entry("425", DEFAULT_HEADER_KEYS),
            Map.entry("426", DEFAULT_HEADER_KEYS),
            Map.entry("427", DEFAULT_HEADER_KEYS),
            Map.entry("428", DEFAULT_HEADER_KEYS),
            Map.entry("429", DEFAULT_HEADER_KEYS),
            Map.entry("430", DEFAULT_HEADER_KEYS),
            Map.entry("431", DEFAULT_HEADER_KEYS),
            Map.entry("451", DEFAULT_HEADER_KEYS),
            Map.entry("500", DEFAULT_HEADER_KEYS),
            Map.entry("501", DEFAULT_HEADER_KEYS),
            Map.entry("502", DEFAULT_HEADER_KEYS),
            Map.entry("503", DEFAULT_HEADER_KEYS),
            Map.entry("504", DEFAULT_HEADER_KEYS),
            Map.entry("505", DEFAULT_HEADER_KEYS),
            Map.entry("506", DEFAULT_HEADER_KEYS),
            Map.entry("507", DEFAULT_HEADER_KEYS),
            Map.entry("508", DEFAULT_HEADER_KEYS),
            Map.entry("510", DEFAULT_HEADER_KEYS),
            Map.entry("511", DEFAULT_HEADER_KEYS)
    );

    public boolean parseHttpHeader(String input) {
        // check if input string is null or empty.
        if (input == null || input == "") {
            System.out.println("Invalid status line");
            return false;
        }

        // Split the input header string by next line and store in string array.
        String[] lines = input.split(NEXT_LINE_DELIMITER);

        // Check if the first header line is valid.
        boolean isValidHeaderLine = isValidHeader(lines[0]);

        if (!isValidHeaderLine) {
            System.out.println("Invalid status line");
            return false;
        }

        String httpVersion = getHttpVersion(lines[0]);
        String status = getStatus(lines[0]);

        System.out.println("HTTP version: " + httpVersion);
        System.out.println("Status: " + status);

        // Store all the header (not the first line) in arrayList to validate and get supported header counts.
        List<String> headerKeyValues = new ArrayList<String>();
        for (int i = 1; i < lines.length; i++) {
            headerKeyValues.add(lines[i]);
        }

        Map<String, String> headerkeyValueMap = getHeaderKeyValue(headerKeyValues);

        long numberOfValidHeaders = getNumberOfValidHeaders(headerkeyValueMap, status);

        System.out.println("Number of valid header: " + numberOfValidHeaders);
        System.out.println("Number of invalid header: " + (headerKeyValues.size() - numberOfValidHeaders));

        return (headerKeyValues.size() == numberOfValidHeaders);
    }

    // split each header key and values and store it in a Map<headerKey, value>.
    private Map<String, String> getHeaderKeyValue(List<String> headerKeyValues) {

        Map<String, String> headerkeyValueMap = new HashMap<>();

        for (String headerKeyValue : headerKeyValues) {
            if (isValidHeaderKeyValue(headerKeyValue)) {
                String[] keyValue = headerKeyValue.split(DELIMITER);
                headerkeyValueMap.put(keyValue[0], keyValue[1]);
            }
        }

        return headerkeyValueMap;
    }

    // check if header string is valid
    private boolean isValidHeaderKeyValue(String headerKeyValue) {
        if (headerKeyValue == null || headerKeyValue == "") {
            return false;
        }
        String[] splitHeaderValues = headerKeyValue.split(DELIMITER);

        if (splitHeaderValues.length == 2) {
            return true;
        }

        return false;
    }

    // get status from header string.
    private String getStatus(String headerString) {
        return headerString.split(SPACE_DELIMITER)[1];
    }

    // validate the header string.
    private boolean isValidHeader(String headerString) {
        if (headerString == null || headerString == "") {
            return false;
        }
        String[] splitHeaderValues = headerString.split(SPACE_DELIMITER);

        if (splitHeaderValues.length == 3 && VALID_HTTP_VERSIONS.contains(splitHeaderValues[0]) && STATUS_TO_HEADER_MAPPING.containsKey(splitHeaderValues[1])) {
            return true;
        }

        return false;
    }

    // get http version from header string
    private String getHttpVersion(String headerString) {
        return headerString.split(SPACE_DELIMITER)[0].split("/")[1];
    }

    /**
     * Gets the number of valid header for a status code. A valid header should be contained in STATUS_TO_HEADER_MAPPING,
     * which maps status codes to set of valid header that status code supports or null/empty value for a header value.
     *
     * @param headerKeyValueMap
     * @param status
     * @return number of valid headers
     */
    private long getNumberOfValidHeaders(Map<String, String> headerKeyValueMap, String status) {
        Set<String> headerToValueTypeMapping = STATUS_TO_HEADER_MAPPING.get(status);
        return headerKeyValueMap.entrySet().stream()
                .filter(entry -> headerToValueTypeMapping.contains(entry.getKey()))
                .count();
    }

    // Tests
    public static void main(String[] args) {
        HttpHeaderParser httpHeaderParser = new HttpHeaderParser();

        // Test 1:  test null string
        boolean output = httpHeaderParser.parseHttpHeader(null);
        System.out.println("*************************************Test case 1 pass: " + (output == false));

        // Test 2:  test empty string
        output = httpHeaderParser.parseHttpHeader("");
        System.out.println("*************************************Test case 2 pass: " + (output == false));

        // Test 3:  Invalid header format.
        output = httpHeaderParser.parseHttpHeader("HTTP/1.0 200 OK" + // No next-line
                "cache-control: public\n" +
                "content-length: 0\n" +
                "content-type: image/svg+xml\n" +
                "date: Tue, 22 Jun 2021 22:24:42 GMT\n");
        System.out.println("*************************************Test case 3 pass: " + (output == false));

        // Test 4:  Invalid http version.
        httpHeaderParser.parseHttpHeader("HTTP/5.0 200 OK" +
                "cache-control: public\n" +
                "content-length: 0\n" +
                "content-type: image/svg+xml\n" +
                "date: Tue, 22 Jun 2021 22:24:42 GMT\n");
        System.out.println("*************************************Test case 4 pass: " + (output == false));

        // Test 5:  Invalid http status code version.
        output = httpHeaderParser.parseHttpHeader("HTTP/1.0 700 OK\n" +
                "cache-control: public\n" +
                "content-length: 0\n" +
                "content-type: image/svg+xml\n" +
                "date: Tue, 22 Jun 2021 22:24:42 GMT\n");
        System.out.println("*************************************Test case 5 pass: " + (output == false));

        // Test 6:  not supported header key .
        output = httpHeaderParser.parseHttpHeader("HTTP/1.0 200 OK\n" +
                "cache-control: public\n" +
                "unsupported: 0\n" +
                "content-type: image/svg+xml\n" +
                "date: Tue, 22 Jun 2021 22:24:42 GMT\n");
        System.out.println("*************************************Test case 6 pass: " + (output == false));

        // Test 7: invalid header key format.
        output = httpHeaderParser.parseHttpHeader("HTTP/1.0 200 OK\n" +
                "cache-control: public\n" +
                "unsupported\n" +
                "content-type: image/svg+xml\n" +
                "date: Tue, 22 Jun 2021 22:24:42 GMT\n");
        System.out.println("*************************************Test case 7 pass: " + (output == false));

        // Test 8:  valid header .
        output = httpHeaderParser.parseHttpHeader("HTTP/1.0 200 OK\n" +
                "cache-control: public\n" +
                "content-length: 0\n" +
                "content-type: image/svg+xml\n" +
                "date: Tue, 22 Jun 2021 22:24:42 GMT\n");
        System.out.println("*************************************Test case 8 pass: " + (output == true));

        // Test 9:  valid header .
        output = httpHeaderParser.parseHttpHeader("HTTP/1.1 302 Found\n" +
                "cache-control: public\n" +
                "Transfer-encoding: chunked\n" +
                "invalid_header\n" +
                "date: Tue, 22 Jun 2021 22:24:42 GMT");
        System.out.println("*************************************Test case 9 pass: " + (output == false));
    }
}