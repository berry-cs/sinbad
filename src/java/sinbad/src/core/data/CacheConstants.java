package core.data;

import core.cache.DataCacher;

// Constants utility class

public final class CacheConstants {
    public static final int NEVER_CACHE = 0;
    public static final int NEVER_RELOAD = -1;

    private CacheConstants() { } // prevents instantiation
    
    public static final String DEFAULT_CACHE_DIR = DataCacher.getDefaultCacheDir();

}
