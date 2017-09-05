'''
Created on Aug 22, 2017

@author: nhamid
'''


import json
import os.path
import tempfile
import shutil

import util as U


__sinbadCacheEnabled = True

# public constants
NEVER_CACHE = 0
NEVER_RELOAD = -1

# private constants
__DEFAULT_CACHE_DIR__ = os.path.join(tempfile.gettempdir(), "sinbad_cache")
__MINIMUM_TIMEOUT_VALUE__ = 1000 # milliseconds



def setCaching(onOff):
    global __sinbadCacheEnabled
    __sinbadCacheEnabled = onOff
    
def isCaching():
    global __sinbadCacheEnabled
    return __sinbadCacheEnabled




class Cacher:

    def is_stale(self, path, subtag):
        if not (isCaching() and self.__isCacheable(path, subtag)): 
            return True
        
        cache_index_name = self.__getCacheIndexFile(path)
        if not cache_index_name: 
            return True
        
        entry = self.cache_entry_for(path, subtag)
        if not entry or is_expired(entry, self.cache_expiration): 
            return True
        
        return False

    
    def clear_cache_data(self, tag, subtag):
        entry = self.cache_entry_for(tag, subtag)
        if not entry: return False
        if data_valid(entry):
            try: os.remove(entry["cachedata"])
            except OSError: pass
        return self.remove_entry(entry)
    
    def cache_entry_for(self, tag, subtag):
        ''' returns a cache entry (dictionary) object '''
        cel = self.__read_cache_entry_list(self.__getCacheIndexFile(tag))

        for e in cel:
            if (e["tag"] == tag and 
                e["subtag"] == subtag):
                return e
            
        return None

    def update_entry(self, entry):
        cache_index_name = self.__getCacheIndexFile(entry["tag"])
        cel = self.__read_cache_entry_list(cache_index_name)
        
        is_new = True
        for i, e in enumerate(cel):
            if (tags_match(e, entry)):
                cel[i] = entry
                is_new = False
        if is_new:
            cel.append(entry)
            
        self.__write_cache_entry_list(cache_index_name, cel)
            
    
    def remove_entry(self, entry):
        cache_index_name = self.__getCacheIndexFile(entry["tag"])
        cel = self.__read_cache_entry_list(cache_index_name)
        leftover_cel = [e for e in cel if not tags_match(e, entry)]
        self.__write_cache_entry_list(cache_index_name, leftover_cel)
        return True
    
    def read_and_cache(self, path):
        try:
            fp = U.create_input(path)
                               #charset = 'utf-8' if not hasattr(fp, 'headers') else fp.headers.get_content_charset('utf-8')
            data = fp.read()   #.decode(charset)
        except OSError:  ### ????
            raise FileNotFoundError("Failed to load: " + path + "\nCHECK NETWORK CONNECTION, if applicable") 
        
        cached_file = self.cacheByteData(path, data)
        return cached_file
    

    def resolvePath(self, path, subtag):
        # first make sure caching is enabled and that the path is not a local file
        if not (isCaching() and self.__isCacheable(path, subtag)):
            return path
        
        cacheIndexName = self.__getCacheIndexFile(path)
        if cacheIndexName is None: return path
        
        #print(cacheIndexName)
        entry = self.cache_entry_for(path, subtag)
        if entry and not data_valid(entry):
            self.clear_cache_data(path, subtag)
            entry = None
            
        cache_path = entry["cachedata"] if entry else None
        
        if (not cache_path) or \
                (entry and is_expired(entry, self.cache_expiration)):
            print("Refreshing cache for: " + path + " (" + subtag + ")")
            cached_file_path = self.read_and_cache(path)
            if cache_path:   # need to remove the old cached file
                os.remove(cache_path)
            
            entry = make_entry(path, subtag, cached_file_path, U.current_time_millis())
            
            self.update_entry(entry)
            return cached_file_path
        
        #print("Using previously data cached for " + path + " (" + subtag + ")")
        return cache_path
    
    

    def updateDirectory(self, newdir):
        new_cacher = Cacher()
        new_cacher.cache_directory = newdir
        new_cacher.cache_expiration = self.cache_expiration
        return new_cacher
    
    
    def updateTimeout(self, value):
        global __MINIMUM_TIMEOUT_VALUE__
        if (value > 0 and value < __MINIMUM_TIMEOUT_VALUE__):
            print("Warning: cannot set cache timeout less than " + str(__MINIMUM_TIMEOUT_VALUE__) + " msec.")
            value = __MINIMUM_TIMEOUT_VALUE__
            
        new_cacher = Cacher()
        new_cacher.cache_directory = self.cache_directory
        new_cacher.cache_expiration = value
        return new_cacher
    
    
    def clearCache(self):
        shutil.rmtree(self.cache_directory)
    
    
    
    ### below here are generally 'private'ish functions...
        
    
    def cacheStringData(self, tag, data):
        ''' tag is used to determine the subdirectory in which a temp
            file is created, into which the data (a string) is stored
           
           a new temporary file is created each time this function is called
           the function returns the path to the newly-created file 
        '''
        
        return self.cacheByteData(tag, data, 'utf-8')
        


    def cacheByteData(self, tag, data, encoding=None):
        ''' tag is used to determine the subdirectory in which a temp
            file is created, into which the data (bytes) is stored
           
           a new temporary file is created each time this function is called
           the function returns the path to the newly-created file 
        '''
                
        if encoding:
            stuff = data.encode(encoding)
        else:
            stuff = data
                
        cacheDir = os.path.join(self.cache_directory, self.__cacheSubdirName(tag))
        os.makedirs(cacheDir, 0o777, True)
        
        fd, temp_path = tempfile.mkstemp(".dat", "cache", cacheDir)
        os.write(fd, stuff)
        os.close(fd)
        
        return temp_path


    
    def __isCacheable(self, path, subtag):
        global NEVER_CACHE
        # if it's something other than "main" data being accessed (e.g. "schema")
        #   then it can be in the cache, even if the main is a local file
        # otherwise, if it's the "main" data we're considering, then only cache
        #  if it smells like a URL and the cacher specific setting is not set 
        #  to never cache
        return (not subtag.startswith("main")) or \
                U.smellsLikeURL(path) and self.cache_expiration != NEVER_CACHE
    
    
    def __getCacheIndexFile(self, tag):
        if not os.path.exists(self.cache_directory):
            os.makedirs(self.cache_directory, 0o777, True)
        if not os.path.isdir(self.cache_directory):
            return None

        cacheIndexFile = os.path.join(self.cache_directory, self.__cacheIndexName(tag) )
        
        if not os.path.exists(cacheIndexFile):
            self.__write_cache_entry_list(cacheIndexFile, [])
        
        if not os.path.isfile(cacheIndexFile):
            return None
        
        return os.path.abspath( cacheIndexFile )
    
    
    def __cacheIndexName(self, tag):
        return "idx" + str(U.hash_string(tag)) + ".json"
    
    
    def __cacheSubdirName(self, tag):
        return "dat" + str(U.hash_string(tag))

            
    def __read_cache_entry_list(self, cache_index_name):
        try:
            with open(cache_index_name, 'r') as fp:
                return json.load(fp)
        except OSError:
            return []
        
        
    def __write_cache_entry_list(self, cache_index_name, cel):
        try:
            with open(cache_index_name, 'w') as fp:
                json.dump(cel, fp)
        except OSError:
            return False
        return True
            
    
    def __init__(self):
        ''' This constructor is 'private' '''
        global __DEFAULT_CACHE_DIR__
        self.cache_directory = __DEFAULT_CACHE_DIR__
        self.cache_expiration = NEVER_RELOAD 
        
        
        
        

