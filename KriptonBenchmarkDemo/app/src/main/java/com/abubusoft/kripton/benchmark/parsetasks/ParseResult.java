package com.abubusoft.kripton.benchmark.parsetasks;

public class ParseResult {

    public long runDuration;
    public int objectsParsed;

    public ParseResult(long runDuration, int objectsParsed) {
        this.runDuration = runDuration;
        this.objectsParsed = objectsParsed;
    }

}
