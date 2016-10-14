/*
 * Decompiled with CFR 0_115.
 */
package jfork.nproperty;

import java.util.LinkedHashMap;
import java.util.Map;
import jfork.nproperty.IConfigStoreFormatter;

public abstract class ConfigStoreFormatterImpl
implements IConfigStoreFormatter {
    protected Map<String, String> pairs = new LinkedHashMap<String, String>();

    @Override
    public void addPair(String key, String value) {
        if (!this.pairs.containsKey(key)) {
            this.pairs.put(key, value);
        }
    }
}

