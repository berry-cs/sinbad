'''
Created on Aug 22, 2017

@author: nhamid
'''
import unittest
import sinbad.cacher as C
import os.path

class Test(unittest.TestCase):


    def setUp(self):
        self.cacher = C.Cacher()


    def tearDown(self):
        pass

    
    def testCaching(self):
        assert C.isCaching()
        C.setCaching(False)
        assert not C.isCaching()
        C.setCaching(True)
        assert C.isCaching()


    def testCacheDir(self):
        print(self.cacher.cacheDirectory)
        assert self.cacher.cacheDirectory.endswith("sinbad_cache")
        
        
    def testUpdateDir(self):
        orig = self.cacher.cacheDirectory
        origTO = self.cacher.cacheExpiration
        c2 = self.cacher.updateDirectory("~/Desktop/sinbad_stuff")
        assert self.cacher.cacheDirectory == orig
        assert c2.cacheDirectory == "~/Desktop/sinbad_stuff"
        assert self.cacher.cacheExpiration == c2.cacheExpiration
        assert self.cacher.cacheExpiration == origTO


    def testUpdateTimeout(self):
        origDir = self.cacher.cacheDirectory
        origTO = self.cacher.cacheExpiration
        c2 = self.cacher.updateTimeout(3)
        assert c2.cacheExpiration == origTO 
        assert c2.cacheDirectory == origDir
        
        c2 = self.cacher.updateTimeout(156789)
        assert c2.cacheExpiration == 156789 
        assert c2.cacheDirectory == origDir
        assert self.cacher.cacheExpiration == origTO 
        assert self.cacher.cacheDirectory == origDir
        
        
    def testDefaultCacher(self):
        assert C.defaultCacher().cacheDirectory.endswith("sinbad_cache")
        assert C.defaultCacher().cacheExpiration == C.__MINIMUM_TIMEOUT_VALUE__


    def testResolveNoCache(self):
        assert self.cacher.resolvePath("../home.txt", "main") == "../home.txt"
        assert self.cacher.resolvePath("http://cs.berry.edu/something", "main") != "http://cs.berry.edu/something"
        C.setCaching(False)
        assert self.cacher.resolvePath("http://cs.berry.edu/something", "main") == "http://cs.berry.edu/something"
        C.setCaching(True)
        
        
    def testReadAndCaching(self):
        self.cacher.resolvePath("http://bigscreen.com/xml/nowshowing_new.xml", "main")
        x = self.cacher.cacheStringData("http://cs.berry.edu/something", "blahblahblah")
        print("cached to: " + x)
        
        
    def NOTtestCreateInput(self):
        fp, pth = C.createInputRaw("http://bigscreen.com/xml/nowshowing_new.xml")
        assert pth == "http://bigscreen.com/xml/nowshowing_new.xml"
        if hasattr(fp, "headers"):
            print(type(fp.headers))
            print(fp.headers.get_content_charset('utf-8'))
        print(fp.read())
        fp.close()

if __name__ == "__main__":
    #import sys;sys.argv = ['', 'Test.testName']
    #unittest.main()
    C.Cacher().clearCache()

    