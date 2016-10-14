/*
 * Decompiled with CFR 0_115.
 */
package jfork.nproperty;

public interface IPropertyListener {
    public void onStart(String var1);

    public void onPropertyMiss(String var1);

    public void onDone(String var1);

    public void onInvalidPropertyCast(String var1, String var2);
}

