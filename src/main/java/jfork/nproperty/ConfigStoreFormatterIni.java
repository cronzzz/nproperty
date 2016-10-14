/*
 * Decompiled with CFR 0_115.
 */
package jfork.nproperty;

import java.util.Map;
import java.util.Set;
import jfork.nproperty.ConfigStoreFormatterImpl;

public class ConfigStoreFormatterIni
extends ConfigStoreFormatterImpl {
    @Override
    public String generate() {
        String lineSeparator = System.getProperty("line.separator");
        boolean isFirstField = true;
        StringBuilder builder = new StringBuilder();
        for (Map.Entry pair : this.pairs.entrySet()) {
            if (isFirstField) {
                isFirstField = false;
            } else {
                builder.append(lineSeparator).append(lineSeparator);
            }
            builder.append((String)pair.getKey()).append(" = ").append((String)pair.getValue());
        }
        return builder.toString();
    }
}

