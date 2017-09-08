'''
Created on Aug 24, 2017

@author: nhamid
'''

from jsonpath_rw import parse
import urllib.parse
from zipfile import ZipFile, BadZipfile
import random
import json
import io

import cacher as C
import util as U
import describe as D

import plugin_csv
import plugin_json
import plugin_satori
import plugin_xml



class DataSource:
    '''
    classdocs
    '''
    
    __predefined_plugins = [ { "name" : "JSON (built-in)", 
                               "type-ext" : "json",
                               "data-infer" : plugin_json.JSON_Infer(),
                               "data-factory" : plugin_json.JSON_Data_Factory },
                            { "name" : "XML (lxml)", 
                               "type-ext" : "xml",
                               "data-infer" : plugin_xml.XML_Infer(),
                               "data-factory" : plugin_xml.XML_Data_Factory },
                            { "name" : "CSV (built-in)",
                               "type-ext" : "csv",
                               "data-infer" : plugin_csv.CSV_Infer(),
                               "data-factory" : plugin_csv.CSV_Data_Factory },
                            { "name" : "TSV (built-in)",
                               "type-ext" : "tsv",
                               "data-infer" : plugin_csv.CSV_Infer(delim = '\t'),
                               "data-factory" : plugin_csv.CSV_Data_Factory },
                            { "name" : "Satori",
                               "type-ext" : "satori",
                               "data-infer" : plugin_satori.Satori_Infer(),
                               "data-factory" : plugin_satori.Satori_Data_Factory}
                        ]
    
    plugins = __predefined_plugins
    
    
    @staticmethod
    def connect(path, format = None):
        if format is None:  # infer it...
            for p in DataSource.plugins:
                if p["data-infer"].matched_by(path):
                    return DataSource(path, path, p["type-ext"], p)
            raise ValueError('could not infer data format for {}'.format(path))
        else:
            type_ext = format.lower()
            for p in DataSource.plugins:
                if p["type-ext"] == type_ext:
                    return DataSource(path, path, type_ext, p)
    
            raise ValueError("no data source plugin for type {}".format(type_ext))
            
        
    
    @staticmethod
    def connect_as(type_ext, path):
        return DataSource.connect(path, format = type_ext)
        
    
    @staticmethod
    def connect_load(path, format = None):
        ds = DataSource.connect(path, format = format)
        return ds.load()
        
        
    def clear_cache(self):
        return self.cacher.clearCache()


    def __init__(self, name, path, typeExt, plugin):
        '''
        (usual use is to call DataSource.connect() to instantiate objects of
         this class) 
        '''
        
        self.name = name
        self.path = path
        self.format_type = typeExt
        
        self.__connected = path and True
        self.__load_ready = False
        self.__loaded = False
        
        self.__random_index = None   # this is so that .fetch_random() actually returns the same position, until .load() is called again
        
        self.data_infer = plugin["data-infer"]
        self.data_factory = plugin["data-factory"]()
        self.data_obj = None
        self.cacher = C.defaultCacher()
        
        self.param_values = {}
        self.option_settings = {}



    def set_param(self, name, value):
        self.param_values[name] = value
        return self
        
    def load_fresh_sample(self, max_elts = 25):
        return self.load_sample(max_elts = max_elts, force_reload = True)
    
    def load_sample(self, max_elts = 25, force_reload = False):
        #
        # look for cache subtag:   "sample:<max-elts>" in the cache
        # if  not there, or if stale, or if force_reload is True:
        #     load()
        #     sample the loaded data
        #     cache the sample (serialized as json)
        #     return the sample
        # otherwise
        #     load the cached sample (unserialize as json)
        #     return it
        #

        if not self.__connected: raise ValueError("not __connected {}".format(self.path))
        if not self.__ready_to_load(): raise ValueError("not ready to load; missing params...")
        
        full_path = self.get_full_path_url()
        
        if 'file-entry' in self.option_settings:
            fe_value = self.option_settings['file-entry']
            subtag = "sample:{}-{}".format(fe_value, max_elts)
        else:
            subtag = "sample:{}".format(max_elts)
            
        sample_path = self.cacher.resolvePath(full_path, subtag)
        if not sample_path or force_reload:
            self.load(force_reload = force_reload)
            if self.__loaded:
                sampled = self.sample_data(self.data_obj, max_elts)
                fp = io.BytesIO(json.dumps(sampled).encode()) 
                self.cacher.add_to_cache(full_path, subtag, fp)
                self.data_obj = sampled
        else: # sample seems to be cached
            fp = U.create_input(sample_path) 
            self.data_obj = json.loads(fp.read().decode())
            self.__loaded = True    # copy these two lines because .load() didn't get called on this path of execution
            self.__random_index = None   # this is so that .fetch_random() actually returns the same position, until .load() is called again
        
        return self
    

    def load(self, force_reload = False):
        if not self.__connected: raise ValueError("not __connected {}".format(self.path))
        if not self.__ready_to_load(): raise ValueError("not ready to load; missing params...")
        
        subtag = "main"
    
        full_path = self.get_full_path_url()
    
        if self.__loaded and \
                not self.cacher.is_stale(full_path, subtag) and \
                not force_reload:
            self.__random_index = None   # this is so that .fetch_random() actually returns the same position, until .load() is called again
            return self
        
        resolved_path = self.cacher.resolvePath(full_path, subtag)
        fp = U.create_input(resolved_path)
        
        #print("Full path: {} {}".format(full_path, U.smellsLikeZip(full_path)))
        if U.smellsLikeZip(full_path) and not U.smellsLikeURL(resolved_path):
            try:
                zf = ZipFile(resolved_path)
                members = zf.namelist()
                
                if 'file-entry' not in self.option_settings and len(members) is 1:
                    #print("Selecting file-entry from zip: {}".format(members[0]))
                    self.option_settings['file-entry'] = members[0]
                
                if 'file-entry' in self.option_settings and \
                        self.option_settings['file-entry'] in members:
                    
                    fe_value = self.option_settings['file-entry']
                    fe_subtag = "file-entry:{}".format(fe_value)
                    
                    entry_cached_path = self.cacher.resolvePath(full_path, fe_subtag)
                    if entry_cached_path:
                        fp = U.create_input(entry_cached_path)                        
                    else: # not in the cache
                        fp = zf.open(fe_value)
                        if not self.cacher.add_to_cache(full_path, fe_subtag, fp):
                            print("something went wrong caching zip file-entry")
                            fp = zf.open(fe_value)
                        else:
                            entry_cached_path = self.cacher.resolvePath(full_path, fe_subtag)
                            fp = U.create_input(entry_cached_path)

                else:
                    raise ValueError("Specify a file-entry from the ZIP file: {}".format(members))
            
            except BadZipfile:
                print("ZIP Failed: " + full_path)
        
        if not self.data_infer.matched_by(self.path):  # because options is only valid after matchedBy has been invoked
            self.data_infer.matched_by(full_path) 
        for k, v in self.data_infer.options.items():
            self.data_factory.set_option(k, v)
        self.data_obj = self.data_factory.load_data(fp)
        
        self.__loaded = True
        self.__random_index = None   # this is so that .fetch_random() actually returns the same position, until .load() is called again
        return self
    

    def has_data(self):
        return self.__connected and self.__loaded


    def fetch_all(self):
        if not self.has_data():
            raise ValueError("no data available - make sure you called load()")
            
        return self.data_obj


    def patch_jsonpath_path(self, pth, data):
        pth = pth.replace("/", ".")
        splits = pth.split(".")
        if not splits:
            return pth
        
        if isinstance(data, list):
            fixed_path = "$[*]"
        else:
            fixed_path = ""
        for piece in splits:
            if fixed_path: fixed_path = fixed_path + "."
            fixed_path = fixed_path + piece
            #print("checking " + fixed_path + " " + str(data))
            selected = parse(fixed_path).find(data)
            if len(selected) == 1 and type(selected[0].value) == list and \
                    len(selected[0].value) > 1 and not fixed_path.endswith("]"):
                #print("adding *: {} b/c {}".format(fixed_path, str(selected[0].value)))
                fixed_path = fixed_path + "[*]"
                
        return fixed_path         

    def fetch_random(self, *field_paths, base_path = None):
        return self.__post_process(self.fetch(*field_paths, base_path = base_path, select = "random"), *field_paths)
    
    def fetch_first(self, *field_paths, base_path = None):
        return self.fetch_ith(0, *field_paths, base_path = base_path)

    def fetch_second(self, *field_paths, base_path = None):
        return self.fetch_ith(1, *field_paths, base_path = base_path)

    def fetch_third(self, *field_paths, base_path = None):
        return self.fetch_ith(2, *field_paths, base_path = base_path)

    def fetch_ith(self, i, *field_paths, base_path = None):
        return self.__post_process(self.fetch(*field_paths, base_path = base_path, select = i), *field_paths)
    
    
    
    def fetch_float(self, field_path, select = None):
        stuff = self.fetch(field_path, select = select)
        if isinstance(stuff, list):
            return [float(v) for v in stuff]
        else:
            return float(stuff)

    def fetch_first_float(self, field_path):
        return self.fetch_float(field_path, select=0)
    
    def fetch_ith_float(self, i, field_path):
        return self.fetch_float(field_path, select=i)

    def fetch_random_float(self, field_path):
        return self.fetch_float(field_path, select="random")

    
    def fetch_int(self, field_path, select = None):
        stuff = self.fetch(field_path, select = select)
        if isinstance(stuff, list):
            return [int(v) for v in stuff]
        else:
            return int(stuff)

    def fetch_first_int(self, field_path):
        return self.fetch_int(field_path, select=0)
    
    def fetch_ith_int(self, i, field_path):
        return self.fetch_int(field_path, select=i)

    def fetch_random_int(self, field_path):
        return self.fetch_int(field_path, select="random")
    
    
    
    def fetch(self, *field_paths, base_path = None, select = None):
        data = self.fetch_all()
        if len(field_paths) is 0:
            collected = data
        else:
            collected = []
    
            if base_path:      
                base_path = self.patch_jsonpath_path(base_path, data)
                data = parse("$[*]." + base_path).find(data)
            elif not isinstance(data, list):
                    data = [data]
            
            parsed_paths = None
            field_names = None
    
            for match in data:
                if parsed_paths is None:
                    parsed_paths = []
                    field_names = []
                    for field_path in field_paths:
                        field_path = self.patch_jsonpath_path(field_path, match)
                        field_name = field_path.split(".")[-1]    # TODO: could end up with [...] at end of field names
                        parsed_paths.append(parse(field_path))
                        field_names.append(field_name)
                
                d = {}
                for fp, fn in zip(parsed_paths, field_names):
                    fv = fp.find(match)
                    if not fv or fv == []:
                        raise ValueError("No data found for field: {}".format(fn))
                    elif len(fv) == 1:
                        fv_result = fv[0].value
                    else:
                        fv_result = [v.value for v in fv]
                        
                    if len(parsed_paths) == 1: d = fv_result
                    else: d[fn] = fv_result
                        
                collected.append(d)
            
            if len(collected) == 1:
                only_one = collected[0]
                if len(field_paths) == 1 and field_names[0] in only_one:
                    collected = only_one[field_names[0]]
                else:
                    collected = only_one
            
        if select and isinstance(select, str) and select.lower() == 'random' and isinstance(collected, list):
            if not self.__random_index:
                self.__random_index = random.randrange(len(collected))
            select = self.__random_index
        
        if type(select) == int and isinstance(collected, list):
            return collected[select]
        else:
            return collected
        
        
    def __post_process(self, result, *field_paths):
        if isinstance(result, dict) and len(result.keys()) == 1:  # unwrap singleton dictionary fields
            for first_key in result: break
            return result[first_key]           
        else:
            return result


    def field_list(self, base_path = None):
        if not self.has_data():
            raise ValueError("no data available - make sure you called load()")

        if not base_path:
            data = self.fetch()
        else:
            data = self.fetch(base_path)
            
        while isinstance(data, list) and len(data) > 0:
            data = data[0]
            
        if isinstance(data, dict):
            return [k for k in data]
        else:
            return []
        
    def data_length(self, base_path = None):
        if not self.has_data():
            raise ValueError("no data available - make sure you called load()")

        if not base_path:
            data = self.fetch()
        else:
            data = self.fetch(base_path)

        if isinstance(data, list):
            return len(data)
        else:
            return 0


    def sample_data(self, obj, max_elts):
        if isinstance(obj, list):
            if len(obj) > max_elts:  # need to sample down
                obj = [ obj[i] for i in sorted(random.sample(range(len(obj)), max_elts)) ]
            
            obj = [self.sample_data(v, max_elts) for v in obj]  # sample inner data
        elif isinstance(obj, dict):
            obj = { k : self.sample_data(v, max_elts) for k, v in obj.items() }
                
        return obj


    def description(self):
        if not self.has_data():
            raise ValueError("no data available - make sure you called load()")
        
        return D.describe(self.data_obj)
    
    def print_description(self):
        print("-----")
        print("Data Source: {}\n".format(self.get_full_path_url()))
        print("The following data is available:")        
        print(self.description())    

    def set_cache_timeout(self, value):
        ''' set the cache delay to the given value in seconds '''
        self.cacher = self.cacher.updateTimeout(value * 1000)
        return self

    def cache_directory(self):
        return self.cacher.cache_directory
    
    def set_option(self, name, value):
        if name.lower() == "file-entry":
            self.option_settings['file-entry'] = value
        else:
            self.data_factory.set_option(name, value)
    
    def set_options(self, opts):
        for k in opts:
            self.set_option(k, opts[k])


    def get_full_path_url(self):
        if not self.__ready_to_load():
            raise ValueError("Cannot finalize path: not ready to load")
        
        full_path = self.path
        
        # TODO:  sort params so the URLs are not different all the time (causing cache reloads) 
        
        params = urllib.parse.urlencode(self.param_values)
        if params:
            full_path = full_path + "?" + params
        
        return full_path


    def __ready_to_load(self):
        # TODO...
        self.__load_ready = self.__load_ready # or  missingParams().size()==0;
        
        self.__load_ready = True
        return self.__load_ready;




'''

   A <data-plugin> is a dictionary:
    {  "name" : string,
       "type-ext" : string,
       "data-infer" : <data infer object>,
       "data-factory" : <data access factory object> }
       
       
    A <data infer object> has one method:
        boolean matched_by(String path)
            path: the primary path (URL/file name) to the data
    and one field: 
        options: dict  { String : String, ... }
        
        
    A <data access factory object> has methods:
        
    
        set_option(name, value)
    
        <data object>  load_data(fp)
            fp : a file object (binary mode)
            returns a dict-like thing with a 
            
    <data object> = a dict-like thing with 
        get schema
        ability to produce an actual dict (possibly pruned from the entire available data)
        

'''