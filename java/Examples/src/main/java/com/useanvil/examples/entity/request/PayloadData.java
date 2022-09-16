package com.useanvil.examples.entity.request;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class PayloadData implements Serializable {

    public Map<String, Serializable> payloads = new HashMap<>();

}
