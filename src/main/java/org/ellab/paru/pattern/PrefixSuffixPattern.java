package org.ellab.paru.pattern;

public abstract class PrefixSuffixPattern implements PasswordPattern {
    private String[] prefix, suffix;
    protected String currentValue;
    private int currentPrefix;
    private int currentSuffix;

    public PrefixSuffixPattern(String prefix, String suffix) {
        initPrefixSuffix(prefix, suffix);
    }

    public PrefixSuffixPattern(String[] prefix, String[] suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    protected void initPrefixSuffix(String prefix, String suffix) {
        this.prefix = (prefix == null ? "" : prefix).split("\\,");
        this.suffix = (suffix == null ? "" : suffix).split("\\,");
    }

    @Override
    public String next() {
        if (!hasNext()) {
            return null;
        }

        if (currentValue != null && currentSuffix < suffix.length - 1) {
            return prefix[currentPrefix] + currentValue + suffix[++currentSuffix];
        }
        else if (currentValue != null && currentPrefix < prefix.length - 1) {
            currentSuffix = 0;
            return prefix[++currentPrefix] + currentValue + suffix[currentSuffix];
        }
        else {
            currentPrefix = 0;
            currentSuffix = 0;
            currentValue = prefixSuffixNext();
            if (currentValue == null) {
                return null;
            }
            return prefix[currentPrefix] + currentValue + suffix[currentSuffix];
        }
    }

    public boolean hasNext() {
        return prefixSuffixHasNext() || (currentPrefix < prefix.length - 1 || currentSuffix < suffix.length - 1);
    }

    @Override
    public double total() {
        return prefixSuffixTotal() * prefix.length * suffix.length;
    }

    public String[] getPrefix() {
        return prefix;
    }

    public String[] getSuffix() {
        return suffix;
    }

    protected abstract String prefixSuffixNext();

    protected abstract boolean prefixSuffixHasNext();

    protected abstract double prefixSuffixTotal();

}
