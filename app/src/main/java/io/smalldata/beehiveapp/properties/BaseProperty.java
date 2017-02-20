package io.smalldata.beehiveapp.properties;

import android.content.Context;

/**
 *
 * Created by fnokeke on 2/15/17.
 */

abstract class BaseProperty<T> {
    private Context context;

    BaseProperty(Context context) {
        this.context = context;
    }

    protected final Context getContext() {
        return this.context;
    }

    public abstract void save(T value);

}
