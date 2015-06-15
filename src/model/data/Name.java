package model.data;

import java.io.Serializable;

public final class Name implements Serializable {

    public final String shortName;
    public final String longName;

    public Name(String shortName, String longName) {
        this.shortName = shortName;
        this.longName = longName;
    }

    @Override
    public String toString() {
        return shortName + " (" + longName + ")";
    }

    @Override
    public boolean equals(Object name) {
        if(!(name instanceof Name)) {
            return false;
        }
        if(this == name) {
            return true;
        }
        if(this.shortName.equals(((Name)name).shortName)) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 31 * 17 + shortName.hashCode();
    }
}