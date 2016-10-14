/*
 * Decompiled with CFR 0_115.
 */
package jfork.nproperty;

import java.io.IOException;

public interface IConfigStoreFormatter {
    public void addPair(String var1, String var2);

    public String generate() throws IOException;
}

