'''
Created on Aug 23, 2017

@author: nhamid
'''

import hashlib
import time
import urllib.request



def hash_string(str):
    '''produces a string'''
    h = hashlib.sha1(str.encode('utf_8')).hexdigest()
    return h[:25]
    
    
def current_time_millis():
    return int(time.time() * 1000)


def smellsLikeURL(path):
    return path.find("://") >= 0

def smellsLikeZip(path):
    return path.find(".zip") >= 0
    
    
def create_input(path, options = {}):
    # for now, but eventually options may including extract a file from a zip archive, etc.
    fp, newpath = create_input_raw(path)
    return fp
    
    
def create_input_raw(path):
    ''' path is either a URL or local path
       returns a tuple of file input stream and the actual filename
       
       the filename could be different from path if a redirect from url happened,
       or if the file came in as an attachment in the http response 
       or if the path was just adjusted in some way 
       
       returns a binary input stream
       '''
    if not path:
        return None
    
    
    if smellsLikeURL(path):
        file = urllib.request.urlopen(path)
    # TODO: this should return the encoding as well.... or None if unknown/unspecified (assume utf-8) 
    else:
        file = open(path, 'rb')  # binary to be consistent with urlopen 

    return (file, path)
    