## cache entries are a dictionary:
##    { "tag" : ..., "subtag" : ..., "cachedata" : ..., "timestamp" : ... }
##   (cachedata) is a local file path
def make_entry(tag, subtag, cachedata, timestamp):
    return { "tag" : tag, "subtag" : subtag, "cachedata" : cachedata, "timestamp" : timestamp }

def data_valid(entry):
    ''' checks to see if the entry's cachedata refers to an actual readable file '''
    return entry["cachedata"] and \
            os.path.isfile(entry["cachedata"]) and \
            U.create_input(entry["cachedata"]) is not None

def is_expired(entry, expiration):
    diff = U.current_time_millis() - entry["timestamp"]
    return expiration >= 0 and diff > expiration
   
def tags_match(e1, e2):
    return e1["tag"] == e2["tag"] and e1["subtag"] == e2["subtag"]
        
        
        
### other functions ...


### singleton object
    
__DEFAULT_CACHER = Cacher()

def defaultCacher():
    global __DEFAULT_CACHER
    return __DEFAULT_CACHER
    

if __name__ == '__main__':
    c = defaultCacher()   #.updateTimeout(1000000)
    pth = c.resolvePath("http://bigscreen.com/xml/nowshowing_new.xml", "main")
    print("Resolved to: " + pth)
    #c.clear_cache_data("http://bigscreen.com/xml/nowshowing_new.xml", "main")
    