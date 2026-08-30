package com.swetlokognatsk.protected_resource.models;

import java.util.Collection;
import java.util.HashSet;

public class ScopesSet extends HashSet<Scopes> {

    public ScopesSet() {
    }

    public ScopesSet(Collection<? extends Scopes> source) {
        super(source);
    }
}
