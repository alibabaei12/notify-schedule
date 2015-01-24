package org.jakelcode.schedule.realm;

import io.realm.RealmObject;

/**
 * @author Pin Khe "Jake" Loo (24 January, 2015)
 */
public class RealmInt extends RealmObject {
    private int value;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
