package com.swetlokognatsk.client;

import java.io.Serializable;

public record Token(String value, String type) implements Serializable {

}
